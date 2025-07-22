package com.smsmode.pricing.service;

import com.smsmode.pricing.resource.pricecalculation.PriceCalculationGetResource;
import com.smsmode.pricing.resource.pricecalculation.PriceCalculationPostResource;
import org.springframework.http.ResponseEntity;

/**
 * Service interface for Pricing Calculation business operations.
 */
public interface PricingCalculationService {

    /**
     * Calculates pricing for multiple units based on stay dates, guests, and segment.
     */
    ResponseEntity<PriceCalculationGetResource> calculatePricing(PriceCalculationPostResource request);
}