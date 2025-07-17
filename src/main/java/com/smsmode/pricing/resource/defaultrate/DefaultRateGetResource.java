/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.resource.defaultrate;

import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeeGetResource;
import com.smsmode.pricing.resource.common.dayspecificrate.DaySpecificRateGetResource;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Resource for returning default rate information
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@Data
public class DefaultRateGetResource {
    private String id;
    private BigDecimal nightly;
    private int minStay;
    private Integer maxStay;
    private UnitRefEmbeddable unit;
    private List<DaySpecificRateGetResource> daySpecificRates;
    private List<AdditionalGuestFeeGetResource> additionalGuestFees;
}