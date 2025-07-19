package com.smsmode.pricing.model;

import com.smsmode.pricing.embeddable.AgeBucketEmbeddable;
import com.smsmode.pricing.enumeration.AmountTypeEnum;
import com.smsmode.pricing.enumeration.GuestTypeEnum;
import com.smsmode.pricing.model.base.AbstractBaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entity representing additional guest fee for rate table.
 */
@Getter
@Setter
@Entity
@Table(name = "RATE_TABLE_ADDITIONAL_GUEST_FEE")
public class RateTableAdditionalGuestFeeModel extends AbstractBaseModel {

    private int guestCount;

    @Enumerated(EnumType.STRING)
    private GuestTypeEnum guestType;

    @Embedded
    private AgeBucketEmbeddable ageBucket;

    @Enumerated(EnumType.STRING)
    private AmountTypeEnum amountType;

    private BigDecimal value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RATE_TABLE_ID")
    private RateTableModel rateTable;
}