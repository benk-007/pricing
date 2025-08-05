package com.smsmode.pricing.resource.fee;

import com.smsmode.pricing.embeddable.UnitRefEmbeddable;
import com.smsmode.pricing.enumeration.FeeModalityEnum;
import com.smsmode.pricing.enumeration.FeeTypeEnum;
import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeePostResource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
public class FeePatchResource {

    @NotBlank
    private String name;

    @DecimalMin("0.0")
    private BigDecimal amount;

    private FeeTypeEnum type;

    private FeeModalityEnum modality;

    private String description;

    private Boolean active;

    @Valid
    private List<@Valid AdditionalGuestFeePostResource> additionalGuestPrices;
}
