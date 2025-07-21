package com.smsmode.pricing.dao.repository;

import com.smsmode.pricing.model.RatePlanModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for RatePlan entity operations.
 */
@Repository
public interface RatePlanRepository extends JpaRepository<RatePlanModel, String>, JpaSpecificationExecutor<RatePlanModel> {
}