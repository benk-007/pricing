/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.resource.defaultrate;

import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeePostResource;
import com.smsmode.pricing.resource.common.dayspecificrate.DaySpecificRatePostResource;
import com.smsmode.pricing.validator.ValidGuestFees;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashSet;
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
    @Min(value = 1, message = "Minimum stay must be at least 1")
    private int minStay = 1;
    @Min(value = 1, message = "Maximum stay must be at least 1")
    private Integer maxStay;

    @NotNull(message = "Unit information is required")
    @Valid
    private UnitRefEmbeddable unit;

    @Valid
    private Set<@Valid AdditionalGuestFeePostResource> additionalGuestFees = new HashSet<>();

    @Valid
    private Set<@Valid DaySpecificRatePostResource> daySpecificRates = new HashSet<>();
}
