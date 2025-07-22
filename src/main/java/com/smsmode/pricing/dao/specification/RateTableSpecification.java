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
                        criteriaBuilder.equal(root.get("ratePlan").get("id"), ratePlanUuid);
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
            var ratePlanCondition = criteriaBuilder.equal(root.get("ratePlan").get("id"), ratePlanUuid);
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

    // ========== PRICING CALCULATION SPECIFICATIONS ==========

    /**
     * Creates specification to find rate tables that cover (overlap with) a date range.
     * Used for finding rate tables that apply to a stay period.
     *
     * Coverage Logic:
     * - A rate table covers a date range if there's any overlap
     * - Overlap exists when: rateTable.startDate <= checkoutDate AND rateTable.endDate >= checkinDate
     *
     * @param checkinDate The check-in date
     * @param checkoutDate The check-out date (exclusive)
     * @return Specification for rate tables covering the date range
     */
    public static Specification<RateTableModel> withDateRangeCoverage(LocalDate checkinDate, LocalDate checkoutDate) {
        return (root, query, criteriaBuilder) -> {
            if (checkinDate == null || checkoutDate == null) {
                return criteriaBuilder.conjunction();
            }

            // Coverage logic: rate table overlaps with stay period
            // startDate <= checkoutDate AND endDate >= checkinDate
            return criteriaBuilder.and(
                    criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), checkoutDate),
                    criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), checkinDate)
            );
        };
    }

    /**
     * Creates specification to find rate table that covers a specific date.
     * Used for finding the rate table that applies to a particular night.
     *
     * Coverage Logic:
     * - A rate table covers a date if: rateTable.startDate <= date <= rateTable.endDate
     *
     * @param date The specific date to check coverage for
     * @return Specification for rate table covering the specific date
     */
    public static Specification<RateTableModel> withSpecificDateCoverage(LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return criteriaBuilder.conjunction();
            }

            // Specific date coverage: date is within rate table's date range
            // startDate <= date <= endDate
            return criteriaBuilder.and(
                    criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), date),
                    criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), date)
            );
        };
    }

    /**
     * Creates specification to find rate tables that are active (not expired).
     * Used for filtering out past rate tables.
     *
     * @param referenceDate The reference date (usually today)
     * @return Specification for active rate tables
     */
    public static Specification<RateTableModel> withActiveRateTables(LocalDate referenceDate) {
        return (root, query, criteriaBuilder) -> {
            if (referenceDate == null) {
                return criteriaBuilder.conjunction();
            }

            // Active rate tables: endDate >= referenceDate
            return criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), referenceDate);
        };
    }

    /**
     * Creates specification to find rate tables by type for pricing calculations.
     * Currently focuses on STANDARD type, DYNAMIC will be implemented later.
     *
     * @param type The rate table type (STANDARD, DYNAMIC)
     * @return Specification for rate tables of specific type
     */
    public static Specification<RateTableModel> withTypeForPricing(RateTableTypeEnum type) {
        return (root, query, criteriaBuilder) -> {
            if (type == null) {
                // Default to STANDARD type for pricing calculations
                return criteriaBuilder.equal(root.get("type"), RateTableTypeEnum.STANDARD);
            }

            return criteriaBuilder.equal(root.get("type"), type);
        };
    }
}