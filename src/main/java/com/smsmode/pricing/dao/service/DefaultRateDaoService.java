/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.dao.service;

import com.smsmode.pricing.model.DefaultRateModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


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
}
