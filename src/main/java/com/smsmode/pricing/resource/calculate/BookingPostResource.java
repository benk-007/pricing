/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.resource.calculate;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 31 Jul 2025</p>
 */
@Data
public class BookingPostResource {
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private GuestsPostResource guests;
    private String segmentId;
    private String subSegmentId;
    Set<UnitOccupancyPostResource> units;
    BigDecimal globalOccupancy;
}
