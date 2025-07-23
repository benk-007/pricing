package com.smsmode.pricing.service;

import com.smsmode.pricing.resource.common.UnitPricingGetResource;
import com.smsmode.pricing.resource.pricecalculation.PriceCalculationGetResource;
import com.smsmode.pricing.resource.pricecalculation.PriceCalculationPostResource;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Service interface for Pricing Calculation business operations.
 */
public interface PricingCalculationService {

    /**
     * Calculates pricing for multiple units based on stay dates, guests, and segment.
     */
    ResponseEntity<List<UnitPricingGetResource>> calculatePricing(PriceCalculationPostResource request);
}