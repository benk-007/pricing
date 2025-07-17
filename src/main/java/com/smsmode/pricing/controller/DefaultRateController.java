package com.smsmode.pricing.controller;

import com.smsmode.pricing.resource.defaultrate.DefaultRateGetResource;
import com.smsmode.pricing.resource.defaultrate.DefaultRatePostResource;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@RequestMapping("default-rates")
public interface DefaultRateController {

    @GetMapping
    ResponseEntity<DefaultRateGetResource> getAll(@RequestParam String unitId);

    @PostMapping
    ResponseEntity<DefaultRateGetResource> post(@Valid @RequestBody DefaultRatePostResource defaultRatePostResource);

    @PatchMapping("/{rateId}")
    ResponseEntity<DefaultRateGetResource> patch(@PathVariable String rateId, @Valid @RequestBody DefaultRatePostResource defaultRatePatchResource);

}
