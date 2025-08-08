package com.smsmode.pricing.resource.fee;

import com.smsmode.pricing.enumeration.FeeModalityEnum;
import com.smsmode.pricing.enumeration.FeeTypeEnum;
import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeePostResource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class FeePostResource {

    @NotBlank
    private String name;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal amount;

    @NotNull
    private FeeModalityEnum modality;

    private String description;

    private UnitRefEmbeddable unit;

    @NotNull
    private Boolean active;

    @NotNull
    private Boolean required;

    @Valid
    private List<@Valid AdditionalGuestFeePostResource> additionalGuestPrices;
}
