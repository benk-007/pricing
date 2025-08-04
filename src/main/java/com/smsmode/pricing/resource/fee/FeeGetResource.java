package com.smsmode.pricing.resource.fee;

import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import com.smsmode.pricing.enumeration.FeeModalityEnum;
import com.smsmode.pricing.enumeration.FeeTypeEnum;
import com.smsmode.pricing.resource.common.AuditGetResource;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class FeeGetResource {
    private String id;
    private String name;
    private BigDecimal amount;
    private FeeTypeEnum type;
    private FeeModalityEnum modality;
    private String description;
    private boolean active;
    private UnitRefEmbeddable unit;
    private AuditGetResource audit;
}
