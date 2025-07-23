package com.smsmode.pricing.resource.common;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GuestsResource {

    private Integer adults;

    @Valid
    private List<@Valid ChildResource> children = new ArrayList<>();
}