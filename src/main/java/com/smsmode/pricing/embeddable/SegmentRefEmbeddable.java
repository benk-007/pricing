package com.smsmode.pricing.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embeddable class representing a reference to a segment.
 * Contains the UUID and name of the segment.
 */
@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class SegmentRefEmbeddable {

    /**
     * UUID of the segment - this is the primary identifier used by the API.
     * Required field for API validation.
     */
    @Column(name = "SEGMENT_UUID")
    private String uuid;

    /**
     * Name of the segment - provided by the frontend for display purposes.
     * Optional field as the frontend handles sending this value.
     */
    @Column(name = "SEGMENT_NAME")
    private String name;
}