package com.smsmode.pricing.dao.service;

import com.smsmode.pricing.model.RatePlanModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Set;

/**
 * DAO Service interface for RatePlan data access operations.
 */
public interface RatePlanDaoService {

    /**
     * Saves a rate plan.
     */
    RatePlanModel save(RatePlanModel ratePlanModel);

    /**
     * Finds rate plans that have at least one segment in common with the given segments.
     */
    List<RatePlanModel> findEnabledRatePlansWithOverlappingSegments(Set<String> segmentUuids);

    /**
     * Disables multiple rate plans by setting enabled=false.
     */
    void disableRatePlans(List<RatePlanModel> ratePlansToDisable);

    /**
     * Finds all rate plans related to a unit with pagination.
     */
    Page<RatePlanModel> findByUnitId(String unitId, String search, String segmentName, Pageable pageable);

    /**
     * Finds a rate plan by its ID.
     */
    RatePlanModel findById(String ratePlanId);

    /**
     * Deletes a rate plan.
     */
    void delete(RatePlanModel ratePlanModel);

    Page<RatePlanModel> findAll(Specification<RatePlanModel> specification, Pageable unpaged);

    boolean existsBy(Specification<RatePlanModel> specification);

    RatePlanModel findOneBy(Specification<RatePlanModel> specification);
}