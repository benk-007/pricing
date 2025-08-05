/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.model;

import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import com.smsmode.pricing.model.base.AbstractBaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a default rate
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@Getter
@Setter
@Entity
@Table(name = "DEFAULT_RATE")
public class DefaultRateModel extends AbstractBaseModel {

    @Column(nullable = false)
    private BigDecimal nightly;

    private Integer minStay;

    private Integer maxStay;

    @Embedded
    private UnitRefEmbeddable unit;

    @OneToMany(mappedBy = "rate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<AdditionalGuestFeeModel> additionalGuestFees = new ArrayList<>();

    @OneToMany(mappedBy = "rate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<DaySpecificRateModel> daySpecificRates = new ArrayList<>();
}