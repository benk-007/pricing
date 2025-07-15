/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.dao.service.impl;

import com.smsmode.pricing.dao.repository.DefaultRateRepository;
import com.smsmode.pricing.dao.service.DefaultRateDaoService;
import com.smsmode.pricing.model.DefaultRateModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultRateDaoServiceImpl implements DefaultRateDaoService {

    private final DefaultRateRepository defaultRateRepository;

    @Override
    public DefaultRateModel save(DefaultRateModel defaultRateModel) {
        return defaultRateRepository.save(defaultRateModel);
    }
}
