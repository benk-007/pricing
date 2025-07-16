package com.smsmode.pricing.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embeddable class representing a reference to a unit.
 * Contains the UUID of the unit this rate belongs to.
 */
@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class UnitRefEmbeddable {

    @NotBlank(message = "Unit UUID is required")
    @Column(name = "UNIT_ID", nullable = false)
    private String uuid;
}