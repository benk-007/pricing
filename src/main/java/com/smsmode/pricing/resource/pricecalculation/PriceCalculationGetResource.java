package com.smsmode.pricing.resource.pricecalculation;

import com.smsmode.pricing.resource.common.UnitPricingGetResource;
import lombok.Data;

import java.util.List;

@Data
public class PriceCalculationGetResource {
    private List<UnitPricingGetResource> units;
}
