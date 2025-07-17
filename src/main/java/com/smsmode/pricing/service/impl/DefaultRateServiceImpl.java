/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.service.impl;

import com.smsmode.pricing.dao.service.DefaultRateDaoService;
import com.smsmode.pricing.mapper.DefaultRateMapper;
import com.smsmode.pricing.model.AdditionalGuestFeeModel;
import com.smsmode.pricing.model.DaySpecificRateModel;
import com.smsmode.pricing.model.DefaultRateModel;
import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeePostResource;
import com.smsmode.pricing.resource.common.dayspecificrate.DaySpecificRatePostResource;
import com.smsmode.pricing.resource.defaultrate.DefaultRateGetResource;
import com.smsmode.pricing.resource.defaultrate.DefaultRatePostResource;
import com.smsmode.pricing.service.DefaultRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of DefaultRateService with proper cascade update handling.
 * Preserves UUIDs of existing entities during updates.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DefaultRateServiceImpl implements DefaultRateService {

    private final DefaultRateMapper defaultRateMapper;
    private final DefaultRateDaoService defaultRateDaoService;

    @Override
    public ResponseEntity<DefaultRateGetResource> create(DefaultRatePostResource defaultRatePostResource) {
        log.debug("Creating default rate for unit: {}", defaultRatePostResource.getUnit().getUuid());

        DefaultRateModel defaultRateModel = defaultRateMapper.postResourceToModel(defaultRatePostResource);

        if (defaultRateModel.getAdditionalGuestFees() != null) {
            for (AdditionalGuestFeeModel fee : defaultRateModel.getAdditionalGuestFees()) {
                fee.setRate(defaultRateModel);
            }
        }

        if (defaultRateModel.getDaySpecificRates() != null) {
            for (DaySpecificRateModel dayRate : defaultRateModel.getDaySpecificRates()) {
                dayRate.setRate(defaultRateModel);
            }
        }

        defaultRateModel = defaultRateDaoService.save(defaultRateModel);
        log.info("Successfully created default rate with ID: {}, additionalGuestFees count: {}, daySpecificRates count: {}",
                defaultRateModel.getId(),
                defaultRateModel.getAdditionalGuestFees().size(),
                defaultRateModel.getDaySpecificRates().size());

        DefaultRateGetResource response = defaultRateMapper.modelToGetResource(defaultRateModel);

        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<DefaultRateGetResource> getByUnitId(String unitId) {
        log.debug("Retrieving default rate for unit: {}", unitId);

        Page<DefaultRateModel> defaultRateModelPage = defaultRateDaoService.findByUnitId(unitId, Pageable.unpaged());
        log.info("Retrieved {} default rates from database", defaultRateModelPage.getTotalElements());

        if (defaultRateModelPage.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DefaultRateModel defaultRateModel = defaultRateModelPage.getContent().get(0);
        DefaultRateGetResource defaultRateGetResource = defaultRateMapper.modelToGetResource(defaultRateModel);

        return ResponseEntity.ok(defaultRateGetResource);
    }

    @Override
    public ResponseEntity<DefaultRateGetResource> update(String rateId, DefaultRatePostResource defaultRatePostResource) {
        log.debug("Updating default rate with ID: {}", rateId);

        // Find existing default rate
        DefaultRateModel existingDefaultRate = defaultRateDaoService.findById(rateId);
        log.debug("Found existing default rate: {}", existingDefaultRate.getId());

        defaultRateMapper.updateModelFromPostResource(defaultRatePostResource, existingDefaultRate);

        // Update Additional Guest Fees with UUID preservation
        updateAdditionalGuestFees(existingDefaultRate, defaultRatePostResource.getAdditionalGuestFees());

        // Update Day Specific Rates with UUID preservation
        updateDaySpecificRates(existingDefaultRate, defaultRatePostResource.getDaySpecificRates());

        log.debug("Updated all fields and collections");

        // Save updated model
        DefaultRateModel updatedDefaultRate = defaultRateDaoService.save(existingDefaultRate);
        log.info("Successfully updated default rate with ID: {}", rateId);

        // Map to response
        DefaultRateGetResource response = defaultRateMapper.modelToGetResource(updatedDefaultRate);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates Additional Guest Fees with 3 scenarios:
     * 1. No ID in request -> CREATE new entity
     * 2. ID provided in request -> UPDATE existing entity (preserve UUID)
     * 3. Existing entity not in request -> DELETE entity
     */
    private void updateAdditionalGuestFees(DefaultRateModel existingRate, List<AdditionalGuestFeePostResource> newFees) {
        log.debug("Updating additional guest fees");

        List<AdditionalGuestFeeModel> existingFees = existingRate.getAdditionalGuestFees();

        // If newFees is null, remove all existing fees
        if (CollectionUtils.isEmpty(newFees)) {
            log.debug("No fees in request, removing all existing fees");
            existingFees.clear();
            return;
        }

        // Créer une map des entités existantes par ID pour lookup rapide
        Map<String, AdditionalGuestFeeModel> existingById = existingFees.stream()
                .collect(Collectors.toMap(
                        AdditionalGuestFeeModel::getId,
                        fee -> fee
                ));

        // Créer une nouvelle collection pour les fees mises à jour
        List<AdditionalGuestFeeModel> updatedFees = new ArrayList<>();

        // Traiter chaque fee dans la requête
        for (AdditionalGuestFeePostResource feeResource : newFees) {
            if (StringUtils.hasText(feeResource.getId())) {
                // Scénario 2: ID fourni = UPDATE entité existante
                AdditionalGuestFeeModel existingFee = existingById.get(feeResource.getId());
                if (existingFee != null) {
                    log.debug("Updating existing fee with ID: {}", existingFee.getId());

                    // ✅ Utiliser MapStruct pour mapper les nouvelles valeurs
                    defaultRateMapper.updateAdditionalGuestFeeFromResource(feeResource, existingFee);

                    updatedFees.add(existingFee);
                } else {
                    log.warn("Fee with ID {} not found, creating new one", feeResource.getId());
                    // ID fourni mais entité introuvable = créer nouvelle entité
                    AdditionalGuestFeeModel newFee = defaultRateMapper.additionalGuestFeePostResourceToModel(feeResource);
                    newFee.setRate(existingRate);
                    updatedFees.add(newFee);
                }
            } else {
                // Scénario 1: Pas d'ID = CREATE nouvelle entité
                log.debug("Creating new fee for guestType: {}", feeResource.getGuestType());
                AdditionalGuestFeeModel newFee = defaultRateMapper.additionalGuestFeePostResourceToModel(feeResource);
                newFee.setRate(existingRate);
                updatedFees.add(newFee);
            }
        }

        // Remplacer la collection existante par la nouvelle
        existingFees.clear();
        existingFees.addAll(updatedFees);

        log.debug("After update - {} fees remaining", existingFees.size());
    }

    /**
     * Updates Day Specific Rates with 3 scenarios:
     * 1. No ID in request -> CREATE new entity
     * 2. ID provided in request -> UPDATE existing entity (preserve UUID)
     * 3. Existing entity not in request -> DELETE entity
     */
    private void updateDaySpecificRates(DefaultRateModel existingRate, List<DaySpecificRatePostResource> newRates) {
        log.debug("Updating day specific rates");

        List<DaySpecificRateModel> existingRates = existingRate.getDaySpecificRates();

        if (CollectionUtils.isEmpty(newRates)) {
            log.debug("No day rates in request, removing all existing rates");
            existingRates.clear();
            return;
        }

        Map<String, DaySpecificRateModel> existingById = existingRates.stream()
                .collect(Collectors.toMap(
                        DaySpecificRateModel::getId,
                        rate -> rate
                ));

        List<DaySpecificRateModel> updatedRates = new ArrayList<>();

        for (DaySpecificRatePostResource rateResource : newRates) {
            if (StringUtils.hasText(rateResource.getId())) {
                DaySpecificRateModel existingRateModel = existingById.get(rateResource.getId());
                if (existingRateModel != null) {
                    log.debug("Updating existing day rate with ID: {}", existingRateModel.getId());

                    // ✅ Utiliser MapStruct pour mapper les nouvelles valeurs
                    defaultRateMapper.updateDaySpecificRateFromResource(rateResource, existingRateModel);

                    updatedRates.add(existingRateModel);
                } else {
                    log.warn("Day rate with ID {} not found, creating new one", rateResource.getId());
                    DaySpecificRateModel newRate = defaultRateMapper.daySpecificRatePostResourceToModel(rateResource);
                    newRate.setRate(existingRate);
                    updatedRates.add(newRate);
                }
            } else {
                log.debug("Creating new day rate for nightly: {}", rateResource.getNightly());
                DaySpecificRateModel newRate = defaultRateMapper.daySpecificRatePostResourceToModel(rateResource);
                newRate.setRate(existingRate);
                updatedRates.add(newRate);
            }
        }

        existingRates.clear();
        existingRates.addAll(updatedRates);

        log.debug("After update - {} day rates remaining", existingRates.size());
    }
}