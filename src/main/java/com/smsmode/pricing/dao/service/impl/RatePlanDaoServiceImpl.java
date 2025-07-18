package com.smsmode.pricing.dao.service.impl;

import com.smsmode.pricing.dao.repository.RatePlanRepository;
import com.smsmode.pricing.dao.service.RatePlanDaoService;
import com.smsmode.pricing.dao.specification.RatePlanSpecification;
import com.smsmode.pricing.exception.ResourceNotFoundException;
import com.smsmode.pricing.exception.enumeration.ResourceNotFoundExceptionTitleEnum;
import com.smsmode.pricing.model.RatePlanModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * Implementation of RatePlanDaoService for managing rate plan data access.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RatePlanDaoServiceImpl implements RatePlanDaoService {

    private final RatePlanRepository ratePlanRepository;

    @Override
    public RatePlanModel save(RatePlanModel ratePlanModel) {
        log.debug("Saving rate plan: {}", ratePlanModel.getName());
        return ratePlanRepository.save(ratePlanModel);
    }

    @Override
    public Page<RatePlanModel> findByUnitId(String unitId, Pageable pageable) {
        Specification<RatePlanModel> specification = RatePlanSpecification.withUnitUuid(unitId);
        return ratePlanRepository.findAll(specification, pageable);
    }

    @Override
    public RatePlanModel findById(String ratePlanId) {
        log.debug("Finding rate plan by ID: {}", ratePlanId);
        return ratePlanRepository.findById(ratePlanId).orElseThrow(() -> {
            log.debug("Rate plan with ID [{}] not found", ratePlanId);
            return new ResourceNotFoundException(
                    ResourceNotFoundExceptionTitleEnum.RATE_PLAN_NOT_FOUND,
                    "Rate plan with ID [" + ratePlanId + "] not found");
        });
    }

    @Override
    public void delete(RatePlanModel ratePlanModel) {
        log.debug("Deleting rate plan: {}", ratePlanModel.getId());
        ratePlanRepository.delete(ratePlanModel);
    }
}