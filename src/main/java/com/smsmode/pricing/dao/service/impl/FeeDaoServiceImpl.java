package com.smsmode.pricing.dao.service.impl;

import com.smsmode.pricing.dao.repository.FeeRepository;
import com.smsmode.pricing.dao.service.FeeDaoService;
import com.smsmode.pricing.model.FeeModel;
import com.smsmode.pricing.model.RatePlanModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
