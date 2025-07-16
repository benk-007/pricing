/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.controller;

import com.smsmode.pricing.resource.defaultrate.DefaultRateGetResource;
import com.smsmode.pricing.resource.defaultrate.DefaultRatePostResource;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@RequestMapping("default-rates")
public interface DefaultRateController {

    @GetMapping
    ResponseEntity<DefaultRateGetResource> getAll(Pageable pageable);

    @PostMapping
    ResponseEntity<DefaultRateGetResource> post(@Valid @RequestBody DefaultRatePostResource defaultRatePostResource);

}
