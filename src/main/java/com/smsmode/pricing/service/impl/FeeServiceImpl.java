package com.smsmode.pricing.service.impl;

import com.smsmode.pricing.dao.repository.FeeRepository;
import com.smsmode.pricing.dao.service.FeeDaoService;
import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import com.smsmode.pricing.enumeration.FeeModalityEnum;
import com.smsmode.pricing.mapper.FeeMapper;
import com.smsmode.pricing.model.AdditionalGuestFeeModel;
import com.smsmode.pricing.model.FeeModel;
import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeePostResource;
import com.smsmode.pricing.resource.fee.*;
import com.smsmode.pricing.service.FeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeeServiceImpl implements FeeService {

    private final FeeDaoService feeDaoService;
    private final FeeMapper feeMapper;
    private final FeeRepository feeRepository;

    @Override
    @Transactional
    public ResponseEntity<FeeGetResource> create(FeePostResource feePostResource) {
        log.debug("Creating fee: {}", feePostResource.getName());

        FeeModel feeModel = feeMapper.postResourceToModel(feePostResource);

        // Link additional guest prices to fee if modality supports it
        if (shouldHaveAdditionalGuestPrices(feeModel.getModality()) &&
                !CollectionUtils.isEmpty(feeModel.getAdditionalGuestPrices())) {
            log.debug("Linking additional guest price models to fee ...");
            for (AdditionalGuestFeeModel price : feeModel.getAdditionalGuestPrices()) {
                price.setFee(feeModel);
            }
        }

        feeModel = feeDaoService.save(feeModel);
        log.info("Successfully created fee with ID: {}", feeModel.getId());

        FeeGetResource feeGetResource = feeMapper.modelToGetResource(feeModel);

        return ResponseEntity.created(URI.create("")).body(feeGetResource);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> copyFeesToUnits(CopyFeesToUnitsResource resource, boolean overwrite) {
        List<UnitRefEmbeddable> targetUnits = resource.getUnitIds().stream()
                .filter(StringUtils::hasText)
                .map(UnitRefEmbeddable::new)
                .toList();

        if (overwrite) {
            for (UnitRefEmbeddable target : targetUnits) {
                feeDaoService.deleteAllByUnit(target.getId());
            }
        }

        for (String feeId : resource.getFeeIds()) {
            FeeModel originalFee = feeDaoService.findById(feeId);

            for (UnitRefEmbeddable target : targetUnits) {
                FeeModel copiedFee = new FeeModel();
                copiedFee.setName(originalFee.getName());
                copiedFee.setAmount(originalFee.getAmount());
                copiedFee.setModality(originalFee.getModality());
                copiedFee.setDescription(originalFee.getDescription());
                copiedFee.setActive(originalFee.isActive());
                copiedFee.setRequired(originalFee.isRequired());
                copiedFee.setUnit(target);

                feeDaoService.save(copiedFee);
            }
        }

        return ResponseEntity.noContent().build();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> copyFeesFromUnits(CopyFeesFromUnitsResource resource, boolean overwrite) {
        String targetUnitId = resource.getUnitId();
        UnitRefEmbeddable targetUnit = new UnitRefEmbeddable(targetUnitId);

        if (overwrite) {
            feeDaoService.deleteAllByUnit(targetUnitId);
        }

        for (String feeId : resource.getFeeIds()) {
            FeeModel originalFee = feeDaoService.findById(feeId);

            FeeModel copiedFee = new FeeModel();
            copiedFee.setName(originalFee.getName());
            copiedFee.setAmount(originalFee.getAmount());
            copiedFee.setModality(originalFee.getModality());
            copiedFee.setDescription(originalFee.getDescription());
            copiedFee.setActive(originalFee.isActive());
            copiedFee.setRequired(originalFee.isRequired());
            copiedFee.setUnit(targetUnit);

            feeDaoService.save(copiedFee);
        }

        return ResponseEntity.noContent().build();
    }



    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Page<FeeGetResource>> getAll(Set<String> unitIds, String search, Pageable pageable) {
        log.debug("Retrieving fees with filters - unitIds: {}, search: {}", unitIds, search);

        Page<FeeModel> feeModelPage = feeDaoService.findWithFilters(unitIds, search, pageable);
        log.info("Retrieved {} fees from database", feeModelPage.getTotalElements());

        Page<FeeGetResource> response = feeModelPage.map(feeMapper::modelToGetResource);

        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional
    public ResponseEntity<FeeGetResource> update(String feeId, FeePatchResource feePatchResource) {
        log.debug("Updating fee with ID: {}", feeId);

        FeeModel existingFee = feeDaoService.findById(feeId);
        log.debug("Found existing fee: {}", existingFee.getId());

        // Store old modality to detect changes
        FeeModalityEnum oldModality = existingFee.getModality();

        feeMapper.updateModelFromPatchResource(feePatchResource, existingFee);

        // Handle additional guest prices based on modality
        handleAdditionalGuestPricesUpdate(existingFee, feePatchResource, existingFee.getModality());

        FeeModel updatedFee = feeDaoService.save(existingFee);
        log.info("Successfully updated fee with ID: {}", feeId);

        FeeGetResource response = feeMapper.modelToGetResource(updatedFee);
        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> delete(String feeId) {
        log.debug("Deleting fee with ID: {}", feeId);

        FeeModel fee = feeDaoService.findById(feeId);
        feeDaoService.delete(fee);

        log.info("Successfully deleted fee with ID: {}", feeId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Checks if a fee modality should have additional guest prices
     */
    private boolean shouldHaveAdditionalGuestPrices(FeeModalityEnum modality) {
        return modality == FeeModalityEnum.PER_PERSON ||
                modality == FeeModalityEnum.PER_PERSON_PER_NIGHT;
    }

    /**
     * Handles additional guest prices update based on modality changes
     */
    private void handleAdditionalGuestPricesUpdate(FeeModel existingFee,
                                                   FeePatchResource feePatchResource,
                                                   FeeModalityEnum oldModality) {
        FeeModalityEnum newModality = feePatchResource.getModality() != null ?
                feePatchResource.getModality() : oldModality;

        if (!shouldHaveAdditionalGuestPrices(newModality)) {
            log.debug("New modality {} doesn't support additional guest prices, clearing all", newModality);
            existingFee.getAdditionalGuestPrices().clear();
        } else {
            // Always update additional guest prices when provided
            updateAdditionalGuestPrices(existingFee,
                    feePatchResource.getAdditionalGuestPrices() != null ?
                            feePatchResource.getAdditionalGuestPrices() : new ArrayList<>());
        }
    }

    /**
     * Updates Additional Guest Prices with 3 scenarios:
     * 1. No ID in request -> CREATE new entity
     * 2. ID provided in request -> UPDATE existing entity (preserve UUID)
     * 3. Existing entity not in request -> DELETE entity
     */
    private void updateAdditionalGuestPrices(FeeModel existingFee,
                                             List<AdditionalGuestFeePostResource> newPrices) {
        log.debug("Updating additional guest prices");

        List<AdditionalGuestFeeModel> existingPrices = existingFee.getAdditionalGuestPrices();

        // If newPrices is null, remove all existing prices
        if (CollectionUtils.isEmpty(newPrices)) {
            log.debug("No prices in request, removing all existing prices");
            existingPrices.clear();
            return;
        }

        // Create a map of existing entities by ID for fast lookup
        Map<String, AdditionalGuestFeeModel> existingById = existingPrices.stream()
                .collect(Collectors.toMap(
                        AdditionalGuestFeeModel::getId,
                        price -> price
                ));

        // Create a new collection for updated prices
        List<AdditionalGuestFeeModel> updatedPrices = new ArrayList<>();

        // Process each price in the request
        for (AdditionalGuestFeePostResource priceResource : newPrices) {
            if (StringUtils.hasText(priceResource.getId())) {
                // Scenario 2: ID provided = UPDATE existing entity
                AdditionalGuestFeeModel existingPrice = existingById.get(priceResource.getId());
                if (existingPrice != null) {
                    log.debug("Updating existing price with ID: {}", existingPrice.getId());

                    feeMapper.updateAdditionalGuestFeeFromResource(priceResource, existingPrice);

                    updatedPrices.add(existingPrice);
                } else {
                    log.warn("Price with ID {} not found, creating new one", priceResource.getId());
                    // ID provided but entity not found = create new entity
                    AdditionalGuestFeeModel newPrice = feeMapper.additionalGuestFeePostResourceToModel(priceResource);
                    newPrice.setFee(existingFee);
                    updatedPrices.add(newPrice);
                }
            } else {
                // Scenario 1: No ID = CREATE new entity
                log.debug("Creating new price for guestType: {}", priceResource.getGuestType());
                AdditionalGuestFeeModel newPrice = feeMapper.additionalGuestFeePostResourceToModel(priceResource);
                newPrice.setFee(existingFee);
                updatedPrices.add(newPrice);
            }
        }

        // Replace existing collection with new one
        existingPrices.clear();
        existingPrices.addAll(updatedPrices);

        log.debug("After update - {} prices remaining", existingPrices.size());
    }
}
