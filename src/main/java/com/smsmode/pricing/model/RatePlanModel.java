package com.smsmode.pricing.model;

import com.smsmode.pricing.embeddable.SegmentRefEmbeddable;
import com.smsmode.pricing.embeddable.SubSegmentRefEmbeddable;
import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import com.smsmode.pricing.model.base.AbstractBaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a rate plan in the pricing system.
 */
@Getter
@Setter
@Entity
@Table(name = "RATE_PLAN")
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

    // Bidirectional relationship with RateTable (no cascade)
    @OneToMany(mappedBy = "ratePlan", fetch = FetchType.LAZY)
    private List<RateTableModel> rateTables = new ArrayList<>();
}