package com.smsmode.pricing.resource.pricecalculation;

import com.smsmode.pricing.resource.common.GuestsResource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PriceCalculationPostResource {
    @NotNull(message = "Check-in date is required")
    private String checkinDate; // Format "DD-MM-YYYY"

    @NotNull(message = "Check-out date is required")
    private String checkoutDate; // Format "DD-MM-YYYY"

    @Valid
    @NotNull(message = "Guests information is required")
    private GuestsResource guests;

    private String segmentId; // Optional

    private String subSegmentId; //Optional

    @NotEmpty(message = "At least one unit is required")
    private List<String> units;
}
