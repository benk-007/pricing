package com.smsmode.pricing.resource.rateplan;

import com.smsmode.pricing.embeddable.SegmentRefEmbeddable;
import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Resource for creating and updating rate plans.
 */
@Data
public class RatePlanPostResource {

    @NotBlank(message = "Rate plan name is required")
    private String name;

    @NotEmpty(message = "At least one segment is required")
    @Valid
    private Set<SegmentRefEmbeddable> segments = new HashSet<>();

    @NotNull(message = "Enabled status is required")
    private Boolean enabled = false;

    @NotNull(message = "Unit information is required")
    @Valid
    private UnitRefEmbeddable unit;
}