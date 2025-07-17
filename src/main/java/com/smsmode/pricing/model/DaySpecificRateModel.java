/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.model;

import com.smsmode.pricing.converter.DayOfWeekSetConverter;
import com.smsmode.pricing.model.base.AbstractBaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.Set;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@Getter
@Setter
@Table(name = "DAY_SPECIFIC_RATE")
@Entity
public class DaySpecificRateModel extends AbstractBaseModel {
    private BigDecimal nightly;
    @Convert(converter = DayOfWeekSetConverter.class)
    private Set<DayOfWeek> days;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEFAULT_RATE_ID")
    private DefaultRateModel rate;
}
