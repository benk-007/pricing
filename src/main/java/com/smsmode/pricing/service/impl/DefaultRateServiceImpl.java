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
import com.smsmode.pricing.resource.defaultrate.DefaultRateGetResource;
import com.smsmode.pricing.resource.defaultrate.DefaultRatePostResource;
import com.smsmode.pricing.service.DefaultRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Implementation of DefaultRateService for managing default rate business logic.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultRateServiceImpl implements DefaultRateService {

    private final DefaultRateMapper defaultRateMapper;
    private final DefaultRateDaoService defaultRateDaoService;

    @Override
    public ResponseEntity<DefaultRateGetResource> create(DefaultRatePostResource defaultRatePostResource) {
        log.debug("Creating default rate for unit: {}", defaultRatePostResource.getUnit().getUuid());

        DefaultRateModel defaultRateModel = defaultRateMapper.postResourceToModel(defaultRatePostResource);

        // Set unit reference directly from the post resource
        defaultRateModel.setUnit(defaultRatePostResource.getUnit());

        // Set bidirectional relationships for additional guest fees
        if (defaultRateModel.getAdditionalGuestFees() != null) {
            for (AdditionalGuestFeeModel fee : defaultRateModel.getAdditionalGuestFees()) {
                fee.setRate(defaultRateModel);
            }
        }

        // Set bidirectional relationships for day specific rates
        if (defaultRateModel.getDaySpecificRates() != null) {
            for (DaySpecificRateModel dayRate : defaultRateModel.getDaySpecificRates()) {
                dayRate.setRate(defaultRateModel);
            }
        }

        defaultRateModel = defaultRateDaoService.save(defaultRateModel);
        log.info("Successfully created default rate with ID: {}", defaultRateModel.getId());

        return ResponseEntity.ok(defaultRateMapper.modelToGetResource(defaultRateModel));
    }

    @Override
    public ResponseEntity<Page<DefaultRateGetResource>> getByUnitId(String unitId, Pageable pageable) {
        log.debug("Retrieving default rates for unit: {}", unitId);
        Page<DefaultRateModel> defaultRateModelPage = defaultRateDaoService.findByUnitId(unitId, pageable);
        log.info("Retrieved {} default rates from database", defaultRateModelPage.getTotalElements());
        log.debug("Mapping default rates to get resources ...");
        Page<DefaultRateGetResource> defaultRateGetResourcePage = defaultRateModelPage.map(defaultRateMapper::modelToGetResource);
        log.info("Default rate get resources after mapping: {}", defaultRateGetResourcePage.getTotalElements());
        return ResponseEntity.ok(defaultRateGetResourcePage);
    }

    @Override
    public ResponseEntity<DefaultRateGetResource> update(String rateId, DefaultRatePostResource defaultRatePostResource) {
        log.debug("Updating default rate with ID: {}", rateId);

        // Find existing default rate
        DefaultRateModel existingDefaultRate = defaultRateDaoService.findById(rateId);
        log.debug("Found existing default rate: {}", existingDefaultRate.getId());

        // Map updated fields from post resource to existing model
        defaultRateMapper.updateModelFromPostResource(defaultRatePostResource, existingDefaultRate);

        // Handle bidirectional relationships for collections
        if (existingDefaultRate.getAdditionalGuestFees() != null) {
            for (AdditionalGuestFeeModel fee : existingDefaultRate.getAdditionalGuestFees()) {
                fee.setRate(existingDefaultRate);
            }
        }

        if (existingDefaultRate.getDaySpecificRates() != null) {
            for (DaySpecificRateModel dayRate : existingDefaultRate.getDaySpecificRates()) {
                dayRate.setRate(existingDefaultRate);
            }
        }

        log.debug("Mapped post resource to existing model");

        // Save updated model
        DefaultRateModel updatedDefaultRate = defaultRateDaoService.save(existingDefaultRate);
        log.info("Successfully updated default rate with ID: {}", rateId);

        // Map to response resource
        DefaultRateGetResource response = defaultRateMapper.modelToGetResource(updatedDefaultRate);
        return ResponseEntity.ok(response);
    }
}