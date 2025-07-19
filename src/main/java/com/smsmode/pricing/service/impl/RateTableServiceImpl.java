package com.smsmode.pricing.service.impl;

import com.smsmode.pricing.dao.service.RatePlanDaoService;
import com.smsmode.pricing.dao.service.RateTableDaoService;
import com.smsmode.pricing.exception.ConflictException;
import com.smsmode.pricing.exception.enumeration.ConflictExceptionTitleEnum;
import com.smsmode.pricing.mapper.RateTableMapper;
import com.smsmode.pricing.model.RatePlanModel;
import com.smsmode.pricing.model.RateTableAdditionalGuestFeeModel;
import com.smsmode.pricing.model.RateTableDaySpecificRateModel;
import com.smsmode.pricing.model.RateTableModel;
import com.smsmode.pricing.resource.ratetable.RateTableGetResource;
import com.smsmode.pricing.resource.ratetable.RateTablePatchResource;
import com.smsmode.pricing.resource.ratetable.RateTablePostResource;
import com.smsmode.pricing.service.RateTableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

/**
 * Implementation of RateTableService for managing rate table business operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RateTableServiceImpl implements RateTableService {

    private final RateTableMapper rateTableMapper;
    private final RateTableDaoService rateTableDaoService;
    private final RatePlanDaoService ratePlanDaoService;

    @Override
    public ResponseEntity<RateTableGetResource> create(RateTablePostResource rateTablePostResource) {
        log.debug("Creating rate table: {}", rateTablePostResource.getName());

        // Transform POST resource to model
        RateTableModel rateTableModel = rateTableMapper.postResourceToModel(rateTablePostResource);

        if (rateTablePostResource.getRatePlan() != null) {
            String ratePlanUuid = rateTablePostResource.getRatePlan().getUuid();
            RatePlanModel ratePlan = rateTableMapper.resolveRatePlan(ratePlanUuid);
            rateTableModel.setRatePlan(ratePlan);
        }


        // Validate overlapping dates before saving
        validateOverlapping(rateTableModel, null);

        // Set bidirectional relationships for collections
        setBidirectionalRelationships(rateTableModel);

        // Save to database
        rateTableModel = rateTableDaoService.save(rateTableModel);
        log.info("Successfully created rate table with ID: {}", rateTableModel.getId());

        // Transform model to GET resource
        RateTableGetResource rateTableGetResource = rateTableMapper.modelToGetResource(rateTableModel);

        return ResponseEntity.created(URI.create("")).body(rateTableGetResource);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Page<RateTableGetResource>> getAll(String ratePlanUuid, String search, Pageable pageable) {
        log.debug("Retrieving rate tables with filters - ratePlanUuid: {}, search: {}", ratePlanUuid, search);

        // Get paginated data from database
        Page<RateTableModel> rateTableModelPage = rateTableDaoService.findWithFilters(ratePlanUuid, search, pageable);
        log.info("Retrieved {} rate tables from database", rateTableModelPage.getTotalElements());

        // Transform models to GET resources
        Page<RateTableGetResource> response = rateTableModelPage.map(rateTableMapper::modelToGetResource);

        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<RateTableGetResource> getById(String rateTableId) {
        log.debug("Retrieving rate table by ID: {}", rateTableId);

        // Find by ID (throws exception if not found)
        RateTableModel rateTableModel = rateTableDaoService.findById(rateTableId);

        // Transform model to GET resource
        RateTableGetResource response = rateTableMapper.modelToGetResource(rateTableModel);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<RateTableGetResource> update(String rateTableId, RateTablePatchResource rateTablePatchResource) {
        log.debug("Updating rate table with ID: {}", rateTableId);

        // Find existing rate table
        RateTableModel existingRateTable = rateTableDaoService.findById(rateTableId);
        log.debug("Found existing rate table: {}", existingRateTable.getId());

        // Update model with new data
        rateTableMapper.updateModelFromPatchResource(rateTablePatchResource, existingRateTable);

        // Dans update(), après le mapping PATCH, ajouter :
        if (rateTablePatchResource.getRatePlan() != null) {
            String ratePlanUuid = rateTablePatchResource.getRatePlan().getUuid();
            RatePlanModel ratePlan = rateTableMapper.resolveRatePlan(ratePlanUuid);
            existingRateTable.setRatePlan(ratePlan);
        }

        // Validate overlapping dates (excluding current rate table)
        validateOverlapping(existingRateTable, rateTableId);

        // Update bidirectional relationships for collections
        setBidirectionalRelationships(existingRateTable);

        // Save updated model
        RateTableModel updatedRateTable = rateTableDaoService.save(existingRateTable);
        log.info("Successfully updated rate table with ID: {}", rateTableId);

        // Transform model to GET resource
        RateTableGetResource response = rateTableMapper.modelToGetResource(updatedRateTable);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> delete(String rateTableId) {
        log.debug("Deleting rate table with ID: {}", rateTableId);

        // Find existing rate table (throws exception if not found)
        RateTableModel existingRateTable = rateTableDaoService.findById(rateTableId);

        // Delete from database
        rateTableDaoService.delete(existingRateTable);
        log.info("Successfully deleted rate table with ID: {}", rateTableId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Validates overlapping dates for rate tables of same type and rate plan.
     */
    private void validateOverlapping(RateTableModel rateTableModel, String excludeId) {
        log.debug("Validating overlapping dates for rate table: {}", rateTableModel.getName());

        String ratePlanUuid = rateTableModel.getRatePlan().getId();
        boolean hasOverlap = rateTableDaoService.hasOverlappingRateTables(
                ratePlanUuid,
                rateTableModel.getType(),
                rateTableModel.getStartDate(),
                rateTableModel.getEndDate(),
                excludeId
        );

        if (hasOverlap) {
            log.debug("Overlapping dates detected for rate table type: {} in rate plan: {}",
                    rateTableModel.getType(), ratePlanUuid);
            throw new ConflictException(
                    ConflictExceptionTitleEnum.OVERLAPPING_RATE_TABLE_DATES,
                    "Rate table dates overlap with existing rate table of same type"
            );
        }

        log.debug("No overlapping dates found");
    }

    /**
     * Sets bidirectional relationships for collections.
     */
    private void setBidirectionalRelationships(RateTableModel rateTableModel) {
        if (rateTableModel.getAdditionalGuestFees() != null) {
            for (RateTableAdditionalGuestFeeModel fee : rateTableModel.getAdditionalGuestFees()) {
                fee.setRateTable(rateTableModel);
            }
        }

        if (rateTableModel.getDaySpecificRates() != null) {
            for (RateTableDaySpecificRateModel dayRate : rateTableModel.getDaySpecificRates()) {
                dayRate.setRateTable(rateTableModel);
            }
        }
    }
}