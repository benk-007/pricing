package com.smsmode.pricing.controller;

import com.smsmode.pricing.resource.fee.CopyFeesToUnitsResource;
import com.smsmode.pricing.resource.fee.FeeGetResource;
import com.smsmode.pricing.resource.fee.FeePatchResource;
import com.smsmode.pricing.resource.fee.FeePostResource;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("fees")
public interface FeeController {

    @PostMapping
    ResponseEntity<FeeGetResource> create(@Valid @RequestBody FeePostResource feePostResource);

    @PostMapping("/copyTo")
    ResponseEntity<Void> copyFeesToUnits(
            @RequestBody CopyFeesToUnitsResource resource,
            @RequestParam(defaultValue = "false") boolean overwrite
    );

    @GetMapping
    ResponseEntity<Page<FeeGetResource>> getAll(
            @RequestParam(required = false) Set<String> unitIds,
            @RequestParam(required = false) String search,
            Pageable pageable
    );

    @PatchMapping("/{feeId}")
    ResponseEntity<FeeGetResource> update(
            @PathVariable String feeId,
            @Valid @RequestBody FeePatchResource feePatchResource
    );

}
