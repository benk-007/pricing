package com.smsmode.pricing.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embeddable class representing a reference to a segment.
 */
@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class SegmentRefEmbeddable {

    @Column(name = "SEGMENT_UUID")
    private String uuid;
    @Column(name = "SEGMENT_NAME")
    private String name;
}