/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.resource.common.additionalguestfee;

import com.smsmode.pricing.embeddable.AgeBucketEmbeddable;
import com.smsmode.pricing.enumeration.AmountTypeEnum;
import com.smsmode.pricing.enumeration.GuestTypeEnum;
import com.smsmode.pricing.validator.ValidAgeBucketIfChild;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Resource for creating and updating additional guest fees
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@Data
@ValidAgeBucketIfChild
public class AdditionalGuestFeePostResource {

    // ID is optional: null for CREATE, provided for UPDATE
    private String id;

    @NotNull(message = "Guest count is required")
    @Min(value = 1, message = "Guest count must be at least 1")
    private int guestCount;

    @NotNull(message = "Guest type is required")
    private GuestTypeEnum guestType;

    @Valid
    private AgeBucketEmbeddable ageBucket;

    @NotNull(message = "Amount type is required")
    private AmountTypeEnum amountType;

    @NotNull(message = "Value is required")
    @PositiveOrZero(message = "Value must be positive or zero")
    private BigDecimal value;
}