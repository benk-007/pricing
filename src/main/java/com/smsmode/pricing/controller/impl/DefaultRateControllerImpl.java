/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.controller.impl;

import com.smsmode.pricing.controller.DefaultRateController;
import com.smsmode.pricing.resource.defaultrate.DefaultRateGetResource;
import com.smsmode.pricing.resource.defaultrate.DefaultRatePostResource;
import com.smsmode.pricing.service.DefaultRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@RestController
@RequiredArgsConstructor
public class DefaultRateControllerImpl implements DefaultRateController {

    private final DefaultRateService defaultRateService;

    @Override
    public ResponseEntity<DefaultRateGetResource> getAll(Pageable pageable) {
        return null;
    }

    @Override
    public ResponseEntity<DefaultRateGetResource> post(DefaultRatePostResource defaultRatePostResource) {
        return defaultRateService.create(defaultRatePostResource);
    }
}
