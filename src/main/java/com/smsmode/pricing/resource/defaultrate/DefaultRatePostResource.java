/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.resource.defaultrate;

import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import com.smsmode.pricing.resource.common.BaseRateResource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Resource for creating and updating default rates
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@Data
public class DefaultRatePostResource extends BaseRateResource {

    @NotNull(message = "Nightly rate is required")
    private BigDecimal nightly;

    @Min(value = 1, message = "Minimum stay must be at least 1")
    private Integer minStay;

    private Integer maxStay;

    @Valid
    @NotNull(message = "Unit information is required")
    private UnitRefEmbeddable unit;
}