/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.resource.defaultrate;

import com.smsmode.pricing.resource.common.BaseRateResource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Resource for creating and updating default rates
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@Getter
@Setter
public class DefaultRatePostResource extends BaseRateResource {

    @NotNull
    private BigDecimal nightly;
    private int minStay;
    private Integer maxStay;
    @NotBlank
    private String unitId;
}