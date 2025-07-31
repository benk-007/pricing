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
    public Page<FeeModel> findWithFilters(String unitId, String search, Pageable pageable) {
        log.debug("Finding fees with filters - unitId: {}, search: {}", unitId, search);

        Specification<FeeModel> specification = Specification
                .where(FeeSpecification.withUnitId(unitId))
                .and(FeeSpecification.withNameContaining(search));

        return feeRepository.findAll(specification, pageable);
    }


}
