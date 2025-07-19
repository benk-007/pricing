package com.smsmode.pricing.validator.impl;

import com.smsmode.pricing.dao.repository.RateTableRepository;
import com.smsmode.pricing.dao.specification.RateTableSpecification;
import com.smsmode.pricing.resource.ratetable.RateTablePostResource;
import com.smsmode.pricing.validator.ValidRateTableDates;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RateTableDatesValidatorImpl implements ConstraintValidator<ValidRateTableDates, RateTablePostResource> {

    private final RateTableRepository rateTableRepository;

    @Override
    public boolean isValid(RateTablePostResource resource, ConstraintValidatorContext context) {
        if (resource == null || resource.getRatePlan() == null) return true;

        // Check overlapping logic
        boolean hasOverlap = rateTableRepository.exists(
                RateTableSpecification.withOverlappingDates(
                        resource.getRatePlan().getUuid(),
                        resource.getType(),
                        resource.getStartDate(),
                        resource.getEndDate()
                )
        );

        if (hasOverlap) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            String.format("Rate table of type %s with dates %s to %s overlaps with existing rate table",
                                    resource.getType(), resource.getStartDate(), resource.getEndDate()))
                    .addPropertyNode("startDate")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
