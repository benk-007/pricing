package com.smsmode.pricing.dao.repository;

import com.smsmode.pricing.model.FeeModel;
import com.smsmode.pricing.model.RatePlanModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeRepository extends JpaRepository<FeeModel, String>, JpaSpecificationExecutor<FeeModel> {
}
