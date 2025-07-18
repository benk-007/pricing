package com.smsmode.pricing.resource.rateplan;

import com.smsmode.pricing.embeddable.SegmentRefEmbeddable;
import com.smsmode.pricing.embeddable.SubSegmentRefEmbeddable;
import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import com.smsmode.pricing.validator.ValidRatePlanSegment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Resource for creating and updating rate plans.
 */
@Data
@ValidRatePlanSegment
public class RatePlanPostResource {

    @NotBlank(message = "Rate plan name is required")
    private String name;

    @Valid
    private SegmentRefEmbeddable segment;

    @Valid
    private SubSegmentRefEmbeddable subSegment;

    @NotNull(message = "Enabled status is required")
    private Boolean enabled = false;

    @NotNull(message = "Unit information is required")
    @Valid
    private UnitRefEmbeddable unit;
}