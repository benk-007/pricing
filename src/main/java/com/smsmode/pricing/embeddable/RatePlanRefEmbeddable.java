package com.smsmode.pricing.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class RatePlanRefEmbeddable {
    @NotBlank(message = "Rate plan UUID is required")
    @Column(name = "RATE_PLAN_ID", nullable = false)
    private String id;
}
