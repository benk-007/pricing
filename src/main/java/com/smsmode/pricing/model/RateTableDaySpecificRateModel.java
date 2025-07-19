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
 * Entity representing day-specific rate for rate table.
 */
@Getter
@Setter
@Table(name = "RATE_TABLE_DAY_SPECIFIC_RATE")
@Entity
public class RateTableDaySpecificRateModel extends AbstractBaseModel {

    private BigDecimal nightly;

    @Convert(converter = DayOfWeekSetConverter.class)
    private Set<DayOfWeek> days;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RATE_TABLE_ID")
    private RateTableModel rateTable;
}