package com.smsmode.pricing.resource.fee;

import com.smsmode.pricing.enumeration.FeeModalityEnum;
import com.smsmode.pricing.enumeration.FeeTypeEnum;
import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class FeePostResource {

    @NotBlank
    private String name;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal amount;

    @NotNull
    private FeeTypeEnum type;

    @NotNull
    private FeeModalityEnum modality;

    private String description;

    @NotNull
    private UnitRefEmbeddable unit;

    @NotNull
    private Boolean active;

}
