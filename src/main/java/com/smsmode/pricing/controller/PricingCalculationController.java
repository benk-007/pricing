package com.smsmode.pricing.controller;

import com.smsmode.pricing.resource.pricecalculation.PriceCalculationGetResource;
import com.smsmode.pricing.resource.pricecalculation.PriceCalculationPostResource;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller interface for Pricing Calculation REST endpoints.
 */
@RequestMapping("price-calculations")
public interface PricingCalculationController {

    /**
     * Calculates pricing for units based on check-in/check-out dates, guests, and segment.
     */
    @PostMapping
    ResponseEntity<PriceCalculationGetResource> calculatePricing(
            @Valid @RequestBody PriceCalculationPostResource priceCalculationPostResource);
}