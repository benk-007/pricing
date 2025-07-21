package com.smsmode.pricing.resource.rateplan;

import com.smsmode.pricing.embeddable.SegmentRefEmbeddable;
import com.smsmode.pricing.embeddable.SubSegmentRefEmbeddable;
import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import lombok.Data;

/**
 * Resource for creating and updating rate plans.
 */
@Data
public class RatePlanPatchResource {
    private String name;
    private SegmentRefEmbeddable segment;
    private SubSegmentRefEmbeddable subSegment;
    private Boolean enabled = false;
    private UnitRefEmbeddable unit;
}