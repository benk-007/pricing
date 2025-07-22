package com.smsmode.pricing.service.impl;

import com.smsmode.pricing.dao.service.DefaultRateDaoService;
import com.smsmode.pricing.dao.service.RatePlanDaoService;
import com.smsmode.pricing.dao.service.RateTableDaoService;
import com.smsmode.pricing.embeddable.AgeBucketEmbeddable;
import com.smsmode.pricing.enumeration.AmountTypeEnum;
import com.smsmode.pricing.enumeration.GuestTypeEnum;
import com.smsmode.pricing.model.*;
import com.smsmode.pricing.resource.common.ChildResource;
import com.smsmode.pricing.resource.common.GuestsResource;
import com.smsmode.pricing.resource.common.NightRateGetResource;
import com.smsmode.pricing.resource.common.UnitPricingGetResource;
import com.smsmode.pricing.resource.pricecalculation.*;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of PricingCalculationService for calculating unit pricing based on dates, guests, and segments.
 *
 * Simplified Business Logic Flow:
 * 1. Parse dates from request
 * 2. For each unit, find pricing rules (rate plan with segment → default rate → empty)
 * 3. Calculate daily base rates (nightly rates + day-specific rates + rate table overrides)
 * 4. Apply additional guest fees (adults and children based on age buckets)
 * 5. Calculate summary statistics (total amount and average rate)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PricingCalculationServiceImpl implements PricingCalculationService {

    private final DefaultRateDaoService defaultRateDaoService;
    private final RatePlanDaoService ratePlanDaoService;
    private final RateTableDaoService rateTableDaoService;

    /**
     * Main method that orchestrates the entire pricing calculation process for multiple units
     * <p>
     * Process Flow:
     * 1. Parse date strings to LocalDate objects for date calculations
     * 2. Calculate pricing for each unit individually (allows partial success)
     * 3. Build and return the complete response with all unit pricing
     *
     * @param request The pricing calculation request containing dates, guests, segment, and units
     * @return ResponseEntity with complete pricing breakdown for all units
     */
    @Override
    public ResponseEntity<PriceCalculationGetResource> calculatePricing(PriceCalculationPostResource request) {
        log.debug("Starting price calculation for {} units", request.getUnits().size());

        // Step 1: Parse string dates to LocalDate objects for date calculations
        // Convert "DD-MM-YYYY" format to LocalDate for easier manipulation
        LocalDate checkinDate = parseDateFromString(request.getCheckinDate());
        LocalDate checkoutDate = parseDateFromString(request.getCheckoutDate());

        // Step 2: Calculate pricing for each requested unit individually
        // Each unit may have different pricing rules (default rates, rate plans)
        List<UnitPricingGetResource> unitPricings = new ArrayList<>();

        for (String unitId : request.getUnits()) {
            try {
                // Calculate daily rates, apply guest fees, and compute totals for this unit
                UnitPricingGetResource unitPricing = calculatePricingForUnit(
                        unitId, checkinDate, checkoutDate, request.getGuests(), request.getSegmentId());
                unitPricings.add(unitPricing);
            } catch (Exception e) {
                log.warn("Failed to calculate pricing for unit {}: {}", unitId, e.getMessage());
                // Add empty result for failed units instead of failing entire request
                unitPricings.add(createEmptyUnitPricing(unitId));
            }
        }

        // Step 3: Build the final response containing all unit pricing information
        // Wrap individual unit results in the standard response format
        PriceCalculationGetResource response = new PriceCalculationGetResource();
        response.setUnits(unitPricings);

        log.info("Successfully calculated pricing for {} units", unitPricings.size());

        return ResponseEntity.ok(response);
    }

    /**
     * Parses a date string in DD-MM-YYYY format to LocalDate object
     *
     * @param dateString The date string to parse (format: "DD-MM-YYYY")
     * @return LocalDate object representing the parsed date
     * @throws DateTimeParseException if the date format is invalid
     */
    private LocalDate parseDateFromString(String dateString) {
        log.debug("Parsing date string: {}", dateString);

        try {
            // Define the expected date format pattern and parse
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate parsedDate = LocalDate.parse(dateString, formatter);

            log.debug("Successfully parsed date: {} -> {}", dateString, parsedDate);
            return parsedDate;

        } catch (DateTimeParseException e) {
            log.error("Failed to parse date string '{}': {}", dateString, e.getMessage());
            throw new DateTimeParseException(
                    "Invalid date format. Expected DD-MM-YYYY, got: " + dateString,
                    dateString,
                    e.getErrorIndex()
            );
        }
    }

    /**
     * Converts LocalDate back to string in DD-MM-YYYY format for response
     *
     * @param date The LocalDate to convert
     * @return String representation in DD-MM-YYYY format
     */
    private String formatDateToString(LocalDate date) {
        log.debug("Formatting date to string: {}", date);

        // Use the same formatter for consistency with request format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = date.format(formatter);

        log.debug("Formatted date: {} -> {}", date, formattedDate);
        return formattedDate;
    }

    /**
     * Calculates pricing for a single unit based on stay dates, guests, and segment
     * <p>
     * Calculation Flow:
     * 1. Find applicable pricing rules (rate plan with segment → default rate → empty)
     * 2. Calculate base daily rates for each night
     * 3. Apply additional guest fees based on guest count and age buckets
     * 4. Calculate summary statistics (total amount and average rate)
     *
     * @param unitId       The unit ID to calculate pricing for
     * @param checkinDate  The check-in date
     * @param checkoutDate The check-out date
     * @param guests       The guests information (adults and children)
     * @param segmentId    The segment ID (optional)
     * @return UnitPricingGetResource with calculated pricing or empty if no pricing rules found
     */
    private UnitPricingGetResource calculatePricingForUnit(String unitId, LocalDate checkinDate,
                                                           LocalDate checkoutDate, GuestsResource guests,
                                                           String segmentId) {
        log.debug("Calculating pricing for unit: {}, dates: {} to {}", unitId, checkinDate, checkoutDate);

        // Try rate plan first if segment provided
        List<NightRateGetResource> nightRates = null;
        if (StringUtils.hasText(segmentId)) {
            nightRates = tryCalculateFromRatePlan(unitId, segmentId, checkinDate, checkoutDate, guests);
        }

        // Fallback to default rate if no rate plan result
        if (nightRates == null) {
            nightRates = tryCalculateFromDefaultRate(unitId, checkinDate, checkoutDate, guests);
        }

        // If still no result, return empty
        if (nightRates == null) {
            log.warn("No pricing rules found for unit: {}", unitId);
            return createEmptyUnitPricing(unitId);
        }

        // Calculate summary statistics (average rate and total amount)
        // Compute the overall pricing metrics for the entire stay
        BigDecimal totalAmount = calculateTotalAmount(nightRates);
        BigDecimal averageRate = calculateAverageRate(nightRates);

        // Build the final response for this unit
        UnitPricingGetResource unitPricing = new UnitPricingGetResource();
        unitPricing.setId(unitId);
        unitPricing.setNightRates(nightRates);
        unitPricing.setNightlyRate(averageRate);
        unitPricing.setTotalAmount(totalAmount);

        log.info("Successfully calculated pricing for unit: {} - Total: {}, Average: {}",
                unitId, totalAmount, averageRate);

        return unitPricing;
    }

    /**
     * Tries to calculate pricing using rate plan for the given segment
     *
     * @return List of night rates or null if no applicable rate plan found
     */
    private List<NightRateGetResource> tryCalculateFromRatePlan(String unitId, String segmentId,
                                                                LocalDate checkinDate, LocalDate checkoutDate,
                                                                GuestsResource guests) {
        try {
            // Find active rate plan with segment for this unit
            Set<String> segmentUuids = Set.of(segmentId);
            List<RatePlanModel> ratePlans = ratePlanDaoService.findEnabledRatePlansWithOverlappingSegments(segmentUuids);

            Optional<RatePlanModel> applicableRatePlan = ratePlans.stream()
                    .filter(rp -> unitId.equals(rp.getUnit().getId()))
                    .findFirst();

            if (applicableRatePlan.isEmpty()) {
                return null;
            }

            RatePlanModel ratePlan = applicableRatePlan.get();
            List<RateTableModel> rateTables = rateTableDaoService.findCoveringRateTables(
                    ratePlan.getId(), checkinDate, checkoutDate);

            // Calculate rates and apply fees
            List<NightRateGetResource> nightRates = calculateDailyRatesFromRatePlan(
                    checkinDate, checkoutDate, ratePlan, rateTables);
            applyAdditionalGuestFeesFromRatePlan(nightRates, guests, ratePlan, rateTables);

            return nightRates;

        } catch (Exception e) {
            log.error("Error calculating from rate plan for unit: {}, segment: {}", unitId, segmentId, e);
            return null;
        }
    }

    /**
     * Tries to calculate pricing using default rate
     *
     * @return List of night rates or null if no default rate found
     */
    private List<NightRateGetResource> tryCalculateFromDefaultRate(String unitId, LocalDate checkinDate,
                                                                   LocalDate checkoutDate, GuestsResource guests) {
        try {
            DefaultRateModel defaultRate = defaultRateDaoService.findWithRelatedDataForPricing(unitId);
            if (defaultRate == null) {
                return null;
            }

            // Calculate rates and apply fees
            List<NightRateGetResource> nightRates = calculateDailyRatesFromDefaultRate(
                    checkinDate, checkoutDate, defaultRate);
            applyAdditionalGuestFeesFromDefaultRate(nightRates, guests, defaultRate);

            return nightRates;

        } catch (Exception e) {
            log.error("Error calculating from default rate for unit: {}", unitId, e);
            return null;
        }
    }

    /**
     * Calculates daily rates using rate plan and rate tables
     */
    private List<NightRateGetResource> calculateDailyRatesFromRatePlan(LocalDate checkinDate, LocalDate checkoutDate,
                                                                       RatePlanModel ratePlan, List<RateTableModel> rateTables) {
        List<NightRateGetResource> nightRates = new ArrayList<>();
        LocalDate currentDate = checkinDate;

        while (currentDate.isBefore(checkoutDate)) {
            // Find rate table covering this date
            RateTableModel coveringTable = rateTableDaoService.findRateTableForDate(ratePlan.getId(), currentDate);
            BigDecimal nightlyRate;

            if (coveringTable != null) {
                // Use rate table rate, check for day-specific overrides
                nightlyRate = coveringTable.getNightly();
                BigDecimal daySpecificRate = findDaySpecificRateInTable(currentDate.getDayOfWeek(), coveringTable);
                if (daySpecificRate != null) {
                    nightlyRate = daySpecificRate;
                }
            } else {
                // Fallback to default rate for this unit
                DefaultRateModel fallbackRate = defaultRateDaoService.findWithRelatedDataForPricing(ratePlan.getUnit().getId());
                if (fallbackRate != null) {
                    nightlyRate = fallbackRate.getNightly();
                    BigDecimal daySpecificRate = findDaySpecificRateInDefault(currentDate.getDayOfWeek(), fallbackRate);
                    if (daySpecificRate != null) {
                        nightlyRate = daySpecificRate;
                    }
                } else {
                    nightlyRate = BigDecimal.ZERO;
                }
            }

            NightRateGetResource nightRate = new NightRateGetResource();
            nightRate.setDate(formatDateToString(currentDate));
            nightRate.setRate(nightlyRate);
            nightRates.add(nightRate);

            currentDate = currentDate.plusDays(1);
        }

        return nightRates;
    }

    /**
     * Calculates daily rates using default rate
     */
    private List<NightRateGetResource> calculateDailyRatesFromDefaultRate(LocalDate checkinDate, LocalDate checkoutDate,
                                                                          DefaultRateModel defaultRate) {
        List<NightRateGetResource> nightRates = new ArrayList<>();
        LocalDate currentDate = checkinDate;

        while (currentDate.isBefore(checkoutDate)) {
            BigDecimal nightlyRate = defaultRate.getNightly();

            // Check for day-specific rate override
            BigDecimal daySpecificRate = findDaySpecificRateInDefault(currentDate.getDayOfWeek(), defaultRate);
            if (daySpecificRate != null) {
                nightlyRate = daySpecificRate;
            }

            NightRateGetResource nightRate = new NightRateGetResource();
            nightRate.setDate(formatDateToString(currentDate));
            nightRate.setRate(nightlyRate);
            nightRates.add(nightRate);

            currentDate = currentDate.plusDays(1);
        }

        return nightRates;
    }

    /**
     * Applies additional guest fees from rate plan (different fees per date based on coverage)
     */
    private void applyAdditionalGuestFeesFromRatePlan(List<NightRateGetResource> nightRates, GuestsResource guests,
                                                      RatePlanModel ratePlan, List<RateTableModel> rateTables) {

        // Get default rate fees as fallback
        DefaultRateModel defaultRate = defaultRateDaoService.findWithRelatedDataForPricing(ratePlan.getUnit().getId());
        List<AdditionalGuestFeeModel> defaultFees = (defaultRate != null) ? defaultRate.getAdditionalGuestFees() : new ArrayList<>();

        // Apply fees to each night individually based on date coverage
        for (NightRateGetResource nightRate : nightRates) {
            LocalDate nightDate = parseDateFromString(nightRate.getDate());

            // Find which rate table covers this specific date
            RateTableModel coveringTable = rateTableDaoService.findRateTableForDate(ratePlan.getId(), nightDate);

            List<AdditionalGuestFeeModel> feesToApply;
            if (coveringTable != null && !CollectionUtils.isEmpty(coveringTable.getAdditionalGuestFees())) {
                // Use rate table fees for covered dates
                feesToApply = coveringTable.getAdditionalGuestFees();
            } else {
                // Use default rate fees for non-covered dates
                feesToApply = defaultFees;
            }

            // Apply fees to this specific night
            applyFeesToSingleNight(nightRate, guests, feesToApply);
        }
    }

    /**
     * Applies additional guest fees to a single night rate
     */
    private void applyFeesToSingleNight(NightRateGetResource nightRate, GuestsResource guests,
                                        List<AdditionalGuestFeeModel> additionalFees) {
        if (CollectionUtils.isEmpty(additionalFees)) {
            return;
        }

        BigDecimal baseRate = nightRate.getRate();

        // Calculate fee amounts
        BigDecimal adultFeeAmount = calculateAdultFees(guests.getAdults(), additionalFees);
        Map<String, BigDecimal> childFeeAmounts = calculateChildFees(guests.getChildren(), additionalFees);

        // Apply adult fees
        BigDecimal totalAdultFee = calculateFinalFeeAmount(baseRate, adultFeeAmount, additionalFees, GuestTypeEnum.ADULT);

        // Apply child fees
        BigDecimal totalChildFee = BigDecimal.ZERO;
        for (Map.Entry<String, BigDecimal> childEntry : childFeeAmounts.entrySet()) {
            BigDecimal childFeeForBucket = calculateFinalFeeAmount(baseRate, childEntry.getValue(), additionalFees, GuestTypeEnum.CHILD);
            totalChildFee = totalChildFee.add(childFeeForBucket);
        }

        // Update the night rate
        BigDecimal finalRate = baseRate.add(totalAdultFee).add(totalChildFee);
        nightRate.setRate(finalRate);

        log.debug("Applied fees to {}: {} + {} (adult) + {} (child) = {}",
                nightRate.getDate(), baseRate, totalAdultFee, totalChildFee, finalRate);
    }

    /**
     * Applies additional guest fees from default rate
     */
    private void applyAdditionalGuestFeesFromDefaultRate(List<NightRateGetResource> nightRates, GuestsResource guests,
                                                         DefaultRateModel defaultRate) {
        applyFeesToNightRates(nightRates, guests, defaultRate.getAdditionalGuestFees());
    }

    /**
     * Finds day-specific rate in a rate table for a given day of week
     */
    private BigDecimal findDaySpecificRateInTable(DayOfWeek dayOfWeek, RateTableModel rateTable) {
        return rateTable.getDaySpecificRates().stream()
                .filter(dsr -> dsr.getDays().contains(dayOfWeek))
                .map(DaySpecificRateModel::getNightly)
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds day-specific rate in a default rate for a given day of week
     */
    private BigDecimal findDaySpecificRateInDefault(DayOfWeek dayOfWeek, DefaultRateModel defaultRate) {
        return defaultRate.getDaySpecificRates().stream()
                .filter(dsr -> dsr.getDays().contains(dayOfWeek))
                .map(DaySpecificRateModel::getNightly)
                .findFirst()
                .orElse(null);
    }

    /**
     * Applies additional guest fees to night rates (refactored from original method)
     */
    private void applyFeesToNightRates(List<NightRateGetResource> nightRates, GuestsResource guests,
                                       List<AdditionalGuestFeeModel> additionalFees) {
        if (CollectionUtils.isEmpty(additionalFees)) {
            log.debug("No additional guest fees defined, skipping fee application");
            return;
        }

        // Calculate fee amounts for adults and children
        BigDecimal adultFeeAmount = calculateAdultFees(guests.getAdults(), additionalFees);
        Map<String, BigDecimal> childFeeAmounts = calculateChildFees(guests.getChildren(), additionalFees);

        log.debug("Calculated fee amounts - Adults: {}, Children: {}", adultFeeAmount, childFeeAmounts);

        // Apply fees to each night rate
        for (NightRateGetResource nightRate : nightRates) {
            BigDecimal baseRate = nightRate.getRate();

            // Apply adult fees (can be FLAT or PERCENT)
            BigDecimal totalAdultFee = calculateFinalFeeAmount(baseRate, adultFeeAmount, additionalFees, GuestTypeEnum.ADULT);

            // Apply child fees (can be FLAT or PERCENT)
            BigDecimal totalChildFee = BigDecimal.ZERO;
            for (Map.Entry<String, BigDecimal> childEntry : childFeeAmounts.entrySet()) {
                BigDecimal childFeeForBucket = calculateFinalFeeAmount(baseRate, childEntry.getValue(), additionalFees, GuestTypeEnum.CHILD);
                totalChildFee = totalChildFee.add(childFeeForBucket);
            }

            // Update the night rate with all additional fees
            BigDecimal finalRate = baseRate.add(totalAdultFee).add(totalChildFee);
            nightRate.setRate(finalRate);

            log.debug("Applied fees to {}: {} + {} (adult) + {} (child) = {}",
                    nightRate.getDate(), baseRate, totalAdultFee, totalChildFee, finalRate);
        }

        log.debug("Successfully applied additional guest fees to all night rates");
    }

    /**
     * Calculates additional fees for adults
     */
    private BigDecimal calculateAdultFees(int adultCount, List<AdditionalGuestFeeModel> additionalFees) {
        log.debug("Calculating adult fees for {} adults", adultCount);

        Optional<AdditionalGuestFeeModel> adultFee = additionalFees.stream()
                .filter(fee -> GuestTypeEnum.ADULT.equals(fee.getGuestType()))
                .findFirst();

        if (adultFee.isEmpty()) {
            log.debug("No adult fee configuration found");
            return BigDecimal.ZERO;
        }

        int extraAdults = Math.max(0, adultCount - 1);
        if (extraAdults == 0) {
            log.debug("No extra adults, no additional fee");
            return BigDecimal.ZERO;
        }

        AdditionalGuestFeeModel fee = adultFee.get();
        BigDecimal feePerGuest = fee.getValue();
        BigDecimal totalFee = feePerGuest.multiply(BigDecimal.valueOf(extraAdults));

        log.debug("Adult fee calculation: {} extra adults × {} {} = {}",
                extraAdults, feePerGuest, fee.getAmountType(), totalFee);

        return totalFee;
    }

    /**
     * Calculates additional fees for children based on age buckets
     */
    private Map<String, BigDecimal> calculateChildFees(List<ChildResource> children, List<AdditionalGuestFeeModel> additionalFees) {
        Map<String, BigDecimal> childFeesByBucket = new HashMap<>();

        if (CollectionUtils.isEmpty(children)) {
            log.debug("No children, no child fees");
            return childFeesByBucket;
        }

        log.debug("Calculating child fees for {} child entries", children.size());

        List<AdditionalGuestFeeModel> childFees = additionalFees.stream()
                .filter(fee -> GuestTypeEnum.CHILD.equals(fee.getGuestType()))
                .filter(fee -> fee.getAgeBucket() != null)
                .collect(Collectors.toList());

        if (childFees.isEmpty()) {
            log.debug("No child fee configurations found");
            return childFeesByBucket;
        }

        for (AdditionalGuestFeeModel childFee : childFees) {
            AgeBucketEmbeddable ageBucket = childFee.getAgeBucket();
            String bucketId = ageBucket.getFromAge() + "-" + ageBucket.getToAge();
            int totalChildrenInBucket = 0;

            for (ChildResource child : children) {
                if (child.getAge() >= ageBucket.getFromAge() && child.getAge() <= ageBucket.getToAge()) {
                    totalChildrenInBucket += child.getQuantity();
                }
            }

            int extraChildren = Math.max(0, totalChildrenInBucket - 1);
            if (extraChildren > 0) {
                BigDecimal feeForThisBucket = childFee.getValue().multiply(BigDecimal.valueOf(extraChildren));
                childFeesByBucket.put(bucketId, feeForThisBucket);

                log.debug("Child fee for age bucket {}: {} extra children × {} {} = {}",
                        bucketId, extraChildren, childFee.getValue(), childFee.getAmountType(), feeForThisBucket);
            }
        }

        return childFeesByBucket;
    }

    /**
     * Calculates the final fee amount considering FLAT vs PERCENT fee types
     */
    private BigDecimal calculateFinalFeeAmount(BigDecimal baseRate, BigDecimal feeAmount,
                                               List<AdditionalGuestFeeModel> additionalFees,
                                               GuestTypeEnum guestType) {
        if (feeAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        Optional<AdditionalGuestFeeModel> feeConfig = additionalFees.stream()
                .filter(fee -> guestType.equals(fee.getGuestType()))
                .findFirst();

        if (feeConfig.isEmpty()) {
            return feeAmount;
        }

        if (AmountTypeEnum.PERCENT.equals(feeConfig.get().getAmountType())) {
            BigDecimal percentageValue = feeConfig.get().getValue();
            BigDecimal percentageFee = baseRate.multiply(percentageValue).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal extraGuestMultiplier = feeAmount.divide(percentageValue, 2, RoundingMode.HALF_UP);
            BigDecimal totalPercentageFee = percentageFee.multiply(extraGuestMultiplier);

            log.debug("Percentage fee calculation: {}% of {} × {} extra guests = {}",
                    percentageValue, baseRate, extraGuestMultiplier, totalPercentageFee);

            return totalPercentageFee;
        }

        return feeAmount;
    }

    /**
     * Calculates the total amount for all nights
     */
    private BigDecimal calculateTotalAmount(List<NightRateGetResource> nightRates) {
        BigDecimal total = nightRates.stream()
                .map(NightRateGetResource::getRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.debug("Calculated total amount: {}", total);
        return total;
    }

    /**
     * Calculates the average rate across all nights
     */
    private BigDecimal calculateAverageRate(List<NightRateGetResource> nightRates) {
        if (nightRates.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = calculateTotalAmount(nightRates);
        BigDecimal average = total.divide(BigDecimal.valueOf(nightRates.size()), 2, RoundingMode.HALF_UP);

        log.debug("Calculated average rate: {}", average);
        return average;
    }


    /**
     * Creates an empty unit pricing result when no pricing rules are available
     *
     * @param unitId The unit ID
     * @return Empty UnitPricingGetResource indicating pricing calculation failed
     */
    private UnitPricingGetResource createEmptyUnitPricing(String unitId) {
        log.debug("Creating empty pricing result for unit: {}", unitId);

        UnitPricingGetResource emptyPricing = new UnitPricingGetResource();
        emptyPricing.setId(unitId);
        emptyPricing.setNightRates(new ArrayList<>()); // Empty list
        emptyPricing.setNightlyRate(BigDecimal.ZERO);
        emptyPricing.setTotalAmount(BigDecimal.ZERO);

        return emptyPricing;
    }
}

