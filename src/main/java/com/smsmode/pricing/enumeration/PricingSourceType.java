package com.smsmode.pricing.enumeration;

/**
 * Enumeration for pricing rule source types.
 * Used to distinguish between different pricing rule origins in pricing calculations.
 */
public enum PricingSourceType {

    /**
     * Pricing rules come from default rate configuration
     */
    DEFAULT_RATE,

    /**
     * Pricing rules come from rate plan with rate tables
     */
    RATE_PLAN
}