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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Implementation of DefaultRateService with proper handling of optional collections.
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
    public ResponseEntity<Page<DefaultRateGetResource>> getByUnitId(String unitId, Pageable pageable) {
        log.debug("Retrieving default rates for unit: {}", unitId);

        Page<DefaultRateModel> defaultRateModelPage = defaultRateDaoService.findByUnitId(unitId, pageable);
        log.info("Retrieved {} default rates from database", defaultRateModelPage.getTotalElements());

        Page<DefaultRateGetResource> defaultRateGetResourcePage = defaultRateModelPage.map(model -> {
            DefaultRateGetResource resource = defaultRateMapper.modelToGetResource(model);

            // For GET, we show collections only if they have data
            if (CollectionUtils.isEmpty(resource.getAdditionalGuestFees())) {
                resource.setAdditionalGuestFees(null);
            }
            if (CollectionUtils.isEmpty(resource.getDaySpecificRates())) {
                resource.setDaySpecificRates(null);
            }

            return resource;
        });

        return ResponseEntity.ok(defaultRateGetResourcePage);
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

        // Map to response and clean up empty collections
        DefaultRateGetResource response = defaultRateMapper.modelToGetResource(updatedDefaultRate);

        if (CollectionUtils.isEmpty(response.getAdditionalGuestFees())) {
            response.setAdditionalGuestFees(null);
        }
        if (CollectionUtils.isEmpty(response.getDaySpecificRates())) {
            response.setDaySpecificRates(null);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Updates Additional Guest Fees while preserving existing UUIDs
     */
    private void updateAdditionalGuestFees(DefaultRateModel existingRate, Set<AdditionalGuestFeePostResource> newFees) {
        log.debug("Updating additional guest fees");

        Set<AdditionalGuestFeeModel> existingFees = existingRate.getAdditionalGuestFees();

        // If newFees is null or empty, clear all existing fees
        if (CollectionUtils.isEmpty(newFees)) {
            log.debug("Clearing all additional guest fees");
            existingFees.clear();
            return;
        }

        Set<AdditionalGuestFeeModel> updatedFees = new HashSet<>();

        for (AdditionalGuestFeePostResource feeResource : newFees) {
            AdditionalGuestFeeModel feeModel = null;

            // Try to find existing fee by ID
            if (StringUtils.hasText(feeResource.getId())) {
                feeModel = existingFees.stream()
                        .filter(existing -> feeResource.getId().equals(existing.getId()))
                        .findFirst()
                        .orElse(null);
            }

            if (feeModel != null) {
                // Update existing entity (preserve UUID)
                log.debug("Updating existing additional guest fee with ID: {}", feeModel.getId());
                feeModel.setGuestCount(feeResource.getGuestCount());
                feeModel.setGuestType(feeResource.getGuestType());
                feeModel.setAgeBucket(feeResource.getAgeBucket());
                feeModel.setAmountType(feeResource.getAmountType());
                feeModel.setValue(feeResource.getValue());
            } else {
                // Create new entity
                log.debug("Creating new additional guest fee");
                feeModel = defaultRateMapper.additionalGuestFeePostResourceToModel(feeResource);
                feeModel.setRate(existingRate);
            }

            updatedFees.add(feeModel);
        }

        // Add new entities
        for (AdditionalGuestFeeModel updated : updatedFees) {
            if (!existingFees.contains(updated)) {
                existingFees.add(updated);
            }
        }

        log.debug("Updated {} additional guest fees", existingFees.size());
    }

    /**
     * Updates Day Specific Rates while preserving existing UUIDs
     */
    private void updateDaySpecificRates(DefaultRateModel existingRate, Set<DaySpecificRatePostResource> newRates) {
        log.debug("Updating day specific rates");

        Set<DaySpecificRateModel> existingRates = existingRate.getDaySpecificRates();

        // If newRates is null or empty, clear all existing rates
        if (CollectionUtils.isEmpty(newRates)) {
            log.debug("Clearing all day specific rates");
            existingRates.clear();
            return;
        }

        Set<DaySpecificRateModel> updatedRates = new HashSet<>();

        for (DaySpecificRatePostResource rateResource : newRates) {
            DaySpecificRateModel rateModel = null;

            // Try to find existing rate by ID
            if (StringUtils.hasText(rateResource.getId())) {
                rateModel = existingRates.stream()
                        .filter(existing -> rateResource.getId().equals(existing.getId()))
                        .findFirst()
                        .orElse(null);
            }

            if (rateModel != null) {
                // Update existing entity (preserve UUID)
                log.debug("Updating existing day specific rate with ID: {}", rateModel.getId());
                rateModel.setNightly(rateResource.getNightly());
                rateModel.setDays(rateResource.getDays());
            } else {
                // Create new entity
                log.debug("Creating new day specific rate");
                rateModel = defaultRateMapper.daySpecificRatePostResourceToModel(rateResource);
                rateModel.setRate(existingRate);
            }

            updatedRates.add(rateModel);
        }

        // Add new entities
        for (DaySpecificRateModel updated : updatedRates) {
            if (!existingRates.contains(updated)) {
                existingRates.add(updated);
            }
        }

        log.debug("Updated {} day specific rates", existingRates.size());
    }
}