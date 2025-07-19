package com.smsmode.pricing.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embeddable class representing a reference to a sub-segment.
 */
@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class SubSegmentRefEmbeddable {

    @Column(name = "SUBSEGMENT_UUID")
    private String uuid;
    @Column(name = "SUBSEGMENT_NAME")
    private String name;
}