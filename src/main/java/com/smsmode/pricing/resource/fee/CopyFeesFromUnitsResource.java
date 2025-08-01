package com.smsmode.pricing.resource.fee;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class CopyFeesFromUnitsResource {

    @NotEmpty
    private Set<String> feeIds;

    @NotNull
    private String unitId;
}

