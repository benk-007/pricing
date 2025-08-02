/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.service;

import com.smsmode.pricing.resource.calculate.BookingPostResource;
import com.smsmode.pricing.resource.calculate.UnitBookingRateGetResource;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 01 Aug 2025</p>
 */
public interface RateEngineService {

    ResponseEntity<Map<String, UnitBookingRateGetResource>> calculateBookingRate(BookingPostResource bookingPostResource);

}
