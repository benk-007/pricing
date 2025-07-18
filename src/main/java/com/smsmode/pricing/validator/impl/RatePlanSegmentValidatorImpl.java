package com.smsmode.pricing.validator.impl;

import com.smsmode.pricing.dao.repository.RatePlanRepository;
import com.smsmode.pricing.dao.specification.RatePlanSpecification;
import com.smsmode.pricing.resource.rateplan.RatePlanPostResource;
import com.smsmode.pricing.validator.ValidRatePlanSegment;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

/**
 * Validator for rate plan segment business rules.
 */
@Slf4j
@RequiredArgsConstructor
public class RatePlanSegmentValidatorImpl implements ConstraintValidator<ValidRatePlanSegment, RatePlanPostResource> {

    private final RatePlanRepository ratePlanRepository;

    @Override
    public boolean isValid(RatePlanPostResource resource, ConstraintValidatorContext context) {
        if (resource == null) return true;

        log.debug("Validating rate plan segment for: {}", resource.getName());

        // Rule 1: First rate plan can have name only
        long totalRatePlans = ratePlanRepository.count();
        log.debug("Total existing rate plans: {}", totalRatePlans);

        if (totalRatePlans == 0) {
            log.debug("First rate plan - no segment validation needed");
            return true;
        }

        // Rule 2: From second rate plan onwards, segment is required
        if (resource.getSegment() == null || ObjectUtils.isEmpty(resource.getSegment().getUuid())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "Segment is required when other rate plans already exist")
                    .addPropertyNode("segmentRef")
                    .addConstraintViolation();
            log.debug("Validation failed: segment required for non-first rate plan");
            return false;
        }

        // Rule 3: If same segment exists, sub-segment is required
        String segmentUuid = resource.getSegment().getUuid();
        boolean segmentExists = ratePlanRepository.exists(RatePlanSpecification.withSegmentUuid(segmentUuid));

        if (segmentExists && (resource.getSubSegment() == null ||
                ObjectUtils.isEmpty(resource.getSubSegment().getUuid()))) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "Sub-segment is required when segment already exists in another rate plan")
                    .addPropertyNode("subSegmentRef")
                    .addConstraintViolation();
            log.debug("Validation failed: sub-segment required for existing segment: {}", segmentUuid);
            return false;
        }

        // Rule 4: No duplicate segment/sub-segment combinations
        if (resource.getSubSegment() != null &&
                !ObjectUtils.isEmpty(resource.getSubSegment().getUuid())) {
            String subSegmentUuid = resource.getSubSegment().getUuid();
            boolean combinationExists = ratePlanRepository.exists(
                    RatePlanSpecification.withSegmentAndSubSegment(segmentUuid, subSegmentUuid));

            if (combinationExists) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                "Rate plan with this segment and sub-segment combination already exists")
                        .addPropertyNode("subSegmentRef")
                        .addConstraintViolation();
                log.debug("Validation failed: duplicate segment/sub-segment combination");
                return false;
            }
        }

        log.debug("Rate plan segment validation passed");
        return true;
    }
}