/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.model;

import com.smsmode.pricing.embeddable.AgeBucketEmbeddable;
import com.smsmode.pricing.enumeration.AmountType;
import com.smsmode.pricing.enumeration.GuestTypeEnum;
import com.smsmode.pricing.model.base.AbstractBaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@Getter
@Setter
@Entity
@Table(name = "ADDITIONAL_GUEST_FEE")
public class AdditionalGuestFeeModel extends AbstractBaseModel {
    private int guestCount;
    @Enumerated(EnumType.STRING)
    private GuestTypeEnum guestType;
    @Embedded
    private AgeBucketEmbeddable ageBucket;
    @Enumerated(EnumType.STRING)
    private AmountType amountType;
    private BigDecimal value;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEFAULT_RATE_ID", nullable = false)
    private DefaultRateModel rate;
}
