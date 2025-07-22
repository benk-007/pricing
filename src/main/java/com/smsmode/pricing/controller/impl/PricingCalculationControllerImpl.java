package com.smsmode.pricing.controller.impl;

import com.smsmode.pricing.controller.PricingCalculationController;
import com.smsmode.pricing.resource.pricecalculation.PriceCalculationGetResource;
import com.smsmode.pricing.resource.pricecalculation.PriceCalculationPostResource;
import com.smsmode.pricing.service.PricingCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implementation of PricingCalculationController for managing pricing calculation REST endpoints.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class PricingCalculationControllerImpl implements PricingCalculationController {

    private final PricingCalculationService pricingCalculationService;

    @Override
    public ResponseEntity<PriceCalculationGetResource> calculatePricing(PriceCalculationPostResource priceCalculationPostResource) {
        log.debug("POST /price-calculations - Calculating pricing for {} units",
                priceCalculationPostResource.getUnits().size());
        return pricingCalculationService.calculatePricing(priceCalculationPostResource);
    }
}