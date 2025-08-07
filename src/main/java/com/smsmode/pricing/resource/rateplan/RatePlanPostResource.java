package com.smsmode.pricing.resource.rateplan;

import com.smsmode.pricing.embeddable.SegmentRefEmbeddable;
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

    @NotBlank
    private String name;

    @NotNull
    private boolean enabled = false;

    @NotNull
    private Boolean standard = false;

    @Valid
    private Set<SegmentRefEmbeddable> segments = new HashSet<>();

    @NotBlank
    private String unitId;
}