/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.dao.service.impl;

import com.smsmode.pricing.dao.repository.DefaultRateRepository;
import com.smsmode.pricing.dao.service.DefaultRateDaoService;
import com.smsmode.pricing.dao.specification.DefaultRateSpecification;
import com.smsmode.pricing.exception.ResourceNotFoundException;
import com.smsmode.pricing.exception.enumeration.ResourceNotFoundExceptionTitleEnum;
import com.smsmode.pricing.model.DefaultRateModel;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of DefaultRateDaoService for managing default rate data access.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultRateDaoServiceImpl implements DefaultRateDaoService {

    private final DefaultRateRepository defaultRateRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DefaultRateModel save(DefaultRateModel defaultRateModel) {
        return defaultRateRepository.save(defaultRateModel);
    }

    @Override
    public Page<DefaultRateModel> findByUnitId(String unitId, Pageable pageable) {
        log.debug("Finding default rates for unit: {}", unitId);
        Specification<DefaultRateModel> specification = DefaultRateSpecification.withUnitId(unitId);
        return defaultRateRepository.findAll(specification, pageable);
    }

    @Override
    public DefaultRateModel findById(String rateId) {
        log.debug("Finding default rate by ID: {}", rateId);
        return defaultRateRepository.findById(rateId).orElseThrow(() -> {
            log.debug("Default rate with ID [{}] not found", rateId);
            return new ResourceNotFoundException(
                    ResourceNotFoundExceptionTitleEnum.DEFAULT_RATE_NOT_FOUND,
                    "Default rate with ID [" + rateId + "] not found");
        });
    }

    // ========== PRICING CALCULATION IMPLEMENTATIONS ==========

    @Override
    public DefaultRateModel findWithRelatedDataForPricing(String unitId) {
        log.debug("Finding default rate with related data for pricing calculations - unit: {}", unitId);

        try {
            // Step 1: Use existing specification-based method
            Page<DefaultRateModel> results = findByUnitId(unitId, Pageable.unpaged());

            if (results.isEmpty()) {
                log.debug("No default rate found for unit: {}", unitId);
                return null;
            }

            DefaultRateModel defaultRate = results.getContent().get(0);

            // Step 2: Force loading of lazy collections
            defaultRate.getAdditionalGuestFees().size(); // Trigger lazy loading
            defaultRate.getDaySpecificRates().size();    // Trigger lazy loading

            log.debug("Found default rate with {} additional guest fees and {} day-specific rates for unit: {}",
                    defaultRate.getAdditionalGuestFees().size(),
                    defaultRate.getDaySpecificRates().size(),
                    unitId);

            return defaultRate;

        } catch (Exception e) {
            log.error("Error finding default rate with related data for unit: {}", unitId, e);
            return null;
        }
    }

    @Override
    public DefaultRateModel findByUnitIdSingle(String unitId) {
        log.debug("Finding single default rate for unit: {}", unitId);

        try {
            // Step 1: Use specification to find default rate by unit
            Specification<DefaultRateModel> specification = DefaultRateSpecification.withUnitId(unitId);
            List<DefaultRateModel> results = defaultRateRepository.findAll(specification);

            // Step 2: Return first result or null
            if (results.isEmpty()) {
                log.debug("No default rate found for unit: {}", unitId);
                return null;
            }

            DefaultRateModel defaultRate = results.get(0);
            log.debug("Found default rate for unit: {}", unitId);

            return defaultRate;

        } catch (Exception e) {
            log.error("Error finding single default rate for unit: {}", unitId, e);
            return null;
        }
    }

    @Override
    public boolean existsByUnitId(String unitId) {
        log.debug("Checking if default rate exists for unit: {}", unitId);

        try {
            // Step 1: Use specification to check existence
            Specification<DefaultRateModel> specification = DefaultRateSpecification.withUnitId(unitId);
            boolean exists = defaultRateRepository.exists(specification);

            log.debug("Default rate exists for unit {}: {}", unitId, exists);
            return exists;

        } catch (Exception e) {
            log.error("Error checking default rate existence for unit: {}", unitId, e);
            return false;
        }
    }
}