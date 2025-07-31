package com.smsmode.pricing.controller;

import com.smsmode.pricing.resource.fee.ApplyFeesToUnitsResource;
import com.smsmode.pricing.resource.fee.FeeGetResource;
import com.smsmode.pricing.resource.fee.FeePostResource;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("fees")
public interface FeeController {

    @PostMapping
    ResponseEntity<FeeGetResource> create(@Valid @RequestBody FeePostResource feePostResource);

    @PostMapping("/apply")
    ResponseEntity<Void> applyFeesToUnits(
            @RequestBody ApplyFeesToUnitsResource resource,
            @RequestParam(defaultValue = "false") boolean overwrite
    );
}
