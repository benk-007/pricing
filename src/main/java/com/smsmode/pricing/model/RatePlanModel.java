package com.smsmode.pricing.model;

import com.smsmode.pricing.embeddable.SegmentRefEmbeddable;
import com.smsmode.pricing.embeddable.SubSegmentRefEmbeddable;
import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import com.smsmode.pricing.model.base.AbstractBaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a rate plan in the pricing system.
 */
@Getter
@Setter
@Entity
@Table(name = "RATE_PLAN",
        uniqueConstraints = {
                // Ensures each rate plan has a unique name globally
                @UniqueConstraint(name = "UK_RATE_PLAN_NAME", columnNames = {"NAME"}),
                // Note: This constraint alone cannot handle our business rules because:
                // - Multiple NULL values are allowed for SEGMENT_UUID (which we want for the first rate plan)
                // - Cannot prevent: segment="S1", subSegment=null + segment="S1", subSegment=null ( NULL != NULL)
                // Business logic validation will be handled by custom validators
                @UniqueConstraint(name = "UK_RATE_PLAN_SEGMENT_SUBSEGMENT",
                        columnNames = {"SEGMENT_UUID", "SUBSEGMENT_UUID"})
        })
public class RatePlanModel extends AbstractBaseModel {

    @Column(name = "NAME", nullable = false)
    private String name;

    @Embedded
    private SegmentRefEmbeddable segment;

    @Embedded
    private SubSegmentRefEmbeddable subSegment;

    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled = false;

    @Embedded
    private UnitRefEmbeddable unit;
}