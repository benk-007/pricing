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
import java.util.HashSet;
import java.util.Set;

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

    @Column(nullable = false)
    private int minStay;

    private Integer maxStay;

    @Embedded
    private UnitRefEmbeddable unit;

    @OneToMany(mappedBy = "rate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<AdditionalGuestFeeModel> additionalGuestFees = new HashSet<>();

    @OneToMany(mappedBy = "rate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<DaySpecificRateModel> daySpecificRates = new HashSet<>();


    public void addAdditionalGuestFee(AdditionalGuestFeeModel fee) {
        additionalGuestFees.add(fee);
        fee.setRate(this);
    }


    public void removeAdditionalGuestFee(AdditionalGuestFeeModel fee) {
        additionalGuestFees.remove(fee);
        fee.setRate(null);
    }


    public void addDaySpecificRate(DaySpecificRateModel dayRate) {
        daySpecificRates.add(dayRate);
        dayRate.setRate(this);
    }


    public void removeDaySpecificRate(DaySpecificRateModel dayRate) {
        daySpecificRates.remove(dayRate);
        dayRate.setRate(null);
    }

}