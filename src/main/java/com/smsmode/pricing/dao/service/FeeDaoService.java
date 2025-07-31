package com.smsmode.pricing.dao.service;

import com.smsmode.pricing.model.FeeModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeeDaoService {

    FeeModel save(FeeModel FeeModel);

    FeeModel findById(String feeId);

    Page<FeeModel> findWithFilters(String unitId, String search, Pageable pageable);
}
