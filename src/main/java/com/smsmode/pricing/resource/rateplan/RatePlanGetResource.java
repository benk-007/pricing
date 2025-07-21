package com.smsmode.pricing.resource.rateplan;

import com.smsmode.pricing.embeddable.SegmentRefEmbeddable;
import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import com.smsmode.pricing.resource.common.AuditGetResource;
import lombok.Data;

import java.util.Set;

/**
 * Resource for returning rate plan information.
 */
@Data
public class RatePlanGetResource {

    private String id;
    private String name;
    private Set<SegmentRefEmbeddable> segment;
    private Boolean enabled;
    private UnitRefEmbeddable unit;
    private AuditGetResource audit;
}