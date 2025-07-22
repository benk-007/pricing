package com.smsmode.pricing.resource.common;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NightRateGetResource {
    private String date; // Format "DD-MM-YYYY"
    private BigDecimal rate;
}
