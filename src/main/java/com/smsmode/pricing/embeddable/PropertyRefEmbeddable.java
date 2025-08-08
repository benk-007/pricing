package com.smsmode.pricing.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class PropertyRefEmbeddable {
    @Column(name = "PROPERTY_ID")
    private String id;
}