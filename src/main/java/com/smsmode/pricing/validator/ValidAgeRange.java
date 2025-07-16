/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.validator;

import com.smsmode.pricing.validator.impl.AgeRangeValidatorImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@Documented
@Constraint(validatedBy = AgeRangeValidatorImpl.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAgeRange {
    String message() default "toAge must be greater than fromAge";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
