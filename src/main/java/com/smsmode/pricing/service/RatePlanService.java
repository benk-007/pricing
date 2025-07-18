package com.smsmode.pricing.service;

import com.smsmode.pricing.resource.rateplan.RatePlanGetResource;
import com.smsmode.pricing.resource.rateplan.RatePlanPatchResource;
import com.smsmode.pricing.resource.rateplan.RatePlanPostResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

/**
 * Service interface for RatePlan business operations.
 */
public interface RatePlanService {

    /**
     * Creates a new rate plan.
     */
    ResponseEntity<RatePlanGetResource> create(RatePlanPostResource ratePlanPostResource);

    /**
     * Retrieves all rate plans with pagination.
     */
    ResponseEntity<Page<RatePlanGetResource>> getAll(String unitId, Pageable pageable);

    /**
     * Retrieves a rate plan by its ID.
     */
    ResponseEntity<RatePlanGetResource> getById(String ratePlanId);

    /**
     * Updates an existing rate plan.
     */
    ResponseEntity<RatePlanGetResource> update(String ratePlanId, RatePlanPatchResource ratePlanPatchResource);

    /**
     * Deletes a rate plan by its ID.
     */
    ResponseEntity<Void> delete(String ratePlanId);
}