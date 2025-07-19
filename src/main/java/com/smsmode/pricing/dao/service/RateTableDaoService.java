package com.smsmode.pricing.dao.service;

import com.smsmode.pricing.enumeration.RateTableTypeEnum;
import com.smsmode.pricing.model.RateTableModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

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
}