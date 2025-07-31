package com.smsmode.pricing.controller.impl;

import com.smsmode.pricing.controller.FeeController;
import com.smsmode.pricing.resource.fee.ApplyFeesToUnitsResource;
import com.smsmode.pricing.resource.fee.FeeGetResource;
import com.smsmode.pricing.resource.fee.FeePostResource;
import com.smsmode.pricing.service.FeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FeeControllerImpl implements FeeController {

    private final FeeService feeService;

    @Override
    public ResponseEntity<FeeGetResource> create(FeePostResource feePostResource) {
        return feeService.create(feePostResource);
    }

    @Override
    public ResponseEntity<Void> applyFeesToUnits(ApplyFeesToUnitsResource resource, boolean overwrite) {
        return feeService.applyFeesToUnits(resource, overwrite);
    }

    @Override
    public ResponseEntity<Page<FeeGetResource>> getAll(String unitId, String search, Pageable pageable) {
        return feeService.getAll(unitId, search, pageable);
    }

}
