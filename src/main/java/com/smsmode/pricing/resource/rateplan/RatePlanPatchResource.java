package com.smsmode.pricing.resource.rateplan;

import com.smsmode.pricing.embeddable.SegmentRefEmbeddable;
import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import lombok.Data;

import java.util.Set;

/**
 * Resource for creating and updating rate plans.
 */
@Data
public class RatePlanPatchResource {
    private String name;
    private Set<SegmentRefEmbeddable> segments;
    private Boolean enabled;
    private UnitRefEmbeddable unit;
}