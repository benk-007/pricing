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
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
public class UniqueAdultGuestTypeValidatorImpl implements ConstraintValidator<ValidGuestFees, DefaultRatePostResource> {

    @Override
    public boolean isValid(DefaultRatePostResource resource, ConstraintValidatorContext context) {
        if (resource == null || resource.getAdditionalGuestFees() == null) return true;

        Set<AdditionalGuestFeePostResource> fees = resource.getAdditionalGuestFees();

        // ✅ Check 1: Only one ADULT
        long adultCount = fees.stream()
                .filter(fee -> GuestTypeEnum.ADULT.equals(fee.getGuestType()))
                .count();

        if (adultCount > 1) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Only one AdditionalGuestFee with guestType ADULT is allowed")
                    .addConstraintViolation();
            return false;
        }

        List<AdditionalGuestFeePostResource> children = fees.stream()
                .filter(fee -> fee.getGuestType() == GuestTypeEnum.CHILD)
                .filter(fee -> fee.getAgeBucket() != null)
                .collect(Collectors.toList());

        // Sort ranges by fromAge
        children.sort(Comparator.comparingInt(f -> f.getAgeBucket().getFromAge()));

        for (int i = 0; i < children.size() - 1; i++) {
            AgeBucketEmbeddable current = children.get(i).getAgeBucket();
            AgeBucketEmbeddable next = children.get(i + 1).getAgeBucket();

            // Check if ranges overlap or are adjacent (e.g., 0-2 and 2-4) — adjust as per your policy
            if (current.getToAge() == null || next.getFromAge() == null) continue;
            if (current.getToAge() >= next.getFromAge()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Overlapping age buckets for CHILD guests")
                        .addPropertyNode("additionalGuestFees")
                        .addConstraintViolation();
                return false;
            }
        }


        return true;
    }
}