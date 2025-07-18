package com.smsmode.pricing.dao.service;

import com.smsmode.pricing.model.RatePlanModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * DAO Service interface for RatePlan data access operations.
 */
public interface RatePlanDaoService {

    /**
     * Saves a rate plan.
     */
    RatePlanModel save(RatePlanModel ratePlanModel);

    /**
     * Finds rate plans with same combination and enabled=true.
     */
    List<RatePlanModel> findEnabledRatePlansWithSameCombination(String segmentUuid, String subSegmentUuid);

    /**
     * Disables multiple rate plans by setting enabled=false.
     */
    void disableRatePlans(List<RatePlanModel> ratePlansToDisable);

    /**
     * Finds all rate plans related to a unit with pagination.
     */
    Page<RatePlanModel> findByUnitId(String unitId, Pageable pageable);

    /**
     * Finds a rate plan by its ID.
     */
    RatePlanModel findById(String ratePlanId);

    /**
     * Deletes a rate plan.
     */
    void delete(RatePlanModel ratePlanModel);
}