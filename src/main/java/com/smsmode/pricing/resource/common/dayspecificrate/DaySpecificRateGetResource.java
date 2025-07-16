package com.smsmode.pricing.resource.common.dayspecificrate;

import lombok.Data;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Resource representing day-specific rate information in GET responses.
 */
@Data
public class DaySpecificRateGetResource {
    private String id;
    private BigDecimal nightly;
    private Set<DayOfWeek> days = new LinkedHashSet<>();
}
