package com.smsmode.pricing.service.impl;

import com.smsmode.pricing.dao.service.RateTableDaoService;
import com.smsmode.pricing.mapper.RateTableMapper;
import com.smsmode.pricing.model.*;
import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeePostResource;
import com.smsmode.pricing.resource.common.dayspecificrate.DaySpecificRatePostResource;
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
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        handleCollections(rateTableModel, rateTablePostResource);

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

        updateCollections(existingRateTable, rateTablePatchResource);

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

    private void handleCollections(RateTableModel model, RateTablePostResource resource) {
        if (resource.getAdditionalGuestFees() != null) {
            for (AdditionalGuestFeePostResource feeResource : resource.getAdditionalGuestFees()) {
                AdditionalGuestFeeModel fee = rateTableMapper.additionalGuestFeePostResourceToModel(feeResource);
                fee.setRateTable(model);
                model.getAdditionalGuestFees().add(fee);
            }
        }

        if (resource.getDaySpecificRates() != null) {
            for (DaySpecificRatePostResource rateResource : resource.getDaySpecificRates()) {
                DaySpecificRateModel rate = rateTableMapper.daySpecificRatePostResourceToModel(rateResource);
                rate.setRateTable(model);
                model.getDaySpecificRates().add(rate);
            }
        }
    }

    private void updateCollections(RateTableModel existingModel, RateTablePatchResource resource) {
        updateAdditionalGuestFees(existingModel, resource.getAdditionalGuestFees());
        updateDaySpecificRates(existingModel, resource.getDaySpecificRates());
    }


    /**
     * Updates Additional Guest Fees with 3 scenarios:
     * 1. No ID in request -> CREATE new entity
     * 2. ID provided in request -> UPDATE existing entity (preserve UUID)
     * 3. Existing entity not in request -> DELETE entity
     */
    private void updateAdditionalGuestFees(RateTableModel existingRateTable,
                                           List<AdditionalGuestFeePostResource> newFees) {
        log.debug("Updating additional guest fees for rate table");

        List<AdditionalGuestFeeModel> existingFees = existingRateTable.getAdditionalGuestFees();

        if (newFees == null) {
            log.debug("No additional guest fees in PATCH request, keeping existing");
            return;
        }

        if (newFees.isEmpty()) {
            log.debug("Empty fees list in request, removing all existing fees");
            existingFees.clear();
            return;
        }
        Map<String, AdditionalGuestFeeModel> existingById = existingFees.stream()
                .filter(fee -> fee.getId() != null)
                .collect(Collectors.toMap(
                        AdditionalGuestFeeModel::getId,
                        fee -> fee
                ));

        List<AdditionalGuestFeeModel> updatedFees = new ArrayList<>();

        for (AdditionalGuestFeePostResource feeResource : newFees) {
            if (StringUtils.hasText(feeResource.getId())) {
                AdditionalGuestFeeModel existingFee = existingById.get(feeResource.getId());
                if (existingFee != null) {
                    log.debug("Updating existing fee with ID: {}", existingFee.getId());
                    rateTableMapper.updateAdditionalGuestFeeFromResource(feeResource, existingFee);
                    updatedFees.add(existingFee);
                } else {
                    log.warn("Fee with ID {} not found, creating new one", feeResource.getId());
                    AdditionalGuestFeeModel newFee = rateTableMapper.additionalGuestFeePostResourceToModel(feeResource);
                    newFee.setRateTable(existingRateTable);
                    updatedFees.add(newFee);
                }
            } else {
                log.debug("Creating new fee for guestType: {}", feeResource.getGuestType());
                AdditionalGuestFeeModel newFee = rateTableMapper.additionalGuestFeePostResourceToModel(feeResource);
                newFee.setRateTable(existingRateTable);
                updatedFees.add(newFee);
            }
        }

        existingFees.clear();
        existingFees.addAll(updatedFees);

        log.debug("After update - {} fees remaining", existingFees.size());
    }

    /**
     * Updates Day Specific Rates with 3 scenarios:
     * 1. No ID in request -> CREATE new entity
     * 2. ID provided in request -> UPDATE existing entity (preserve UUID)
     * 3. Existing entity not in request -> DELETE entity
     */
    private void updateDaySpecificRates(RateTableModel existingRateTable,
                                        List<DaySpecificRatePostResource> newRates) {
        log.debug("Updating day specific rates for rate table");

        List<DaySpecificRateModel> existingRates = existingRateTable.getDaySpecificRates();

        if (newRates == null) {
            log.debug("No day specific rates in PATCH request, keeping existing");
            return;
        }

        if (newRates.isEmpty()) {
            log.debug("Empty rates list in request, removing all existing rates");
            existingRates.clear();
            return;
        }

        Map<String, DaySpecificRateModel> existingById = existingRates.stream()
                .filter(rate -> rate.getId() != null)
                .collect(Collectors.toMap(
                        DaySpecificRateModel::getId,
                        rate -> rate
                ));

        List<DaySpecificRateModel> updatedRates = new ArrayList<>();

        for (DaySpecificRatePostResource rateResource : newRates) {
            if (StringUtils.hasText(rateResource.getId())) {
                DaySpecificRateModel existingRateModel = existingById.get(rateResource.getId());
                if (existingRateModel != null) {
                    log.debug("Updating existing day rate with ID: {}", existingRateModel.getId());
                    rateTableMapper.updateDaySpecificRateFromResource(rateResource, existingRateModel);
                    updatedRates.add(existingRateModel);
                } else {
                    log.warn("Day rate with ID {} not found, creating new one", rateResource.getId());
                    DaySpecificRateModel newRate = rateTableMapper.daySpecificRatePostResourceToModel(rateResource);
                    newRate.setRateTable(existingRateTable);
                    updatedRates.add(newRate);
                }
            } else {
                log.debug("Creating new day rate for nightly: {}", rateResource.getNightly());
                DaySpecificRateModel newRate = rateTableMapper.daySpecificRatePostResourceToModel(rateResource);
                newRate.setRateTable(existingRateTable);
                updatedRates.add(newRate);
            }
        }

        existingRates.clear();
        existingRates.addAll(updatedRates);

        log.debug("After update - {} day rates remaining", existingRates.size());
    }

}