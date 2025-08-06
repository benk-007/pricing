/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.resource.calculate;

import com.smsmode.pricing.resource.fee.FeeItemGetResource;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 31 Jul 2025</p>
 */
@Data
public class UnitBookingRateGetResource {
    Map<LocalDate, BigDecimal> pricingPerDay;
    BigDecimal averagePrice;
    List<FeeItemGetResource> fees;
}
