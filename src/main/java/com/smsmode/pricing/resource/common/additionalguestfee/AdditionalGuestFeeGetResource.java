package com.smsmode.pricing.resource.common.additionalguestfee;

import com.smsmode.pricing.embeddable.AgeBucketEmbeddable;
import com.smsmode.pricing.enumeration.AmountTypeEnum;
import com.smsmode.pricing.enumeration.GuestTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Resource representing additional guest fee information in GET responses.
 */
@Data
public class AdditionalGuestFeeGetResource {
    private String id;
    private int guestCount;
    private GuestTypeEnum guestType;
    private AgeBucketEmbeddable ageBucket;
    private AmountTypeEnum amountType;
    private BigDecimal value;
}