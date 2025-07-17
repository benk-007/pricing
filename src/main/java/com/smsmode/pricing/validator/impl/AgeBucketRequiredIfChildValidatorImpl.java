/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.validator.impl;

import com.smsmode.pricing.enumeration.GuestTypeEnum;
import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeePostResource;
import com.smsmode.pricing.validator.ValidAgeBucketIfChild;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
public class AgeBucketRequiredIfChildValidatorImpl implements ConstraintValidator<ValidAgeBucketIfChild, AdditionalGuestFeePostResource> {

    @Override
    public boolean isValid(AdditionalGuestFeePostResource resource, ConstraintValidatorContext context) {
        if (resource == null) return true; // Let @NotNull handle nulls

        if (resource.getGuestType() == GuestTypeEnum.CHILD && resource.getAgeBucket() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("ageBucket must not be null when guestType is CHILD")
                    .addPropertyNode("ageBucket")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}