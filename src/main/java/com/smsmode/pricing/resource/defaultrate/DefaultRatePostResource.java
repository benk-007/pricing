/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.resource.defaultrate;

import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeePostResource;
import com.smsmode.pricing.resource.common.dayspecificrate.DaySpecificRatePostResource;
import com.smsmode.pricing.validator.ValidGuestFees;
import com.smsmode.pricing.validator.ValidDaySpecificRates;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Resource for creating and updating default rates
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@Data
@ValidGuestFees
@ValidDaySpecificRates
public class DefaultRatePostResource {

    @NotNull(message = "Nightly rate is required")
    private BigDecimal nightly;

    @NotNull(message = "Minimum stay is required")
    @Min(value = 1, message = "Minimum stay must be at least 1")
    private int minStay;

    private Integer maxStay;

    @Valid
    @NotNull(message = "Unit information is required")
    private UnitRefEmbeddable unit;

    @Valid
    private List<@Valid DaySpecificRatePostResource> daySpecificRates;

    @Valid
    private List<@Valid AdditionalGuestFeePostResource> additionalGuestFees;
}