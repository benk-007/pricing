package com.smsmode.pricing.resource.calculate;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GuestsPostResource {

    private Integer adults;

    @Valid
    private List<@Valid ChildPostResource> children = new ArrayList<>();
}