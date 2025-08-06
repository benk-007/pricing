package com.smsmode.pricing.model;

import com.smsmode.pricing.embeddable.SegmentRefEmbeddable;
import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import com.smsmode.pricing.model.base.AbstractBaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<SegmentRefEmbeddable> segments = new HashSet<>();

    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled = false;

    @Column(name = "standard", nullable = false)
    private boolean standard = false;

    @Embedded
    private UnitRefEmbeddable unit;

    @OneToMany(mappedBy = "ratePlan", fetch = FetchType.LAZY)
    private List<RateTableModel> rateTables = new ArrayList<>();
}