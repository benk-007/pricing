package com.smsmode.pricing.service;

import com.smsmode.pricing.resource.ratetable.RateTableGetResource;
import com.smsmode.pricing.resource.ratetable.RateTablePatchResource;
import com.smsmode.pricing.resource.ratetable.RateTablePostResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

/**
 * Service interface for RateTable business operations.
 */
public interface RateTableService {

    /**
     * Creates a new rate table with overlap validation.
     */
    ResponseEntity<RateTableGetResource> create(RateTablePostResource rateTablePostResource);

    /**
     * Retrieves rate tables with filters and pagination.
     */
    ResponseEntity<Page<RateTableGetResource>> getAll(String ratePlanUuid, String search, Pageable pageable);

    /**
     * Retrieves a rate table by its ID.
     */
    ResponseEntity<RateTableGetResource> getById(String rateTableId);

    /**
     * Updates an existing rate table with overlap validation.
     */
    ResponseEntity<RateTableGetResource> update(String rateTableId, RateTablePatchResource rateTablePatchResource);

    /**
     * Deletes a rate table by its ID.
     */
    ResponseEntity<Void> delete(String rateTableId);
}