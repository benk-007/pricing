package com.smsmode.pricing.resource.common.dayspecificrate;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Resource representing day-specific rate information in POST/PATCH requests.
 */
@Data
public class DaySpecificRatePostResource {

    // ID is optional: null for CREATE, provided for UPDATE
    private String id;

    @NotNull(message = "Nightly rate is required")
    @Positive(message = "Nightly rate must be positive")
    private BigDecimal nightly;

    @NotEmpty(message = "At least one day must be selected")
    private Set<DayOfWeek> days = new LinkedHashSet<>();
}