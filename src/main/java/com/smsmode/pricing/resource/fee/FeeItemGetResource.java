/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.resource.fee;

import com.smsmode.pricing.enumeration.FeeModalityEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 06 Aug 2025</p>
 */
@Data
public class FeeItemGetResource {
    private String id;
    private String name;
    private BigDecimal amount;
    private FeeModalityEnum modality;
    private boolean required;
}
