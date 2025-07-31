package com.smsmode.pricing.service.impl;

import com.smsmode.pricing.dao.repository.FeeRepository;
import com.smsmode.pricing.dao.service.FeeDaoService;
import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import com.smsmode.pricing.mapper.FeeMapper;
import com.smsmode.pricing.model.FeeModel;
import com.smsmode.pricing.resource.fee.ApplyFeesToUnitsResource;
import com.smsmode.pricing.resource.fee.FeeGetResource;
import com.smsmode.pricing.resource.fee.FeePostResource;
import com.smsmode.pricing.service.FeeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.net.URI;
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
    public ResponseEntity<Void> applyFeesToUnits(ApplyFeesToUnitsResource resource, boolean overwrite) {
        Set<UnitRefEmbeddable> unitRefs = resource.getUnitIds().stream()
                .filter(StringUtils::hasText)
                .map(UnitRefEmbeddable::new)
                .collect(Collectors.toSet());

        if (overwrite) {
            List<FeeModel> allFees = feeRepository.findAll();
            for (FeeModel fee : allFees) {
                fee.getUnits().removeIf(unitRefs::contains);
            }
        }

        for (String feeId : resource.getFeeIds()) {
            FeeModel fee = feeDaoService.findById(feeId);
            fee.getUnits().addAll(unitRefs);
            feeRepository.save(fee);
        }

        return ResponseEntity.noContent().build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Page<FeeGetResource>> getAll(String unitId, String search, Pageable pageable) {
        log.debug("Retrieving fees with filters - unitId: {}, search: {}", unitId, search);

        Page<FeeModel> feeModelPage = feeDaoService.findWithFilters(unitId, search, pageable);
        log.info("Retrieved {} fees from database", feeModelPage.getTotalElements());

        Page<FeeGetResource> response = feeModelPage.map(feeMapper::modelToGetResource);

        return ResponseEntity.ok(response);
    }

}
