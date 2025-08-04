package com.smsmode.pricing.controller.impl;

import com.smsmode.pricing.controller.FeeController;
import com.smsmode.pricing.resource.fee.*;
import com.smsmode.pricing.service.FeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

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
    public ResponseEntity<Void> copyFeesToUnits(CopyFeesToUnitsResource resource, boolean overwrite) {
        return feeService.copyFeesToUnits(resource, overwrite);
    }

    @Override
    public ResponseEntity<Void> copyFeesFromUnits(CopyFeesFromUnitsResource resource, boolean overwrite) {
        return feeService.copyFeesFromUnits(resource, overwrite);
    }


    @Override
    public ResponseEntity<Page<FeeGetResource>> getAll(Set<String> unitIds, String search, Pageable pageable) {
        return feeService.getAll(unitIds, search, pageable);
    }

    @Override
    public ResponseEntity<FeeGetResource> update(String feeId, FeePatchResource feePatchResource) {
        return feeService.update(feeId, feePatchResource);
    }

    @Override
    public ResponseEntity<Void> delete(String feeId) {
        return feeService.delete(feeId);
    }

}
