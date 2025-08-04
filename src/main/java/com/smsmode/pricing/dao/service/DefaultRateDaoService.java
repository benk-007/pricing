/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.dao.service;

import com.smsmode.pricing.model.DefaultRateModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
public interface DefaultRateDaoService {

    DefaultRateModel save(DefaultRateModel defaultRateModel);

    /**
     * Finds all default rates for a specific unit with pagination
     */
    Page<DefaultRateModel> findByUnitId(String unitId, Pageable pageable);

    /**
     * Finds a default rate by its ID
     */
    DefaultRateModel findById(String rateId);

    // ========== PRICING CALCULATION EXTENSIONS ==========

    /**
     * Finds default rate with all related data (additional guest fees and day-specific rates) for pricing calculations.
     * This method ensures all collections are eagerly loaded to avoid lazy loading issues during pricing calculations.
     *
     * @param unitId The unit ID to find default rate for
     * @return DefaultRateModel with all related data loaded, or null if not found
     */
    DefaultRateModel findWithRelatedDataForPricing(String unitId);

    /**
     * Finds default rate for a unit without pagination (convenience method for pricing calculations).
     * Returns the first default rate found for the unit.
     *
     * @param unitId The unit ID
     * @return DefaultRateModel or null if not found
     */
    DefaultRateModel findByUnitIdSingle(String unitId);

    /**
     * Checks if a default rate exists for a unit (used for pricing rule resolution).
     *
     * @param unitId The unit ID to check
     * @return true if default rate exists, false otherwise
     */
    boolean existsByUnitId(String unitId);

    DefaultRateModel findOneBy(Specification<DefaultRateModel> specification);

    boolean existsBy(Specification<DefaultRateModel> specification);
}
