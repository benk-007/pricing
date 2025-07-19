package com.smsmode.pricing.resource.ratetable;

import com.smsmode.pricing.embeddable.RatePlanRefEmbeddable;
import com.smsmode.pricing.enumeration.RateTableTypeEnum;
import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeePostResource;
import com.smsmode.pricing.resource.common.dayspecificrate.DaySpecificRatePostResource;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class RateTablePatchResource {
    // Everything is optional (no @NotNull, @NotBlank)
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private RateTableTypeEnum type;

    // Standard fields
    private BigDecimal nightly;
    private Integer minStay;
    private Integer maxStay;

    // Dynamic fields
    private BigDecimal lowRate;
    private BigDecimal maxRate;
    private Integer lowestOccupancy;
    private Integer maxOccupancy;

    private RatePlanRefEmbeddable ratePlan;
    private List<DaySpecificRatePostResource> daySpecificRates;
    private List<AdditionalGuestFeePostResource> additionalGuestFees;
}
