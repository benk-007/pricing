package com.smsmode.pricing.resource.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChildResource {
    @NotNull(message = "Child age is required")
    @Min(value = 0, message = "Age cannot be negative")
    @Max(value = 17, message = "Age cannot exceed 17")
    private Integer age;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
