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
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@Getter
@Setter
@Entity
@Table(name = "DEFAULT_RATE")
public class DefaultRateModel extends AbstractBaseModel {
    private BigDecimal nightly;
    private int minStay = 1;
    private Integer maxStay;
    @Embedded
    private UnitRefEmbeddable unit;
    @OneToMany(mappedBy = "rate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AdditionalGuestFeeModel> additionalGuestFees = new HashSet<>();
    @OneToMany(mappedBy = "rate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DaySpecificRateModel> daySpecificRates = new HashSet<>();



/*    public void addAdditionalGuestFee(AdditionalGuestFeeModel fee) {
        fee.setRate(this); // set the reverse side
        this.additionalGuestFee.add(fee);
    }

    public void removeAdditionalGuestFee(AdditionalGuestFeeModel fee) {
        this.additionalGuestFee.remove(fee);
        fee.setRate(null); // optional if you want full cleanup
    }*/

}
