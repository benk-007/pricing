/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.resource.common.additionalguestfee;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smsmode.pricing.embeddable.AgeBucketEmbeddable;
import com.smsmode.pricing.enumeration.AmountTypeEnum;
import com.smsmode.pricing.enumeration.GuestTypeEnum;
import com.smsmode.pricing.validator.ValidAgeBucketIfChild;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@Data
@ValidAgeBucketIfChild
public class AdditionalGuestFeePostResource {
    @Min(1)
    private int guestCount;
    @NotNull
    private GuestTypeEnum guestType;
    @Valid
    private AgeBucketEmbeddable ageBucket;
    @NotNull
    private AmountTypeEnum amountType;
    @NotNull
    @PositiveOrZero
    private BigDecimal value;
}
