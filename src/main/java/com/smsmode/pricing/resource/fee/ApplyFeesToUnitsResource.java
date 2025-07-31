package com.smsmode.pricing.resource.fee;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class ApplyFeesToUnitsResource {

    @NotEmpty
    private Set<String> feeIds;

    @NotEmpty
    private Set<String> unitIds;
}