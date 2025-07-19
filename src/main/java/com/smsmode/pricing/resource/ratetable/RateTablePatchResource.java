package com.smsmode.pricing.resource.ratetable;

import com.smsmode.pricing.embeddable.RatePlanRefEmbeddable;
import com.smsmode.pricing.enumeration.RateTableTypeEnum;
import com.smsmode.pricing.resource.common.BaseRateResource;
import com.smsmode.pricing.validator.ValidRateTableDates;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@ValidRateTableDates
public class RateTablePatchResource extends BaseRateResource {
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
}
