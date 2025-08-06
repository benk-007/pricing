/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.resource.calculate;

import com.smsmode.pricing.enumeration.FeeModalityEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 05 Aug 2025</p>
 */
@Data
public class UnitFeeRateGetResource {
    private String id;
    private String name;
    private FeeModalityEnum modality;
    BigDecimal amount;
    private boolean required;
    List<FeeItemGetResource> details;
}
