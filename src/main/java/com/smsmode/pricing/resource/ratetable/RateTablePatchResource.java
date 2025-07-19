package com.smsmode.pricing.resource.ratetable;

import com.smsmode.pricing.embeddable.RatePlanRefEmbeddable;
import com.smsmode.pricing.enumeration.RateTableTypeEnum;
import com.smsmode.pricing.resource.common.BaseRateResource;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RateTablePatchResource extends BaseRateResource {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private RateTableTypeEnum type;

    // Standard fields
    private BigDecimal nightly;

    //Standard & Dynamic fields
    private Integer minStay;
    private Integer maxStay;

    // Dynamic fields
    private BigDecimal lowRate;
    private BigDecimal maxRate;
    private Integer lowestOccupancy;
    private Integer maxOccupancy;

    private RatePlanRefEmbeddable ratePlan;
}
