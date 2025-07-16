/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.validator.impl;

import com.smsmode.pricing.embeddable.AgeBucketEmbeddable;
import com.smsmode.pricing.validator.ValidAgeRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
public class AgeRangeValidatorImpl implements ConstraintValidator<ValidAgeRange, AgeBucketEmbeddable> {

    @Override
    public boolean isValid(AgeBucketEmbeddable ageBucket, ConstraintValidatorContext context) {
        if (ageBucket == null) return true; // Null check handled elsewhere
        if (ageBucket.getFromAge() == null || ageBucket.getToAge() == null) {
            return true; // Let @Positive/@PositiveOrZero handle these
        }
        int from = ageBucket.getFromAge();
        int to = ageBucket.getToAge();

        if (to <= from) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("toAge must be greater than fromAge")
                    .addPropertyNode("toAge")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}