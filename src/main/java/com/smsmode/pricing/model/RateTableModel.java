package com.smsmode.pricing.model;

import com.smsmode.pricing.enumeration.OccupancyModeEnum;
import com.smsmode.pricing.enumeration.RateTableTypeEnum;
import com.smsmode.pricing.model.base.AbstractBaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a rate table in the pricing system.
 */
@Getter
@Setter
@Entity
@Table(name = "RATE_TABLE")
public class RateTableModel extends AbstractBaseModel {

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "START_DATE", nullable = false)
    private LocalDate startDate;

    @Column(name = "END_DATE", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false)
    private RateTableTypeEnum type = RateTableTypeEnum.STANDARD;

    // Standard fields (only for STANDARD type)
    @Column(name = "NIGHTLY")
    private BigDecimal nightly;

    // STANDARD & Dynamic fields
    @Column(name = "MIN_STAY")
    private Integer minStay;

    @Column(name = "MAX_STAY")
    private Integer maxStay;

    // Dynamic fields (only for DYNAMIC type)
    @Column(name = "LOW_RATE")
    private BigDecimal lowRate;

    @Column(name = "MAX_RATE")
    private BigDecimal maxRate;

    @Column(name = "LOWEST_OCCUPANCY")
    private Integer lowestOccupancy;

    @Column(name = "MAX_OCCUPANCY")
    private Integer maxOccupancy;

    private OccupancyModeEnum occupancyMode = OccupancyModeEnum.UNIT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RATE_PLAN_ID", nullable = false)
    private RatePlanModel ratePlan;


    @OneToMany(mappedBy = "rateTable", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<AdditionalGuestFeeModel> additionalGuestFees = new ArrayList<>();

    @OneToMany(mappedBy = "rateTable", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<DaySpecificRateModel> daySpecificRates = new ArrayList<>();
}