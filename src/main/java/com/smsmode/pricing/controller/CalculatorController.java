/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.controller;

import com.smsmode.pricing.resource.calculate.BookingPostResource;
import com.smsmode.pricing.resource.calculate.UnitBookingRateGetResource;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 31 Jul 2025</p>
 */
@RequestMapping("/calculate")
public interface CalculatorController {

    @PostMapping
    ResponseEntity<Map<String, UnitBookingRateGetResource>> postCalculate(@Valid @RequestBody BookingPostResource bookingPostResource);

}
