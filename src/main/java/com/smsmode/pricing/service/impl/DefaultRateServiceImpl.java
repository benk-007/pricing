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

        // Create the main entity
        DefaultRateModel defaultRateModel = new DefaultRateModel();
        defaultRateModel.setNightly(defaultRatePostResource.getNightly());
        defaultRateModel.setMinStay(defaultRatePostResource.getMinStay());
        defaultRateModel.setMaxStay(defaultRatePostResource.getMaxStay());
        defaultRateModel.setUnit(defaultRatePostResource.getUnit());

        // Handle Additional Guest Fees ONLY if provided and not empty
        if (!CollectionUtils.isEmpty(defaultRatePostResource.getAdditionalGuestFees())) {
            log.debug("Processing {} additional guest fees", defaultRatePostResource.getAdditionalGuestFees().size());
            for (AdditionalGuestFeePostResource feeResource : defaultRatePostResource.getAdditionalGuestFees()) {
                AdditionalGuestFeeModel feeModel = defaultRateMapper.additionalGuestFeePostResourceToModel(feeResource);
                defaultRateModel.addAdditionalGuestFee(feeModel);
                log.debug("Added additional guest fee: guestType={}, value={}", feeModel.getGuestType(), feeModel.getValue());
            }
        } else {
            log.debug("No additional guest fees provided");
        }

        // Handle Day Specific Rates ONLY if provided and not empty
        if (!CollectionUtils.isEmpty(defaultRatePostResource.getDaySpecificRates())) {
            log.debug("Processing {} day specific rates", defaultRatePostResource.getDaySpecificRates().size());
            for (DaySpecificRatePostResource rateResource : defaultRatePostResource.getDaySpecificRates()) {
                DaySpecificRateModel rateModel = defaultRateMapper.daySpecificRatePostResourceToModel(rateResource);
                defaultRateModel.addDaySpecificRate(rateModel);
                log.debug("Added day specific rate: nightly={}, days={}", rateModel.getNightly(), rateModel.getDays());
            }
        } else {
            log.debug("No day specific rates provided");
        }

        // Save the entity
        defaultRateModel = defaultRateDaoService.save(defaultRateModel);
        log.info("Successfully created default rate with ID: {}, additionalGuestFees count: {}, daySpecificRates count: {}",
                defaultRateModel.getId(),
                defaultRateModel.getAdditionalGuestFees().size(),
                defaultRateModel.getDaySpecificRates().size());

        // Map to response
        DefaultRateGetResource response = defaultRateMapper.modelToGetResource(defaultRateModel);

        // Clean up response: remove empty collections if they weren't in the original request
        if (CollectionUtils.isEmpty(defaultRatePostResource.getAdditionalGuestFees())) {
            response.setAdditionalGuestFees(null);
        }
        if (CollectionUtils.isEmpty(defaultRatePostResource.getDaySpecificRates())) {
            response.setDaySpecificRates(null);
        }

        log.debug("Final response: additionalGuestFees={}, daySpecificRates={}",
                response.getAdditionalGuestFees() != null ? response.getAdditionalGuestFees().size() : "null",
                response.getDaySpecificRates() != null ? response.getDaySpecificRates().size() : "null");

        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<DefaultRateGetResource> getByUnitId(String unitId) {
        log.debug("Retrieving default rate for unit: {}", unitId);

        Page<DefaultRateModel> defaultRateModelPage = defaultRateDaoService.findByUnitId(unitId, Pageable.unpaged());
        log.info("Retrieved {} default rates from database", defaultRateModelPage.getTotalElements());

        // Prendre le premier élément ou retourner 404 si aucun
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

        // Update basic fields
        existingDefaultRate.setNightly(defaultRatePostResource.getNightly());
        existingDefaultRate.setMinStay(defaultRatePostResource.getMinStay());
        existingDefaultRate.setMaxStay(defaultRatePostResource.getMaxStay());
        existingDefaultRate.setUnit(defaultRatePostResource.getUnit());

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
    private void updateAdditionalGuestFees(DefaultRateModel existingRate, Set<AdditionalGuestFeePostResource> newFees) {
        log.debug("Updating additional guest fees");

        Set<AdditionalGuestFeeModel> existingFees = existingRate.getAdditionalGuestFees();

        // If newFees is null or empty, remove all existing fees (scenario 3)
        if (CollectionUtils.isEmpty(newFees)) {
            log.debug("No fees in request, removing all existing fees");
            existingFees.forEach(fee -> fee.setRate(null));
            existingFees.clear();
            return;
        }

        // Create a map of existing entities by ID for quick lookup
        Map<String, AdditionalGuestFeeModel> existingById = existingFees.stream()
                .collect(Collectors.toMap(
                        AdditionalGuestFeeModel::getId,
                        fee -> fee
                ));

        // Set to track which IDs are processed
        Set<String> processedIds = new HashSet<>();

        // Process each fee in the request
        for (AdditionalGuestFeePostResource feeResource : newFees) {
            if (StringUtils.hasText(feeResource.getId())) {
                // Scenario 2: ID provided = UPDATE existing entity
                AdditionalGuestFeeModel existingFee = existingById.get(feeResource.getId());
                if (existingFee != null) {
                    log.debug("Updating existing fee with ID: {} - changing guestType from {} to {}",
                            existingFee.getId(), existingFee.getGuestType(), feeResource.getGuestType());

                    // UPDATE: preserve UUID, modify values
                    existingFee.setGuestCount(feeResource.getGuestCount());
                    existingFee.setGuestType(feeResource.getGuestType());
                    existingFee.setAgeBucket(feeResource.getAgeBucket());
                    existingFee.setAmountType(feeResource.getAmountType());
                    existingFee.setValue(feeResource.getValue());

                    processedIds.add(feeResource.getId());
                } else {
                    log.warn("Fee with ID {} not found in existing fees, will create new one", feeResource.getId());
                    // ID provided but entity not found = create new entity
                    AdditionalGuestFeeModel newFee = defaultRateMapper.additionalGuestFeePostResourceToModel(feeResource);
                    newFee.setRate(existingRate);
                    existingFees.add(newFee);
                }
            } else {
                // Scenario 1: No ID = CREATE new entity
                log.debug("Creating new fee for guestType: {}", feeResource.getGuestType());
                AdditionalGuestFeeModel newFee = defaultRateMapper.additionalGuestFeePostResourceToModel(feeResource);
                newFee.setRate(existingRate);
                existingFees.add(newFee);
                // Ajouter l'ID de la nouvelle entité aux IDs traités pour éviter la suppression
                processedIds.add(newFee.getId());
            }
        }

        // Scenario 3: Remove entities that are no longer in the request
        Iterator<AdditionalGuestFeeModel> iterator = existingFees.iterator();
        while (iterator.hasNext()) {
            AdditionalGuestFeeModel existingFee = iterator.next();
            if (!processedIds.contains(existingFee.getId())) {
                log.debug("Removing fee with ID: {} (not in request)", existingFee.getId());
                existingFee.setRate(null);
                iterator.remove();
            }
        }

        log.debug("After update - {} fees remaining", existingFees.size());
    }

    /**
     * Updates Day Specific Rates with 3 scenarios:
     * 1. No ID in request -> CREATE new entity
     * 2. ID provided in request -> UPDATE existing entity (preserve UUID)
     * 3. Existing entity not in request -> DELETE entity
     */
    private void updateDaySpecificRates(DefaultRateModel existingRate, Set<DaySpecificRatePostResource> newRates) {
        log.debug("Updating day specific rates");

        Set<DaySpecificRateModel> existingRates = existingRate.getDaySpecificRates();

        // If newRates is null or empty, remove all existing rates (scenario 3)
        if (CollectionUtils.isEmpty(newRates)) {
            log.debug("No day rates in request, removing all existing rates");
            existingRates.forEach(rate -> rate.setRate(null));
            existingRates.clear();
            return;
        }

        // Create a map of existing entities by ID
        Map<String, DaySpecificRateModel> existingById = existingRates.stream()
                .collect(Collectors.toMap(
                        DaySpecificRateModel::getId,
                        rate -> rate
                ));

        // Set to track which IDs are processed
        Set<String> processedIds = new HashSet<>();

        // Process each rate in the request
        for (DaySpecificRatePostResource rateResource : newRates) {
            if (StringUtils.hasText(rateResource.getId())) {
                // Scenario 2: ID provided = UPDATE existing entity
                DaySpecificRateModel existingRateModel = existingById.get(rateResource.getId());
                if (existingRateModel != null) {
                    log.debug("Updating existing day rate with ID: {} - changing nightly from {} to {}",
                            existingRateModel.getId(), existingRateModel.getNightly(), rateResource.getNightly());

                    // UPDATE: preserve UUID, modify values
                    existingRateModel.setNightly(rateResource.getNightly());
                    existingRateModel.setDays(rateResource.getDays());

                    processedIds.add(rateResource.getId());
                } else {
                    log.warn("Day rate with ID {} not found, will create new one", rateResource.getId());
                    // ID provided but entity not found = create new entity
                    DaySpecificRateModel newRate = defaultRateMapper.daySpecificRatePostResourceToModel(rateResource);
                    newRate.setRate(existingRate);
                    existingRates.add(newRate);
                }
            } else {
                // Scenario 1: No ID = CREATE new entity
                log.debug("Creating new day rate for nightly: {}", rateResource.getNightly());
                DaySpecificRateModel newRate = defaultRateMapper.daySpecificRatePostResourceToModel(rateResource);
                newRate.setRate(existingRate);
                existingRates.add(newRate);
                // Ajouter l'ID de la nouvelle entité aux IDs traités pour éviter la suppression
                processedIds.add(newRate.getId());
            }
        }

        // Scenario 3: Remove entities that are no longer in the request
        Iterator<DaySpecificRateModel> iterator = existingRates.iterator();
        while (iterator.hasNext()) {
            DaySpecificRateModel existingRateModel = iterator.next();
            if (!processedIds.contains(existingRateModel.getId())) {
                log.debug("Removing day rate with ID: {} (not in request)", existingRateModel.getId());
                existingRateModel.setRate(null);
                iterator.remove();
            }
        }

        log.debug("After update - {} day rates remaining", existingRates.size());
    }
}