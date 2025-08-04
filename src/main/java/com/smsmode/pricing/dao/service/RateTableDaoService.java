package com.smsmode.pricing.dao.service;

import com.smsmode.pricing.enumeration.RateTableTypeEnum;
import com.smsmode.pricing.model.RateTableModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

/**
 * DAO Service interface for RateTable data access operations.
 */
public interface RateTableDaoService {

    /**
     * Saves a rate table.
     */
    RateTableModel save(RateTableModel rateTableModel);

    /**
     * Finds rate tables with filters and pagination.
     */
    Page<RateTableModel> findWithFilters(String ratePlanUuid, String search, Pageable pageable);

    /**
     * Finds a rate table by its ID.
     */
    RateTableModel findById(String rateTableId);

    /**
     * Deletes a rate table.
     */
    void delete(RateTableModel rateTableModel);

    /**
     * Checks if there are overlapping rate tables for validation.
     * Returns true if overlapping rate tables exist for the same rate plan and type.
     */
    boolean hasOverlappingRateTables(String ratePlanUuid, RateTableTypeEnum type, LocalDate startDate, LocalDate endDate, String excludeId);

    // ========== PRICING CALCULATION EXTENSIONS ==========

    /**
     * Finds rate tables that cover (partially or fully) the specified date range for a rate plan.
     *
     * Coverage Logic:
     * - A rate table covers a date if: rateTable.startDate <= date <= rateTable.endDate
     * - Returns all rate tables that have any overlap with the stay period
     * - Results are sorted by startDate for easier processing
     *
     * @param ratePlanId The rate plan ID to search rate tables for
     * @param checkinDate The check-in date
     * @param checkoutDate The check-out date (exclusive)
     * @return List of rate tables that cover any part of the stay period, sorted by start date
     */
    List<RateTableModel> findCoveringRateTables(String ratePlanId, LocalDate checkinDate, LocalDate checkoutDate);

    /**
     * Finds the rate table that covers a specific date for a rate plan.
     *
     * @param ratePlanId The rate plan ID
     * @param date The specific date to find coverage for
     * @return The rate table covering this date, or null if none found
     */
    RateTableModel findRateTableForDate(String ratePlanId, LocalDate date);

    /**
     * Finds all rate tables for a rate plan (used for pricing rule resolution).
     *
     * @param ratePlanId The rate plan ID
     * @return List of all rate tables for this rate plan
     */
    List<RateTableModel> findByRatePlanId(String ratePlanId);

    boolean existsBy(Specification<RateTableModel> specification);

    RateTableModel findOneBy(Specification<RateTableModel> specification);
}