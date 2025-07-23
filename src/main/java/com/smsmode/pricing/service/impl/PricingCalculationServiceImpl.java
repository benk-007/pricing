package com.smsmode.pricing.service.impl;

import com.smsmode.pricing.dao.service.DefaultRateDaoService;
import com.smsmode.pricing.dao.service.RatePlanDaoService;
import com.smsmode.pricing.dao.service.RateTableDaoService;
import com.smsmode.pricing.enumeration.AmountTypeEnum;
import com.smsmode.pricing.enumeration.GuestTypeEnum;
import com.smsmode.pricing.model.*;
import com.smsmode.pricing.resource.common.ChildResource;
import com.smsmode.pricing.resource.common.GuestsResource;
import com.smsmode.pricing.resource.common.NightRateGetResource;
import com.smsmode.pricing.resource.common.UnitPricingGetResource;
import com.smsmode.pricing.resource.pricecalculation.PriceCalculationPostResource;
import com.smsmode.pricing.service.PricingCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Simplified PricingCalculationService implementation.
 *
 * Business Logic:
 * 1. For each day: Rate table → Default rate → Empty
 * 2. No partial fallback - either full rate table or full default rate
 * 3. First guest gets nightly rate, others pay additional fees
 * 4. Adults have priority over children as primary guests
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PricingCalculationServiceImpl implements PricingCalculationService {

    private final DefaultRateDaoService defaultRateDaoService;
    private final RatePlanDaoService ratePlanDaoService;
    private final RateTableDaoService rateTableDaoService;

    @Override
    public ResponseEntity<List<UnitPricingGetResource>> calculatePricing(PriceCalculationPostResource request) {
        log.debug("Starting price calculation for {} units", request.getUnits().size());

        LocalDate checkinDate = parseDateFromString(request.getCheckinDate());
        LocalDate checkoutDate = parseDateFromString(request.getCheckoutDate());

        List<UnitPricingGetResource> unitPricings = new ArrayList<>();

        for (String unitId : request.getUnits()) {
            try {
                UnitPricingGetResource unitPricing = calculatePricingForUnit(
                        unitId, checkinDate, checkoutDate, request.getGuests(), request.getSegmentId());
                unitPricings.add(unitPricing);
            } catch (Exception e) {
                log.warn("Failed to calculate pricing for unit {}: {}", unitId, e.getMessage());
                unitPricings.add(createEmptyUnitPricing(unitId));
            }
        }

        log.info("Successfully calculated pricing for {} units", unitPricings.size());
        return ResponseEntity.created(URI.create("")).body(unitPricings);
    }

    private LocalDate parseDateFromString(String dateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            return LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            log.error("Failed to parse date string '{}': {}", dateString, e.getMessage());
            throw new DateTimeParseException(
                    "Invalid date format. Expected DD-MM-YYYY, got: " + dateString,
                    dateString, e.getErrorIndex());
        }
    }

    private String formatDateToString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return date.format(formatter);
    }

    private UnitPricingGetResource calculatePricingForUnit(String unitId, LocalDate checkinDate,
                                                           LocalDate checkoutDate, GuestsResource guests,
                                                           String segmentId) {
        log.debug("Calculating pricing for unit: {}, dates: {} to {}", unitId, checkinDate, checkoutDate);

        List<NightRateGetResource> nightRates = null;
        RatePlanModel foundRatePlan = null;

        // Try rate plan if segment provided
        if (StringUtils.hasText(segmentId)) {
            nightRates = tryCalculateFromRatePlan(unitId, segmentId, checkinDate, checkoutDate, guests);
            if (nightRates != null) {
                Set<String> segmentUuids = Set.of(segmentId);
                List<RatePlanModel> ratePlans = ratePlanDaoService.findEnabledRatePlansWithOverlappingSegments(segmentUuids);
                foundRatePlan = ratePlans.stream()
                        .filter(rp -> unitId.equals(rp.getUnit().getId()))
                        .findFirst()
                        .orElse(null);
            }
        }

        // Fallback to default rate
        if (nightRates == null) {
            nightRates = tryCalculateFromDefaultRate(unitId, checkinDate, checkoutDate, guests);
        }

        if (nightRates == null) {
            log.warn("No pricing rules found for unit: {}", unitId);
            return createEmptyUnitPricing(unitId);
        }

        BigDecimal totalAmount = calculateTotalAmount(nightRates);
        BigDecimal averageRate = calculateAverageRate(nightRates);

        Integer minStay = null;
        Integer maxStay = null;

        if (foundRatePlan != null) {
            // Cas rate plan - vérifier quel source a été utilisé pour check-in date
            RateTableModel checkinRateTable = rateTableDaoService.findRateTableForDate(foundRatePlan.getId(), checkinDate);

            if (checkinRateTable != null) {
                // Rate table utilisé pour check-in date
                minStay = checkinRateTable.getMinStay();
                maxStay = checkinRateTable.getMaxStay();
                log.debug("Using rate table minStay/maxStay for check-in date: {}/{}", minStay, maxStay);
            } else {
                // Default rate utilisé pour check-in date
                DefaultRateModel defaultRate = defaultRateDaoService.findWithRelatedDataForPricing(unitId);
                if (defaultRate != null) {
                    minStay = defaultRate.getMinStay();
                    maxStay = defaultRate.getMaxStay();
                    log.debug("Using default rate minStay/maxStay for check-in date: {}/{}", minStay, maxStay);
                }
            }
        } else {
            // Cas default rate seulement
            DefaultRateModel defaultRate = defaultRateDaoService.findWithRelatedDataForPricing(unitId);
            if (defaultRate != null) {
                minStay = defaultRate.getMinStay();
                maxStay = defaultRate.getMaxStay();
                log.debug("Using default rate minStay/maxStay: {}/{}", minStay, maxStay);
            }
        }

        UnitPricingGetResource unitPricing = new UnitPricingGetResource();
        unitPricing.setId(unitId);
        unitPricing.setNightRates(nightRates);
        unitPricing.setNightlyRate(averageRate);
        unitPricing.setTotalAmount(totalAmount);
        unitPricing.setMinStay(minStay);
        unitPricing.setMaxStay(maxStay);

        log.info("Successfully calculated pricing for unit: {} - Total: {}, Average: {}",
                unitId, totalAmount, averageRate);
        return unitPricing;
    }

    private List<NightRateGetResource> tryCalculateFromRatePlan(String unitId, String segmentId,
                                                                LocalDate checkinDate, LocalDate checkoutDate,
                                                                GuestsResource guests) {
        try {
            Set<String> segmentUuids = Set.of(segmentId);
            List<RatePlanModel> ratePlans = ratePlanDaoService.findEnabledRatePlansWithOverlappingSegments(segmentUuids);

            Optional<RatePlanModel> applicableRatePlan = ratePlans.stream()
                    .filter(rp -> unitId.equals(rp.getUnit().getId()))
                    .findFirst();

            if (applicableRatePlan.isEmpty()) {
                log.debug("No applicable rate plan found for unit: {} with segment: {}", unitId, segmentId);
                return null;
            }

            RatePlanModel ratePlan = applicableRatePlan.get();
            log.debug("Found applicable rate plan: {} for unit: {}", ratePlan.getName(), unitId);

            return calculateDailyRatesFromRatePlan(checkinDate, checkoutDate, ratePlan, guests);

        } catch (Exception e) {
            log.error("Error calculating from rate plan for unit: {}, segment: {}", unitId, segmentId, e);
            return null;
        }
    }

    private List<NightRateGetResource> tryCalculateFromDefaultRate(String unitId, LocalDate checkinDate,
                                                                   LocalDate checkoutDate, GuestsResource guests) {
        try {
            DefaultRateModel defaultRate = defaultRateDaoService.findWithRelatedDataForPricing(unitId);
            if (defaultRate == null) {
                log.debug("No default rate found for unit: {}", unitId);
                return null;
            }

            log.debug("Found default rate for unit: {}", unitId);
            return calculateDailyRatesFromDefaultRate(checkinDate, checkoutDate, defaultRate, guests);

        } catch (Exception e) {
            log.error("Error calculating from default rate for unit: {}", unitId, e);
            return null;
        }
    }

    /**
     * Calculates daily rates from rate plan with simple fallback logic
     */
    private List<NightRateGetResource> calculateDailyRatesFromRatePlan(LocalDate checkinDate, LocalDate checkoutDate,
                                                                       RatePlanModel ratePlan, GuestsResource guests) {
        List<NightRateGetResource> nightRates = new ArrayList<>();
        LocalDate currentDate = checkinDate;

        // Get default rate for fallback when no rate table covers a date
        DefaultRateModel defaultRate = defaultRateDaoService.findWithRelatedDataForPricing(ratePlan.getUnit().getId());

        while (currentDate.isBefore(checkoutDate)) {
            RateTableModel coveringTable = rateTableDaoService.findRateTableForDate(ratePlan.getId(), currentDate);

            BigDecimal finalRate;

            // Simple fallback: Rate table OR default rate (no mixing)
            if (coveringTable != null) {
                // Use rate table entirely
                finalRate = calculateRateFromRateTable(currentDate, coveringTable, guests);
                log.debug("Using rate table for {}: {}", currentDate, finalRate);
            } else if (defaultRate != null) {
                // Fallback to default rate entirely
                finalRate = calculateRateFromDefaultRate(currentDate, defaultRate, guests);
                log.debug("Fallback to default rate for {}: {}", currentDate, finalRate);
            } else {
                log.warn("No pricing source available for date: {}", currentDate);
                return null;
            }

            NightRateGetResource nightRate = new NightRateGetResource();
            nightRate.setDate(formatDateToString(currentDate));
            nightRate.setRate(finalRate);
            nightRates.add(nightRate);

            currentDate = currentDate.plusDays(1);
        }

        return nightRates;
    }

    /**
     * Calculates daily rates using default rate only
     */
    private List<NightRateGetResource> calculateDailyRatesFromDefaultRate(LocalDate checkinDate, LocalDate checkoutDate,
                                                                          DefaultRateModel defaultRate, GuestsResource guests) {
        List<NightRateGetResource> nightRates = new ArrayList<>();
        LocalDate currentDate = checkinDate;

        while (currentDate.isBefore(checkoutDate)) {
            BigDecimal finalRate = calculateRateFromDefaultRate(currentDate, defaultRate, guests);

            NightRateGetResource nightRate = new NightRateGetResource();
            nightRate.setDate(formatDateToString(currentDate));
            nightRate.setRate(finalRate);
            nightRates.add(nightRate);

            currentDate = currentDate.plusDays(1);
        }

        return nightRates;
    }

    /**
     * Calculates rate from rate table (nightly + day-specific + additional fees)
     */
    private BigDecimal calculateRateFromRateTable(LocalDate date, RateTableModel rateTable, GuestsResource guests) {
        // Calculate base nightly rate (MAX between nightly and day-specific)
        BigDecimal baseNightlyRate = calculateBaseNightlyRate(date, rateTable.getNightly(), rateTable.getDaySpecificRates());

        // Apply additional guest fees
        BigDecimal additionalFees = calculateAdditionalGuestFees(guests, rateTable.getAdditionalGuestFees(), baseNightlyRate);

        BigDecimal finalRate = baseNightlyRate.add(additionalFees);
        log.debug("Rate table calculation for {}: {} (base) + {} (fees) = {}", date, baseNightlyRate, additionalFees, finalRate);

        return finalRate;
    }

    /**
     * Calculates rate from default rate (nightly + day-specific + additional fees)
     */
    private BigDecimal calculateRateFromDefaultRate(LocalDate date, DefaultRateModel defaultRate, GuestsResource guests) {
        // Calculate base nightly rate (MAX between nightly and day-specific)
        BigDecimal baseNightlyRate = calculateBaseNightlyRate(date, defaultRate.getNightly(), defaultRate.getDaySpecificRates());

        // Apply additional guest fees
        BigDecimal additionalFees = calculateAdditionalGuestFees(guests, defaultRate.getAdditionalGuestFees(), baseNightlyRate);

        BigDecimal finalRate = baseNightlyRate.add(additionalFees);
        log.debug("Default rate calculation for {}: {} (base) + {} (fees) = {}", date, baseNightlyRate, additionalFees, finalRate);

        return finalRate;
    }

    /**
     * Calculates base nightly rate (MAX between regular nightly and day-specific)
     */
    private BigDecimal calculateBaseNightlyRate(LocalDate date, BigDecimal regularNightly, List<DaySpecificRateModel> daySpecificRates) {
        BigDecimal baseRate = regularNightly != null ? regularNightly : BigDecimal.ZERO;

        // Check for day-specific rate
        if (!CollectionUtils.isEmpty(daySpecificRates)) {
            BigDecimal daySpecificRate = daySpecificRates.stream()
                    .filter(dsr -> dsr.getDays().contains(date.getDayOfWeek()))
                    .map(DaySpecificRateModel::getNightly)
                    .findFirst()
                    .orElse(null);

            if (daySpecificRate != null) {
                baseRate = baseRate.max(daySpecificRate);
                log.debug("Applied day-specific rate for {} ({}): MAX({}, {}) = {}",
                        date.getDayOfWeek(), date, regularNightly, daySpecificRate, baseRate);
            }
        }

        return baseRate;
    }

    /**
     * Calculates additional guest fees based on simplified guest logic
     */
    private BigDecimal calculateAdditionalGuestFees(GuestsResource guests, List<AdditionalGuestFeeModel> additionalFees, BigDecimal baseNightlyRate) {
        if (CollectionUtils.isEmpty(additionalFees)) {
            log.debug("No additional guest fees defined");
            return BigDecimal.ZERO;
        }

        int totalAdults = guests.getAdults() != null ? guests.getAdults() : 0;
        int totalChildren = getTotalChildren(guests.getChildren());

        log.debug("Guest analysis: {} adults, {} children", totalAdults, totalChildren);

        // Guest priority logic: Adults > Children
        // First guest (adult if available, otherwise oldest child) pays base nightly rate
        // All others are additional guests
        int additionalAdults;
        int additionalChildren;

        if (totalAdults > 0) {
            // Adults present: First adult gets base rate, others are additional
            additionalAdults = totalAdults - 1;
            additionalChildren = totalChildren; // All children are additional
            log.debug("Adults present: 1 adult gets base rate, {} additional adults, {} additional children",
                    additionalAdults, additionalChildren);
        } else if (totalChildren > 0) {
            // Only children: First child gets base rate, others are additional
            additionalAdults = 0;
            additionalChildren = totalChildren - 1;
            log.debug("Only children: 1 child gets base rate, {} additional children", additionalChildren);
        } else {
            // No guests (shouldn't happen)
            log.warn("No guests found in request");
            return BigDecimal.ZERO;
        }

        BigDecimal totalAdditionalFees = BigDecimal.ZERO;

        // Calculate adult additional fees
        if (additionalAdults > 0) {
            BigDecimal adultFees = calculateAdultAdditionalFees(additionalAdults, additionalFees);
            totalAdditionalFees = totalAdditionalFees.add(adultFees);
            log.debug("Adult additional fees: {}", adultFees);
        }

        // Calculate child additional fees (grouped by buckets)
        if (additionalChildren > 0) {
            BigDecimal childFees = calculateChildAdditionalFees(guests.getChildren(), additionalChildren, additionalFees, baseNightlyRate);
            totalAdditionalFees = totalAdditionalFees.add(childFees);
            log.debug("Child additional fees: {}", childFees);
        }

        log.debug("Total additional guest fees: {}", totalAdditionalFees);
        return totalAdditionalFees;
    }

    /**
     * Calculates adult additional fees
     */
    private BigDecimal calculateAdultAdditionalFees(int additionalAdults, List<AdditionalGuestFeeModel> additionalFees) {
        Optional<AdditionalGuestFeeModel> adultFee = additionalFees.stream()
                .filter(fee -> GuestTypeEnum.ADULT.equals(fee.getGuestType()))
                .findFirst();

        if (adultFee.isEmpty()) {
            log.debug("No adult fee configuration found");
            return BigDecimal.ZERO;
        }

        AdditionalGuestFeeModel fee = adultFee.get();
        int chargeableAdults = Math.max(0, additionalAdults - (fee.getGuestCount() - 1));

        BigDecimal totalFee = chargeableAdults > 0 ?
                fee.getValue().multiply(BigDecimal.valueOf(chargeableAdults)) :
                BigDecimal.ZERO;

        log.debug("Adult fee calculation: {} additional adults, guestCount={}, chargeable={}, fee={}×{}={}",
                additionalAdults, fee.getGuestCount(), chargeableAdults, fee.getValue(), chargeableAdults, totalFee);

        return totalFee;
    }

    /**
     * Calculates child additional fees (grouped by age buckets)
     */
    private BigDecimal calculateChildAdditionalFees(List<ChildResource> children, int additionalChildren,
                                                    List<AdditionalGuestFeeModel> additionalFees, BigDecimal baseNightlyRate) {
        if (CollectionUtils.isEmpty(children)) {
            return BigDecimal.ZERO;
        }

        // Get child fee buckets
        List<AdditionalGuestFeeModel> childFeeBuckets = additionalFees.stream()
                .filter(fee -> GuestTypeEnum.CHILD.equals(fee.getGuestType()))
                .filter(fee -> fee.getAgeBucket() != null)
                .collect(Collectors.toList());

        if (childFeeBuckets.isEmpty()) {
            log.debug("No child fee buckets found");
            return BigDecimal.ZERO;
        }

        // Group children by age for processing (but maintain total additional children logic)
        Map<Integer, Integer> childrenByAge = children.stream()
                .collect(Collectors.groupingBy(
                        ChildResource::getAge,
                        Collectors.summingInt(ChildResource::getQuantity)
                ));

        BigDecimal totalChildFees = BigDecimal.ZERO;
        int processedChildren = 0;

        // Process each bucket and distribute additional children
        for (AdditionalGuestFeeModel bucket : childFeeBuckets) {
            // Find children in this bucket
            int childrenInBucket = childrenByAge.entrySet().stream()
                    .filter(entry -> {
                        int age = entry.getKey();
                        return age >= bucket.getAgeBucket().getFromAge() && age <= bucket.getAgeBucket().getToAge();
                    })
                    .mapToInt(Map.Entry::getValue)
                    .sum();

            if (childrenInBucket > 0) {
                // Calculate additional children for this bucket (proportional distribution)
                int additionalChildrenForBucket = Math.min(childrenInBucket, additionalChildren - processedChildren);

                if (additionalChildrenForBucket > 0) {
                    int chargeableChildren = Math.max(0, additionalChildrenForBucket - (bucket.getGuestCount() - 1));

                    if (chargeableChildren > 0) {
                        BigDecimal bucketFee = calculateFeeForBucket(bucket, chargeableChildren, baseNightlyRate);
                        totalChildFees = totalChildFees.add(bucketFee);

                        log.debug("Child bucket {}-{}: {} children in bucket, {} additional, {} chargeable, fee={}",
                                bucket.getAgeBucket().getFromAge(), bucket.getAgeBucket().getToAge(),
                                childrenInBucket, additionalChildrenForBucket, chargeableChildren, bucketFee);
                    }

                    processedChildren += additionalChildrenForBucket;
                }
            }
        }

        return totalChildFees;
    }

    /**
     * Calculates fee for a specific bucket
     */
    private BigDecimal calculateFeeForBucket(AdditionalGuestFeeModel bucket, int quantity, BigDecimal baseNightlyRate) {
        if (quantity <= 0) {
            return BigDecimal.ZERO;
        }

        if (AmountTypeEnum.PERCENT.equals(bucket.getAmountType())) {
            return baseNightlyRate.multiply(bucket.getValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(quantity));
        } else {
            return bucket.getValue().multiply(BigDecimal.valueOf(quantity));
        }
    }

    /**
     * Gets total number of children
     */
    private int getTotalChildren(List<ChildResource> children) {
        if (CollectionUtils.isEmpty(children)) {
            return 0;
        }
        return children.stream().mapToInt(ChildResource::getQuantity).sum();
    }

    private BigDecimal calculateTotalAmount(List<NightRateGetResource> nightRates) {
        return nightRates.stream()
                .map(NightRateGetResource::getRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateAverageRate(List<NightRateGetResource> nightRates) {
        if (nightRates.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = calculateTotalAmount(nightRates);
        return total.divide(BigDecimal.valueOf(nightRates.size()), 2, RoundingMode.HALF_UP);
    }

    private UnitPricingGetResource createEmptyUnitPricing(String unitId) {
        UnitPricingGetResource emptyPricing = new UnitPricingGetResource();
        emptyPricing.setId(unitId);
        emptyPricing.setNightRates(new ArrayList<>());
        emptyPricing.setNightlyRate(BigDecimal.ZERO);
        emptyPricing.setTotalAmount(BigDecimal.ZERO);
        emptyPricing.setMinStay(null);
        emptyPricing.setMaxStay(null);
        return emptyPricing;
    }
}