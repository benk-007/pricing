package com.smsmode.pricing.dao.service;
import com.smsmode.pricing.model.FeeModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface FeeDaoService {

    FeeModel save(FeeModel FeeModel);

    FeeModel findById(String feeId);

    Page<FeeModel> findWithFilters(Set<String> unitIds, String search, Pageable pageable);

    void deleteAllByUnit(String unitId);

    void delete(FeeModel fee);
}
