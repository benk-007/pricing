package com.smsmode.pricing.validator;

import com.smsmode.pricing.validator.impl.DaySpecificRatesValidatorImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation for day-specific rates to ensure no overlapping days
 */
@Documented
@Constraint(validatedBy = DaySpecificRatesValidatorImpl.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDaySpecificRates {
    String message() default "Overlapping days found in day-specific rates";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}