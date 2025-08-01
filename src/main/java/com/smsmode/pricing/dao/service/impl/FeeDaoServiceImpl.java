package com.smsmode.pricing.dao.service.impl;

import com.smsmode.pricing.dao.repository.FeeRepository;
import com.smsmode.pricing.dao.service.FeeDaoService;
import com.smsmode.pricing.dao.specification.FeeSpecification;
import com.smsmode.pricing.exception.ResourceNotFoundException;
import com.smsmode.pricing.exception.enumeration.ResourceNotFoundExceptionTitleEnum;
import com.smsmode.pricing.model.FeeModel;
import com.smsmode.pricing.model.RatePlanModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeeDaoServiceImpl implements FeeDaoService {

    private final FeeRepository feeRepository;

    @Override
    public FeeModel save(FeeModel FeeModel) {
        log.debug("Saving rate plan: {}", FeeModel.getName());
        return feeRepository.save(FeeModel);
    }

    @Override
    public FeeModel findById(String feeId) {
        log.debug("Finding fee by ID: {}", feeId);
        return feeRepository.findById(feeId).orElseThrow(() -> {
            log.debug("Fee with ID [{}] not found", feeId);
            return new ResourceNotFoundException(
                    ResourceNotFoundExceptionTitleEnum.FEE_NOT_FOUND,
                    "Fee with ID [" + feeId + "] not found");
        });
    }

    @Override
    public Page<FeeModel> findWithFilters(Set<String> unitIds, String search, Pageable pageable) {
        log.debug("Finding fees with filters - unitIds: {}, search: {}", unitIds, search);

        Specification<FeeModel> specification = Specification
                .where(FeeSpecification.withUnitIds(unitIds))
                .and(FeeSpecification.withNameContaining(search));

        return feeRepository.findAll(specification, pageable);
    }

    @Override
    @Transactional
    public void deleteAllByUnit(String unitId) {
        log.debug("Deleting all fees for unitId: {}", unitId);
        Specification<FeeModel> spec = FeeSpecification.withUnitId(unitId);
        List<FeeModel> feesToDelete = feeRepository.findAll(spec);
        feeRepository.deleteAll(feesToDelete);
    }

    @Override
    public void delete(FeeModel fee) {
        feeRepository.delete(fee);
    }

}
