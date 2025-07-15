/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.resource.defaultrate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@Data
public class DefaultRatePostResource {
    @NotNull
    private BigDecimal nightly;
    @NotNull
    @Positive
    private int minStay = 1;
    private Integer maxStay;
}
