package com.smsmode.pricing.controller.impl;

import com.smsmode.pricing.controller.RatePlanController;
import com.smsmode.pricing.resource.rateplan.RatePlanGetResource;
import com.smsmode.pricing.resource.rateplan.RatePlanPatchResource;
import com.smsmode.pricing.resource.rateplan.RatePlanPostResource;
import com.smsmode.pricing.service.RatePlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implementation of RatePlanController for managing rate plan REST endpoints.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class RatePlanControllerImpl implements RatePlanController {

    private final RatePlanService ratePlanService;

    @Override
    public ResponseEntity<RatePlanGetResource> create(RatePlanPostResource ratePlanPostResource) {
        return ratePlanService.create(ratePlanPostResource);
    }

    @Override
    public ResponseEntity<Page<RatePlanGetResource>> getAll(String unitId, String search, String segmentName, Pageable pageable) {
        log.debug("GET /rate-plans - Retrieving all rate plans with pagination");
        return ratePlanService.getAll(unitId, search, segmentName, pageable);
    }

    @Override
    public ResponseEntity<RatePlanGetResource> getById(String ratePlanId) {
        log.debug("GET /rate-plans/{} - Retrieving rate plan by ID", ratePlanId);
        return ratePlanService.getById(ratePlanId);
    }

    @Override
    public ResponseEntity<RatePlanGetResource> update(String ratePlanId, RatePlanPatchResource ratePlanPatchResource) {
        log.debug("PATCH /rate-plans/{} - Updating rate plan", ratePlanId);
        return ratePlanService.update(ratePlanId, ratePlanPatchResource);
    }

    @Override
    public ResponseEntity<Void> delete(String ratePlanId) {
        log.debug("DELETE /rate-plans/{} - Deleting rate plan", ratePlanId);
        return ratePlanService.delete(ratePlanId);
    }
}