package com.smsmode.pricing.dao.service.impl;

import com.smsmode.pricing.dao.repository.RateTableRepository;
import com.smsmode.pricing.dao.service.RateTableDaoService;
import com.smsmode.pricing.dao.specification.RateTableSpecification;
import com.smsmode.pricing.enumeration.RateTableTypeEnum;
import com.smsmode.pricing.exception.ResourceNotFoundException;
import com.smsmode.pricing.exception.enumeration.ResourceNotFoundExceptionTitleEnum;
import com.smsmode.pricing.model.RateTableModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of RateTableDaoService for managing rate table data access.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateTableDaoServiceImpl implements RateTableDaoService {

    private final RateTableRepository rateTableRepository;

    @Override
    public RateTableModel save(RateTableModel rateTableModel) {
        log.debug("Saving rate table: {}", rateTableModel.getName());
        return rateTableRepository.save(rateTableModel);
    }

    @Override
    public Page<RateTableModel> findWithFilters(String ratePlanUuid, String search, Pageable pageable) {
        log.debug("Finding rate tables with filters - ratePlanUuid: {}, search: {}", ratePlanUuid, search);

        Specification<RateTableModel> specification = Specification
                .where(RateTableSpecification.withRatePlanId(ratePlanUuid))
                .and(RateTableSpecification.withNameContaining(search));

        return rateTableRepository.findAll(specification, pageable);
    }

    @Override
    public RateTableModel findById(String rateTableId) {
        log.debug("Finding rate table by ID: {}", rateTableId);
        return rateTableRepository.findById(rateTableId).orElseThrow(() -> {
            log.debug("Rate table with ID [{}] not found", rateTableId);
            return new ResourceNotFoundException(
                    ResourceNotFoundExceptionTitleEnum.RATE_TABLE_NOT_FOUND,
                    "Rate table with ID [" + rateTableId + "] not found");
        });
    }

    @Override
    public void delete(RateTableModel rateTableModel) {
        log.debug("Deleting rate table: {}", rateTableModel.getId());
        rateTableRepository.delete(rateTableModel);
    }

    @Override
    public boolean hasOverlappingRateTables(String ratePlanUuid, RateTableTypeEnum type,
                                            LocalDate startDate, LocalDate endDate, String excludeId) {
        log.debug("Checking overlapping rate tables for ratePlan: {}, dates: {} to {} (type no longer considered)",
                ratePlanUuid, startDate, endDate);

        Specification<RateTableModel> specification = Specification
                .where(RateTableSpecification.withOverlappingDates(ratePlanUuid, null, startDate, endDate));

        if (excludeId != null) {
            specification = specification.and(RateTableSpecification.excludingId(excludeId));
        }

        boolean hasOverlap = rateTableRepository.exists(specification);
        log.debug("Overlapping check result: {}", hasOverlap);

        return hasOverlap;
    }

    // ========== PRICING CALCULATION IMPLEMENTATIONS ==========

    @Override
    public List<RateTableModel> findCoveringRateTables(String ratePlanId, LocalDate checkinDate, LocalDate checkoutDate) {
        log.debug("Finding covering rate tables for rate plan: {} between {} and {}", ratePlanId, checkinDate, checkoutDate);

        try {
            // Step 1: Build specification for rate tables that overlap with the stay period
            // A rate table overlaps if: startDate <= checkoutDate AND endDate >= checkinDate
            Specification<RateTableModel> specification = Specification
                    .where(RateTableSpecification.withRatePlanId(ratePlanId))
                    .and(RateTableSpecification.withDateRangeCoverage(checkinDate, checkoutDate));

            // Step 2: Execute query and sort by start date
            List<RateTableModel> coveringTables = rateTableRepository.findAll(specification)
                    .stream()
                    .sorted(Comparator.comparing(RateTableModel::getStartDate))
                    .collect(Collectors.toList());

            log.debug("Found {} rate tables covering the period", coveringTables.size());
            return coveringTables;

        } catch (Exception e) {
            log.error("Error finding covering rate tables for rate plan: {}", ratePlanId, e);
            return List.of(); // Return empty list instead of null
        }
    }

    @Override
    public RateTableModel findRateTableForDate(String ratePlanId, LocalDate date) {
        log.debug("Finding rate table for rate plan: {} on date: {}", ratePlanId, date);

        try {
            // Step 1: Build specification for rate table covering specific date
            Specification<RateTableModel> specification = Specification
                    .where(RateTableSpecification.withRatePlanId(ratePlanId))
                    .and(RateTableSpecification.withSpecificDateCoverage(date));

            // Step 2: Find first matching rate table
            List<RateTableModel> matchingTables = rateTableRepository.findAll(specification);

            if (matchingTables.isEmpty()) {
                log.debug("No rate table found covering date: {} for rate plan: {}", date, ratePlanId);
                return null;
            }

            // Step 3: Return first match (there should be only one due to no-overlap constraint)
            RateTableModel coveringTable = matchingTables.get(0);
            log.debug("Found rate table: {} covering date: {}", coveringTable.getName(), date);

            return coveringTable;

        } catch (Exception e) {
            log.error("Error finding rate table for date: {} in rate plan: {}", date, ratePlanId, e);
            return null;
        }
    }

    @Override
    public List<RateTableModel> findByRatePlanId(String ratePlanId) {
        log.debug("Finding all rate tables for rate plan: {}", ratePlanId);

        try {
            // Step 1: Build specification for all rate tables of a rate plan
            Specification<RateTableModel> specification = RateTableSpecification.withRatePlanId(ratePlanId);

            // Step 2: Execute query and sort by start date
            List<RateTableModel> rateTables = rateTableRepository.findAll(specification)
                    .stream()
                    .sorted(Comparator.comparing(RateTableModel::getStartDate))
                    .collect(Collectors.toList());

            log.debug("Found {} rate tables for rate plan: {}", rateTables.size(), ratePlanId);
            return rateTables;

        } catch (Exception e) {
            log.error("Error finding rate tables for rate plan: {}", ratePlanId, e);
            return List.of(); // Return empty list instead of null
        }
    }

    @Override
    public boolean existsBy(Specification<RateTableModel> specification) {

        return rateTableRepository.exists(specification);
    }

    @Override
    public RateTableModel findOneBy(Specification<RateTableModel> specification) {
        return rateTableRepository.findOne(specification).orElseThrow(() -> {
            log.warn("No rate table found corresponding to specification. Will throw an error ...");
            return new ResourceNotFoundException(
                    ResourceNotFoundExceptionTitleEnum.RATE_TABLE_NOT_FOUND,
                    "No rate table found based on your criteria");
        });
    }
}