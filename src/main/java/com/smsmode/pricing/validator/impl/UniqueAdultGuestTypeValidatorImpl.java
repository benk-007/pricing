/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.validator.impl;

import com.smsmode.pricing.embeddable.AgeBucketEmbeddable;
import com.smsmode.pricing.enumeration.GuestTypeEnum;
import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeePostResource;
import com.smsmode.pricing.resource.defaultrate.DefaultRatePostResource;
import com.smsmode.pricing.validator.ValidGuestFees;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validator for additional guest fees ensuring:
 * 1. Only one ADULT guest type is allowed
 * 2. No overlapping age buckets for CHILD guest types
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
public class UniqueAdultGuestTypeValidatorImpl implements ConstraintValidator<ValidGuestFees, DefaultRatePostResource> {

    @Override
    public boolean isValid(DefaultRatePostResource resource, ConstraintValidatorContext context) {
        if (resource == null || resource.getAdditionalGuestFees() == null) {
            return true;
        }

        List<AdditionalGuestFeePostResource> fees = resource.getAdditionalGuestFees();
        boolean isValid = true;

        // Check 1: Only one ADULT guest type allowed
        long adultCount = fees.stream()
                .filter(fee -> GuestTypeEnum.ADULT.equals(fee.getGuestType()))
                .count();

        if (adultCount > 1) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Only one additional guest fee with ADULT guest type is allowed")
                    .addPropertyNode("additionalGuestFees")
                    .addConstraintViolation();
            isValid = false;
        }

        // Check 2: No overlapping age buckets for CHILD guest types
        List<AdditionalGuestFeePostResource> children = fees.stream()
                .filter(fee -> GuestTypeEnum.CHILD.equals(fee.getGuestType()))
                .filter(fee -> fee.getAgeBucket() != null)
                .filter(fee -> fee.getAgeBucket().getFromAge() != null && fee.getAgeBucket().getToAge() != null)
                .collect(Collectors.toList());

        // Sort children by fromAge for easier overlap detection
        children.sort(Comparator.comparing(f -> f.getAgeBucket().getFromAge()));

        for (int i = 0; i < children.size() - 1; i++) {
            AgeBucketEmbeddable current = children.get(i).getAgeBucket();
            AgeBucketEmbeddable next = children.get(i + 1).getAgeBucket();

            // Check for overlap: current.toAge >= next.fromAge
            if (current.getToAge() >= next.getFromAge()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                String.format("Overlapping age buckets detected: [%d-%d] and [%d-%d]",
                                        current.getFromAge(), current.getToAge(),
                                        next.getFromAge(), next.getToAge()))
                        .addPropertyNode("additionalGuestFees")
                        .addConstraintViolation();
                isValid = false;
                break; // Stop at first overlap found
            }
        }

        return isValid;
    }
}