/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.service.impl;

import com.smsmode.pricing.dao.service.DefaultRateDaoService;
import com.smsmode.pricing.mapper.DefaultRateMapper;
import com.smsmode.pricing.model.DefaultRateModel;
import com.smsmode.pricing.resource.defaultrate.DefaultRateGetResource;
import com.smsmode.pricing.resource.defaultrate.DefaultRatePostResource;
import com.smsmode.pricing.service.DefaultRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
public class DefaultRateServiceImpl implements DefaultRateService {

    private final DefaultRateMapper defaultRateMapper;
    private final DefaultRateDaoService defaultRateDaoService;


    @Override
    public ResponseEntity<DefaultRateGetResource> create(DefaultRatePostResource defaultRatePostResource) {
        DefaultRateModel defaultRateModel = defaultRateMapper.postResourceToModel(defaultRatePostResource);
        defaultRateModel = defaultRateDaoService.save(defaultRateModel);
        return ResponseEntity.ok(defaultRateMapper.modelToGetResource(defaultRateModel));
    }
}
