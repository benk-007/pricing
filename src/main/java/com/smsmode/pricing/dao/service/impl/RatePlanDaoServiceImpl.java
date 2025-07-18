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

import java.util.List;

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
    public List<RatePlanModel> findEnabledRatePlansWithSameCombination(String segmentUuid, String subSegmentUuid) {
        log.debug("Finding enabled rate plans with segment: {}, subSegment: {}", segmentUuid, subSegmentUuid);

        Specification<RatePlanModel> spec;

        // Case 1: Name only (segment=null, subSegment=null)
        if (segmentUuid == null && subSegmentUuid == null) {
            spec = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                    criteriaBuilder.isNull(root.get("segment")),
                    criteriaBuilder.equal(root.get("enabled"), true)
            );
        }// Case 2: name + segment (subSegment=null)
        else if (subSegmentUuid == null) {

            spec = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("segment").get("uuid"), segmentUuid),
                    criteriaBuilder.isNull(root.get("subSegment")),
                    criteriaBuilder.equal(root.get("enabled"), true)
            );
        }// Case 3: name + segment + subSegment
        else {
            spec = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("segment").get("uuid"), segmentUuid),
                    criteriaBuilder.equal(root.get("subSegment").get("uuid"), subSegmentUuid),
                    criteriaBuilder.equal(root.get("enabled"), true)
            );
        }

        return ratePlanRepository.findAll(spec);
    }

    @Override
    public void disableRatePlans(List<RatePlanModel> ratePlansToDisable) {
        log.debug("Disabling {} rate plans", ratePlansToDisable.size());
        for (RatePlanModel ratePlan : ratePlansToDisable) {
            ratePlan.setEnabled(false);
            ratePlanRepository.save(ratePlan);
        }
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