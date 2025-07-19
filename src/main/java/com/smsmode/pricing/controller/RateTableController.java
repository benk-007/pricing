package com.smsmode.pricing.controller;

import com.smsmode.pricing.resource.ratetable.RateTableGetResource;
import com.smsmode.pricing.resource.ratetable.RateTablePatchResource;
import com.smsmode.pricing.resource.ratetable.RateTablePostResource;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller interface for RateTable REST endpoints.
 */
@RequestMapping("rate-tables")
public interface RateTableController {

    /**
     * Creates a new rate table.
     */
    @PostMapping
    ResponseEntity<RateTableGetResource> create(@Valid @RequestBody RateTablePostResource rateTablePostResource);

    /**
     * Retrieves rate tables with filters and pagination.
     */
    @GetMapping
    ResponseEntity<Page<RateTableGetResource>> getAll(@RequestParam String ratePlanUuid,
                                                      @RequestParam(required = false) String search,
                                                      Pageable pageable);

    /**
     * Retrieves a rate table by its ID.
     */
    @GetMapping("/{rateTableId}")
    ResponseEntity<RateTableGetResource> getById(@PathVariable String rateTableId);

    /**
     * Updates an existing rate table.
     */
    @PatchMapping("/{rateTableId}")
    ResponseEntity<RateTableGetResource> update(@PathVariable String rateTableId,
                                                @Valid @RequestBody RateTablePatchResource rateTablePatchResource);

    /**
     * Deletes a rate table by its ID.
     */
    @DeleteMapping("/{rateTableId}")
    ResponseEntity<Void> delete(@PathVariable String rateTableId);
}