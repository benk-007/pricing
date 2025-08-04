/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.controller.impl;

import com.smsmode.pricing.controller.CalculatorController;
import com.smsmode.pricing.resource.calculate.BookingPostResource;
import com.smsmode.pricing.resource.calculate.UnitBookingRateGetResource;
import com.smsmode.pricing.service.RateEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 31 Jul 2025</p>
 */
@RestController
@RequiredArgsConstructor
public class CalculatorControllerImpl implements CalculatorController {

    private final RateEngineService rateEngineService;

    @Override
    public ResponseEntity<Map<String, UnitBookingRateGetResource>> postCalculate(BookingPostResource bookingPostResource) {
        return rateEngineService.calculateBookingRate(bookingPostResource);
    }
}
