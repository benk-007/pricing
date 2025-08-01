package com.smsmode.pricing.service;

import com.smsmode.pricing.resource.fee.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface FeeService {

    ResponseEntity<FeeGetResource> create(FeePostResource feePostResource);

    ResponseEntity<Void> copyFeesToUnits(CopyFeesToUnitsResource resource, boolean overwrite);

    ResponseEntity<Void> copyFeesFromUnits(CopyFeesFromUnitsResource resource, boolean overwrite);

    ResponseEntity<Page<FeeGetResource>> getAll(Set<String> unitIds, String search, Pageable pageable);

    ResponseEntity<FeeGetResource> update(String feeId, FeePatchResource feePatchResource);

}
