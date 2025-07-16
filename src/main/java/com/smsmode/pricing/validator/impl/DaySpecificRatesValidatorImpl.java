package com.smsmode.pricing.validator.impl;

import com.smsmode.pricing.resource.common.dayspecificrate.DaySpecificRatePostResource;
import com.smsmode.pricing.resource.defaultrate.DefaultRatePostResource;
import com.smsmode.pricing.validator.ValidDaySpecificRates;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;

/**
 * Validator to ensure no overlapping days in day-specific rates
 */
public class DaySpecificRatesValidatorImpl implements ConstraintValidator<ValidDaySpecificRates, DefaultRatePostResource> {

    @Override
    public boolean isValid(DefaultRatePostResource resource, ConstraintValidatorContext context) {
        if (resource == null || resource.getDaySpecificRates() == null || resource.getDaySpecificRates().isEmpty()) {
            return true; // Let other validators handle null cases
        }

        Set<DayOfWeek> usedDays = new HashSet<>();

        for (DaySpecificRatePostResource dayRate : resource.getDaySpecificRates()) {
            if (dayRate.getDays() != null) {
                for (DayOfWeek day : dayRate.getDays()) {
                    if (!usedDays.add(day)) {
                        // Day already exists in another rate
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate(
                                        "Day " + day + " is used in multiple day-specific rates")
                                .addPropertyNode("daySpecificRates")
                                .addConstraintViolation();
                        return false;
                    }
                }
            }
        }

        return true;
    }
}