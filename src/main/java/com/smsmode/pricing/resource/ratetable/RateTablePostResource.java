package com.smsmode.pricing.resource.ratetable;

import com.smsmode.pricing.embeddable.RatePlanRefEmbeddable;
import com.smsmode.pricing.enumeration.RateTableTypeEnum;
import com.smsmode.pricing.resource.common.BaseRateResource;
import com.smsmode.pricing.validator.ValidRateTableDates;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Resource for creating rate tables.
 */
@Getter
@Setter
@ValidRateTableDates
public class RateTablePostResource extends BaseRateResource {

    @NotBlank(message = "Rate table name is required")
    private String name;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Rate table type is required")
    private RateTableTypeEnum type = RateTableTypeEnum.STANDARD;

    // Standard fields
    private BigDecimal nightly;
    private Integer minStay;
    private Integer maxStay;

    // Dynamic fields
    private BigDecimal lowRate;
    private BigDecimal maxRate;
    private Integer lowestOccupancy;
    private Integer maxOccupancy;

    @NotNull(message = "Rate plan information is required")
    @Valid
    private RatePlanRefEmbeddable ratePlan;
}