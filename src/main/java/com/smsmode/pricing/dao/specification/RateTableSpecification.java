package com.smsmode.pricing.dao.specification;

import com.smsmode.pricing.enumeration.RateTableTypeEnum;
import com.smsmode.pricing.model.RateTableModel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;

/**
 * Specification class for RateTableModel queries.
 */
public class RateTableSpecification {

    /**
     * Creates specification to find rate tables by rate plan UUID.
     */
    public static Specification<RateTableModel> withRatePlanUuid(String ratePlanUuid) {
        return (root, query, criteriaBuilder) ->
                ObjectUtils.isEmpty(ratePlanUuid) ? criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("ratePlan").get("uuid"), ratePlanUuid);
    }

    /**
     * Creates specification to search by rate table name.
     */
    public static Specification<RateTableModel> withNameContaining(String name) {
        return (root, query, criteriaBuilder) ->
                ObjectUtils.isEmpty(name) ? criteriaBuilder.conjunction() :
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")),
                                "%" + name.toLowerCase() + "%"
                        );
    }

    /**
     * Creates specification to find rate tables by type.
     */
    public static Specification<RateTableModel> withType(RateTableTypeEnum type) {
        return (root, query, criteriaBuilder) ->
                type == null ? criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("type"), type);
    }

    /**
     * Creates specification to find overlapping rate tables by rate plan and type.
     * For validation: finds rate tables of same type and rate plan that overlap with given dates.
     */
    public static Specification<RateTableModel> withOverlappingDates(String ratePlanUuid, RateTableTypeEnum type, LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (ObjectUtils.isEmpty(ratePlanUuid) || type == null || startDate == null || endDate == null) {
                return criteriaBuilder.conjunction();
            }

            // Same rate plan and same type
            var ratePlanCondition = criteriaBuilder.equal(root.get("ratePlan").get("uuid"), ratePlanUuid);
            var typeCondition = criteriaBuilder.equal(root.get("type"), type);

            // Overlapping logic: NOT (endDate < other.startDate OR startDate > other.endDate)
            // Which is equivalent to: (endDate >= other.startDate AND startDate <= other.endDate)
            var overlapCondition = criteriaBuilder.and(
                    criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), endDate),
                    criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), startDate)
            );

            return criteriaBuilder.and(ratePlanCondition, typeCondition, overlapCondition);
        };
    }

    /**
     * Creates specification to exclude a specific rate table by ID.
     * Used in UPDATE operations to exclude the current rate table from overlap validation.
     */
    public static Specification<RateTableModel> excludingId(String excludeId) {
        return (root, query, criteriaBuilder) ->
                ObjectUtils.isEmpty(excludeId) ? criteriaBuilder.conjunction() :
                        criteriaBuilder.notEqual(root.get("id"), excludeId);
    }
}