package com.smsmode.pricing.resource.common;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UnitPricingGetResource {
    private String id; // unitId
    private List<NightRateGetResource> nightRates;
    private BigDecimal nightlyRate; // average rate
    private BigDecimal totalAmount;
}
