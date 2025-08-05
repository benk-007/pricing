/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.service.impl;

import com.smsmode.pricing.dao.service.DefaultRateDaoService;
import com.smsmode.pricing.dao.service.RatePlanDaoService;
import com.smsmode.pricing.dao.service.RateTableDaoService;
import com.smsmode.pricing.dao.specification.DefaultRateSpecification;
import com.smsmode.pricing.dao.specification.RatePlanSpecification;
import com.smsmode.pricing.dao.specification.RateTableSpecification;
import com.smsmode.pricing.embeddable.AgeBucketEmbeddable;
import com.smsmode.pricing.enumeration.AmountTypeEnum;
import com.smsmode.pricing.enumeration.GuestTypeEnum;
import com.smsmode.pricing.enumeration.OccupancyModeEnum;
import com.smsmode.pricing.enumeration.RateTableTypeEnum;
import com.smsmode.pricing.model.*;
import com.smsmode.pricing.resource.calculate.*;
import com.smsmode.pricing.service.RateEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 01 Aug 2025</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateEngineServiceImpl implements RateEngineService {

    private final RatePlanDaoService ratePlanDaoService;
    private final RateTableDaoService rateTableDaoService;
    private final DefaultRateDaoService defaultRateDaoService;

    @Override
    public ResponseEntity<Map<String, UnitBookingRateGetResource>> calculateBookingRate(BookingPostResource bookingPostResource) {
        Map<String, UnitBookingRateGetResource> unitPricing = new HashMap<>();
        List<LocalDate> bookingDates = getDatesBetween(bookingPostResource.getCheckinDate(), bookingPostResource.getCheckoutDate());
        for (UnitOccupancyPostResource unit : bookingPostResource.getUnits()) {
            unitPricing.put(unit.getId(), this.calculateBookingRateForUnit(bookingDates, bookingPostResource, unit));
        }
        return ResponseEntity.ok(unitPricing);
    }

    UnitBookingRateGetResource calculateBookingRateForUnit(List<LocalDate> bookingDates, BookingPostResource bookingPostResource, UnitOccupancyPostResource unit) {
        DefaultRateModel defaultRateModel = null;
        if (defaultRateDaoService.existsBy(DefaultRateSpecification.withUnitId(unit.getId()))) {
            log.debug("Retrieving default rate for unit with Id: {} from database ...", unit.getId());
            defaultRateModel = defaultRateDaoService.findOneBy(DefaultRateSpecification.withUnitId(unit.getId()));
        }
        String segmentId = null;
        if (!ObjectUtils.isEmpty(bookingPostResource.getSubSegmentId())) {
            segmentId = bookingPostResource.getSubSegmentId();
        } else if (!ObjectUtils.isEmpty(bookingPostResource.getSegmentId())) {
            segmentId = bookingPostResource.getSegmentId();
        }

        RatePlanModel ratePlanModel = null;
        if (ratePlanDaoService.existsBy(Specification.where(RatePlanSpecification.withEnabled(true)
                        .and(RatePlanSpecification.withUnitId(unit.getId())))
                .and(RatePlanSpecification.withSegmentId(segmentId)))) {
            ratePlanModel = ratePlanDaoService.findOneBy(Specification.where(RatePlanSpecification.withEnabled(true)
                            .and(RatePlanSpecification.withUnitId(unit.getId())))
                    .and(RatePlanSpecification.withSegmentId(segmentId)));
        }

        return this.calculateBookingRateForUnitByPlan(bookingDates, bookingPostResource.getGuests(), ratePlanModel,
                defaultRateModel, bookingPostResource.getGlobalOccupancy(), unit.getOccupancy());
    }

    private UnitBookingRateGetResource calculateBookingRateForUnitByPlan(List<LocalDate> bookingDates, GuestsPostResource guests, RatePlanModel ratePlan, DefaultRateModel defaultRateModel, BigDecimal globalOccupancy, BigDecimal unitOccupancy) {


        Map<LocalDate, BigDecimal> pricingPerDay = new HashMap<>();

        for (LocalDate date : bookingDates) {
            BigDecimal amount = calculatePricingPerPlanAndDate(date, guests, ratePlan, defaultRateModel, globalOccupancy, unitOccupancy);
            pricingPerDay.put(date, amount);
        }


        UnitBookingRateGetResource unitBookingRateGetResource = new UnitBookingRateGetResource();
        //Set values for this object
        unitBookingRateGetResource.setPricingPerDay(pricingPerDay);
        unitBookingRateGetResource.setAveragePrice(RateEngineServiceImpl.calculateAverage(pricingPerDay));
        unitBookingRateGetResource.setTotalPrice(RateEngineServiceImpl.calculateTotal(pricingPerDay));
        return unitBookingRateGetResource;
    }

    private BigDecimal calculatePricingPerPlanAndDate(LocalDate date, GuestsPostResource guests, RatePlanModel ratePlan, DefaultRateModel defaultRateModel, BigDecimal globalOccupancy, BigDecimal unitOccupancy) {
        BigDecimal amount = BigDecimal.valueOf(0);
        if (!ObjectUtils.isEmpty(ratePlan)) {
            if (rateTableDaoService.existsBy(RateTableSpecification.withRatePlanId(ratePlan.getId()).and(RatePlanSpecification.withType(RateTableTypeEnum.DYNAMIC)).and(RateTableSpecification.withDateWithinInclusive(date)))) {
                log.info("Found dynamic rate table handling date: {} in plan with id: {}", date, ratePlan.getId());
                log.debug("Will retrieve the dynamic table from database ...");
                RateTableModel rateTable = rateTableDaoService.findOneBy(RateTableSpecification.withRatePlanId(ratePlan.getId()).and(RatePlanSpecification.withType(RateTableTypeEnum.DYNAMIC)).and(RateTableSpecification.withDateWithinInclusive(date)));
                //calculate pricing for dynamic table
                amount = this.calculateRateForDynamicTable(date, guests, rateTable, globalOccupancy, unitOccupancy);

            } else if (rateTableDaoService.existsBy(RateTableSpecification.withRatePlanId(ratePlan.getId()).and(RatePlanSpecification.withType(RateTableTypeEnum.STANDARD)).and(RateTableSpecification.withDateWithinInclusive(date)))) {
                log.info("Found standard rate table handling date: {} in plan with id: {}", date, ratePlan.getId());
                log.debug("Will retrieve the standard table from database ...");
                RateTableModel rateTable = rateTableDaoService.findOneBy(RateTableSpecification.withRatePlanId(ratePlan.getId()).and(RatePlanSpecification.withType(RateTableTypeEnum.STANDARD)).and(RateTableSpecification.withDateWithinInclusive(date)));
                amount = this.calculateRateForDefaultAndStandardTable(date, guests, rateTable.getNightly(), rateTable.getDaySpecificRates(), rateTable.getAdditionalGuestFees());
            } else {
                if (!ObjectUtils.isEmpty(defaultRateModel)) {
                    amount = this.calculateRateForDefaultAndStandardTable(date, guests, defaultRateModel.getNightly(), defaultRateModel.getDaySpecificRates(), defaultRateModel.getAdditionalGuestFees());
                }
            }
        } else {
            if (!ObjectUtils.isEmpty(defaultRateModel)) {
                amount = this.calculateRateForDefaultAndStandardTable(date, guests, defaultRateModel.getNightly(), defaultRateModel.getDaySpecificRates(), defaultRateModel.getAdditionalGuestFees());
            }
        }
        return amount;
    }

    private BigDecimal calculateRateForDynamicTable(LocalDate date, GuestsPostResource guests, RateTableModel rateTable, BigDecimal globalOccupancy, BigDecimal unitOccupancy) {
        BigDecimal amount = BigDecimal.valueOf(0);
        BigDecimal nightly = BigDecimal.valueOf(0);
        BigDecimal occupancy = rateTable.getOccupancyMode().equals(OccupancyModeEnum.GLOBAL) ? globalOccupancy : unitOccupancy;

        if (!CollectionUtils.isEmpty(rateTable.getDaySpecificRates())) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            for (DaySpecificRateModel daySpecificRateModel : rateTable.getDaySpecificRates()) {
                if (!CollectionUtils.isEmpty(daySpecificRateModel.getDays()) && daySpecificRateModel.getDays().contains(dayOfWeek)) {
                    nightly = daySpecificRateModel.getNightly();
                    break;
                }
            }
        } else {
            if (occupancy.compareTo(BigDecimal.valueOf(rateTable.getLowestOccupancy())) < 0) {
                nightly = rateTable.getLowRate();
            } else if (occupancy.compareTo(BigDecimal.valueOf(rateTable.getMaxOccupancy())) > 0) {
                nightly = rateTable.getMaxRate();
            } else {
                BigDecimal rateDifference = rateTable.getMaxRate().subtract(rateTable.getLowRate());
                BigDecimal plusRateValue = rateDifference
                        .multiply(occupancy)
                        .divide(BigDecimal.valueOf(rateTable.getMaxOccupancy()), 2, RoundingMode.HALF_UP);
                nightly = rateTable.getLowRate().add(plusRateValue);
            }
        }
        amount = amount.add(nightly);
        if (!CollectionUtils.isEmpty(rateTable.getAdditionalGuestFees())) {
            log.debug("Default rate contains rules related to additional guest fees, will calculate the amount in case any guests matching ...");
            Optional<AdditionalGuestFeeModel> adultFeeOptional = rateTable.getAdditionalGuestFees().stream()
                    .filter(fee -> GuestTypeEnum.ADULT.equals(fee.getGuestType()))
                    .findFirst();
            if (adultFeeOptional.isPresent()) {
                log.debug("Adult Guest additional fee is specified, will calculate and update the new amount ...");
                int adultGuests = Math.max(0, guests.getAdults() - adultFeeOptional.get().getGuestCount());
                if (adultGuests >= 1) {
                    BigDecimal adultFeeAmount = getGuestFeeAmount(adultFeeOptional.get(), adultGuests, nightly);
                    amount = amount.add(adultFeeAmount);
                }
            }
            if (!CollectionUtils.isEmpty(guests.getChildren())) {
                log.debug("Children are specified within booking, will check if there's any child guest additional fee specified ...");
                boolean hasChildFee = rateTable.getAdditionalGuestFees().stream()
                        .anyMatch(fee -> fee.getGuestType() == GuestTypeEnum.CHILD);
                if (hasChildFee) {
                    log.debug("Child Guest additional fee is specified, will calculate and update the new amount if it matches ...");
                    List<AdditionalGuestFeeModel> childFees = rateTable.getAdditionalGuestFees().stream()
                            .filter(fee -> fee.getGuestType() == GuestTypeEnum.CHILD).toList();
                    log.debug("Will loop over children guests to calculate and update nightly rate ...");
                    for (ChildPostResource childPostResource : guests.getChildren()) {
                        log.debug("Processing child entry: {} ...", childPostResource);
                        int age = childPostResource.getAge();
                        Optional<AdditionalGuestFeeModel> matchingFeeOpt = childFees.stream()
                                .filter(fee -> {
                                    AgeBucketEmbeddable ageBucket = fee.getAgeBucket();
                                    return age >= ageBucket.getFromAge() && age <= ageBucket.getToAge();
                                })
                                .findFirst();
                        if (matchingFeeOpt.isPresent()) {
                            int childGuests = Math.max(0, (childPostResource.getQuantity() + 1) - matchingFeeOpt.get().getGuestCount());
                            if (childGuests >= 1) {
                                BigDecimal childFeeAmount = getGuestFeeAmount(matchingFeeOpt.get(), childGuests, nightly);
                                amount = amount.add(childFeeAmount);
                            }
                        } else {
                            log.debug("No fee rule found for child age {}, skipping.", age);
                        }

                    }
                }
            }
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateRateForDefaultAndStandardTable(LocalDate date, GuestsPostResource guests, BigDecimal nightly,
                                                               List<DaySpecificRateModel> daySpecificRateModels, List<AdditionalGuestFeeModel> additionalGuestFeeModels) {
        BigDecimal amount = BigDecimal.valueOf(0);
        if (!CollectionUtils.isEmpty(daySpecificRateModels)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            for (DaySpecificRateModel daySpecificRateModel : daySpecificRateModels) {
                if (!CollectionUtils.isEmpty(daySpecificRateModel.getDays()) && daySpecificRateModel.getDays().contains(dayOfWeek)) {
                    nightly = daySpecificRateModel.getNightly();
                    break;
                }
            }
        }
        amount = amount.add(nightly);
        if (!CollectionUtils.isEmpty(additionalGuestFeeModels)) {
            log.debug("Default rate contains rules related to additional guest fees, will calculate the amount in case any guests matching ...");
            Optional<AdditionalGuestFeeModel> adultFeeOptional = additionalGuestFeeModels.stream()
                    .filter(fee -> GuestTypeEnum.ADULT.equals(fee.getGuestType()))
                    .findFirst();
            if (adultFeeOptional.isPresent()) {
                log.debug("Adult Guest additional fee is specified, will calculate and update the new amount ...");
                int adultGuests = Math.max(0, guests.getAdults() - adultFeeOptional.get().getGuestCount());
                if (adultGuests >= 1) {
                    BigDecimal adultFeeAmount = getGuestFeeAmount(adultFeeOptional.get(), adultGuests, nightly);
                    amount = amount.add(adultFeeAmount);
                }
            }
            if (!CollectionUtils.isEmpty(guests.getChildren())) {
                log.debug("Children are specified within booking, will check if there's any child guest additional fee specified ...");
                boolean hasChildFee = additionalGuestFeeModels.stream()
                        .anyMatch(fee -> fee.getGuestType() == GuestTypeEnum.CHILD);
                if (hasChildFee) {
                    log.debug("Child Guest additional fee is specified, will calculate and update the new amount if it matches ...");
                    List<AdditionalGuestFeeModel> childFees = additionalGuestFeeModels.stream()
                            .filter(fee -> fee.getGuestType() == GuestTypeEnum.CHILD).toList();
                    log.debug("Will loop over children guests to calculate and update nightly rate ...");
                    for (ChildPostResource childPostResource : guests.getChildren()) {
                        log.debug("Processing child entry: {} ...", childPostResource);
                        int age = childPostResource.getAge();
                        Optional<AdditionalGuestFeeModel> matchingFeeOpt = childFees.stream()
                                .filter(fee -> {
                                    AgeBucketEmbeddable ageBucket = fee.getAgeBucket();
                                    return age >= ageBucket.getFromAge() && age <= ageBucket.getToAge();
                                })
                                .findFirst();
                        if (matchingFeeOpt.isPresent()) {
                            int childGuests = Math.max(0, (childPostResource.getQuantity() + 1) - matchingFeeOpt.get().getGuestCount());
                            if (childGuests >= 1) {
                                BigDecimal childFeeAmount = getGuestFeeAmount(matchingFeeOpt.get(), childGuests, nightly);
                                amount = amount.add(childFeeAmount);
                            }
                        } else {
                            log.debug("No fee rule found for child age {}, skipping.", age);
                        }

                    }
                }
            }
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal getGuestFeeAmount(AdditionalGuestFeeModel guestFee, Integer guestCount, BigDecimal nightly) {
        BigDecimal guestFeeAmount;
        if (guestFee.getAmountType().equals(AmountTypeEnum.FLAT)) {
            guestFeeAmount = guestFee.getValue().multiply(BigDecimal.valueOf(guestCount));
        } else {
            guestFeeAmount = nightly.multiply(guestFee.getValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(guestCount));
        }
        return guestFeeAmount;
    }


    public static List<LocalDate> getDatesBetween(LocalDate checkinDate, LocalDate checkoutDate) {
        if (checkinDate == null || checkoutDate == null || !checkinDate.isBefore(checkoutDate)) {
            return List.of(); // return empty list for invalid input
        }
        return checkinDate.datesUntil(checkoutDate).toList();
    }

    public static BigDecimal calculateTotal(Map<LocalDate, BigDecimal> pricingPerDay) {
        return pricingPerDay.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateAverage(Map<LocalDate, BigDecimal> pricingPerDay) {
        if (pricingPerDay.isEmpty()) return BigDecimal.ZERO;
        BigDecimal total = calculateTotal(pricingPerDay);
        return total
                .divide(new BigDecimal(pricingPerDay.size()), 2, RoundingMode.HALF_UP);
    }

}
