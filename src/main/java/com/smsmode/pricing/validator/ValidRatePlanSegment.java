package com.smsmode.pricing.validator;

import com.smsmode.pricing.validator.impl.RatePlanSegmentValidatorImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates rate plan segment requirements:
 * - First rate plan can have name only
 * - Second rate plan must have a segment
 * - Same segment requires different sub-segments
 */
@Documented
@Constraint(validatedBy = RatePlanSegmentValidatorImpl.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRatePlanSegment {
    String message() default "Invalid rate plan segment configuration";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}