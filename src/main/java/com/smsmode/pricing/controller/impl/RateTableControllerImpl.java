package com.smsmode.pricing.controller.impl;

import com.smsmode.pricing.controller.RateTableController;
import com.smsmode.pricing.resource.ratetable.RateTableGetResource;
import com.smsmode.pricing.resource.ratetable.RateTablePatchResource;
import com.smsmode.pricing.resource.ratetable.RateTablePostResource;
import com.smsmode.pricing.service.RateTableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implementation of RateTableController for managing rate table REST endpoints.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class RateTableControllerImpl implements RateTableController {

    private final RateTableService rateTableService;

    @Override
    public ResponseEntity<RateTableGetResource> create(RateTablePostResource rateTablePostResource) {
        log.debug("POST /rate-tables - Creating rate table: {}", rateTablePostResource.getName());
        return rateTableService.create(rateTablePostResource);
    }

    @Override
    public ResponseEntity<Page<RateTableGetResource>> getAll(String ratePlanUuid, String search, Pageable pageable) {
        log.debug("GET /rate-tables - Retrieving rate tables for ratePlan: {}, search: {}", ratePlanUuid, search);
        return rateTableService.getAll(ratePlanUuid, search, pageable);
    }

    @Override
    public ResponseEntity<RateTableGetResource> getById(String rateTableId) {
        log.debug("GET /rate-tables/{} - Retrieving rate table by ID", rateTableId);
        return rateTableService.getById(rateTableId);
    }

    @Override
    public ResponseEntity<RateTableGetResource> update(String rateTableId, RateTablePatchResource rateTablePatchResource) {
        log.debug("PATCH /rate-tables/{} - Updating rate table", rateTableId);
        return rateTableService.update(rateTableId, rateTablePatchResource);
    }

    @Override
    public ResponseEntity<Void> delete(String rateTableId) {
        log.debug("DELETE /rate-tables/{} - Deleting rate table", rateTableId);
        return rateTableService.delete(rateTableId);
    }
}