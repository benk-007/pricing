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
                .where(RateTableSpecification.withRatePlanUuid(ratePlanUuid))
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
        log.debug("Checking overlapping rate tables for ratePlan: {}, type: {}, dates: {} to {}",
                ratePlanUuid, type, startDate, endDate);

        Specification<RateTableModel> specification = Specification
                .where(RateTableSpecification.withOverlappingDates(ratePlanUuid, type, startDate, endDate));

        // Exclude current rate table for UPDATE operations
        if (excludeId != null) {
            specification = specification.and(RateTableSpecification.excludingId(excludeId));
        }

        boolean hasOverlap = rateTableRepository.exists(specification);
        log.debug("Overlapping check result: {}", hasOverlap);

        return hasOverlap;
    }
}