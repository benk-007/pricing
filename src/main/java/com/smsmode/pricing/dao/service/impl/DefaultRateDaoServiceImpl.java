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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
                    ResourceNotFoundExceptionTitleEnum.GUEST_NOT_FOUND, // TODO: Add DEFAULT_RATE_NOT_FOUND
                    "Default rate with ID [" + rateId + "] not found");
        });
    }
}