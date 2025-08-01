package com.smsmode.pricing.service.impl;

import com.smsmode.pricing.dao.repository.FeeRepository;
import com.smsmode.pricing.dao.service.FeeDaoService;
import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import com.smsmode.pricing.mapper.FeeMapper;
import com.smsmode.pricing.model.FeeModel;
import com.smsmode.pricing.resource.fee.*;
import com.smsmode.pricing.service.FeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
                copiedFee.setType(originalFee.getType());
                copiedFee.setModality(originalFee.getModality());
                copiedFee.setDescription(originalFee.getDescription());
                copiedFee.setActive(originalFee.isActive());
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
            copiedFee.setType(originalFee.getType());
            copiedFee.setModality(originalFee.getModality());
            copiedFee.setDescription(originalFee.getDescription());
            copiedFee.setActive(originalFee.isActive());
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

        feeMapper.updateModelFromPatchResource(feePatchResource, existingFee);

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

}
