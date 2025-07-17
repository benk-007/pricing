/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.validator;

import com.smsmode.pricing.validator.impl.UniqueAdultGuestTypeValidatorImpl;
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
@Constraint(validatedBy = UniqueAdultGuestTypeValidatorImpl.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGuestFees {
    String message() default "Invalid additional guest fees";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
