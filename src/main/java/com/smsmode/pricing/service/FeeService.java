package com.smsmode.pricing.service;

import com.smsmode.pricing.resource.fee.ApplyFeesToUnitsResource;
import com.smsmode.pricing.resource.fee.FeeGetResource;
import com.smsmode.pricing.resource.fee.FeePostResource;
import org.springframework.http.ResponseEntity;

public interface FeeService {

    ResponseEntity<FeeGetResource> create(FeePostResource feePostResource);

    ResponseEntity<Void> applyFeesToUnits(ApplyFeesToUnitsResource resource, boolean overwrite);

}
