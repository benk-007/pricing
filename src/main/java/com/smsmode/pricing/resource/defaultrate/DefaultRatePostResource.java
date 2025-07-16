/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.resource.defaultrate;

import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeePostResource;
import com.smsmode.pricing.validator.ValidGuestFees;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@Data
@ValidGuestFees
public class DefaultRatePostResource {
    @NotNull
    private BigDecimal nightly;
    @NotNull
    @Positive
    private int minStay = 1;
    private Integer maxStay;
    @Valid
    Set<@Valid AdditionalGuestFeePostResource> additionalGuestFees;
}
