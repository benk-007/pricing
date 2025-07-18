package com.smsmode.pricing.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embeddable class representing a reference to a sub-segment.
 * Contains the UUID and name of the sub-segment.
 */
@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class SubSegmentRefEmbeddable {

    /**
     * UUID of the sub-segment - this is the primary identifier used by the API.
     * Required field for API validation.
     */
    @Column(name = "SUBSEGMENT_UUID")
    private String uuid;

    /**
     * Name of the sub-segment - provided by the frontend for display purposes.
     * Optional field as the frontend handles sending this value.
     */
    @Column(name = "SUBSEGMENT_NAME")
    private String name;
}