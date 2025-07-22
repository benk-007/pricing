package com.smsmode.pricing.resource.common;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GuestsResource {
    @NotNull(message = "Number of adults is required")
    @Min(value = 1, message = "At least one adult is required")
    private Integer adults;

    @Valid
    private List<@Valid ChildResource> children = new ArrayList<>();
}