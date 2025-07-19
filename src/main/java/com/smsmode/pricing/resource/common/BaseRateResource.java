package com.smsmode.pricing.resource.common;

import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeePostResource;
import com.smsmode.pricing.resource.common.dayspecificrate.DaySpecificRatePostResource;
import com.smsmode.pricing.validator.ValidGuestFees;
import com.smsmode.pricing.validator.ValidDaySpecificRates;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
@ValidGuestFees
@ValidDaySpecificRates
public abstract class BaseRateResource {

    @Valid
    private List<@Valid DaySpecificRatePostResource> daySpecificRates;

    @Valid
    private List<@Valid AdditionalGuestFeePostResource> additionalGuestFees;
}
