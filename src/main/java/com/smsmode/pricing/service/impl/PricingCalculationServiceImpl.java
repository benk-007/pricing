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
import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of PricingCalculationService for calculating unit pricing based on dates, guests, and segments.
 *
 * Business Logic Flow:
 * 1. Parse dates from request
 * 2. For each unit, find pricing rules (rate plan with segment → default rate → empty)
 * 3. Calculate daily base rates using MAX logic between all sources
 * 4. Apply additional guest fees using MAX logic with final nightly rate for percentages
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

        PriceCalculationGetResource response = new PriceCalculationGetResource();
        response.setUnits(unitPricings);

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
        if (StringUtils.hasText(segmentId)) {
            nightRates = tryCalculateFromRatePlan(unitId, segmentId, checkinDate, checkoutDate, guests);
        }

        if (nightRates == null) {
            nightRates = tryCalculateFromDefaultRate(unitId, checkinDate, checkoutDate, guests);
        }

        if (nightRates == null) {
            log.warn("No pricing rules found for unit: {}", unitId);
            return createEmptyUnitPricing(unitId);
        }

        BigDecimal totalAmount = calculateTotalAmount(nightRates);
        BigDecimal averageRate = calculateAverageRate(nightRates);

        UnitPricingGetResource unitPricing = new UnitPricingGetResource();
        unitPricing.setId(unitId);
        unitPricing.setNightRates(nightRates);
        unitPricing.setNightlyRate(averageRate);
        unitPricing.setTotalAmount(totalAmount);

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
     * Calculates daily rates using rate plan with MAX logic and applies all fees
     */
    private List<NightRateGetResource> calculateDailyRatesFromRatePlan(LocalDate checkinDate, LocalDate checkoutDate,
                                                                       RatePlanModel ratePlan, GuestsResource guests) {
        List<NightRateGetResource> nightRates = new ArrayList<>();
        LocalDate currentDate = checkinDate;

        DefaultRateModel defaultRate = defaultRateDaoService.findWithRelatedDataForPricing(ratePlan.getUnit().getId());

        while (currentDate.isBefore(checkoutDate)) {
            RateTableModel coveringTable = rateTableDaoService.findRateTableForDate(ratePlan.getId(), currentDate);

            // Calculate MAX nightly rate from all sources
            BigDecimal nightlyRate = calculateMaxNightlyRate(currentDate, coveringTable, defaultRate);

            // Calculate and apply all fees using the final nightly rate
            BigDecimal finalRate = applyAllFees(nightlyRate, guests, coveringTable, defaultRate);

            NightRateGetResource nightRate = new NightRateGetResource();
            nightRate.setDate(formatDateToString(currentDate));
            nightRate.setRate(finalRate);
            nightRates.add(nightRate);

            log.debug("Calculated final rate for {}: {} (base: {})", currentDate, finalRate, nightlyRate);
            currentDate = currentDate.plusDays(1);
        }

        return nightRates;
    }

    /**
     * Calculates daily rates using default rate only and applies fees
     */
    private List<NightRateGetResource> calculateDailyRatesFromDefaultRate(LocalDate checkinDate, LocalDate checkoutDate,
                                                                          DefaultRateModel defaultRate, GuestsResource guests) {
        List<NightRateGetResource> nightRates = new ArrayList<>();
        LocalDate currentDate = checkinDate;

        while (currentDate.isBefore(checkoutDate)) {
            BigDecimal nightlyRate = defaultRate.getNightly();

            // Check for day-specific rate override
            BigDecimal daySpecificRate = findDaySpecificRateInDefault(currentDate.getDayOfWeek(), defaultRate);
            if (daySpecificRate != null) {
                nightlyRate = daySpecificRate;
            }

            // Apply fees using default rate only
            BigDecimal finalRate = applyFeesFromSingleSource(nightlyRate, guests, defaultRate.getAdditionalGuestFees());

            NightRateGetResource nightRate = new NightRateGetResource();
            nightRate.setDate(formatDateToString(currentDate));
            nightRate.setRate(finalRate);
            nightRates.add(nightRate);

            currentDate = currentDate.plusDays(1);
        }

        return nightRates;
    }

    /**
     * Calculates MAX nightly rate from all available sources
     */
    private BigDecimal calculateMaxNightlyRate(LocalDate date, RateTableModel rateTable, DefaultRateModel defaultRate) {
        List<BigDecimal> allRates = new ArrayList<>();

        // Rate table rates
        if (rateTable != null) {
            if (rateTable.getNightly() != null) {
                allRates.add(rateTable.getNightly());
            }
            BigDecimal rateTableDaySpecific = findDaySpecificRateInTable(date.getDayOfWeek(), rateTable);
            if (rateTableDaySpecific != null) {
                allRates.add(rateTableDaySpecific);
            }
        }

        // Default rate rates
        if (defaultRate != null) {
            if (defaultRate.getNightly() != null) {
                allRates.add(defaultRate.getNightly());
            }
            BigDecimal defaultDaySpecific = findDaySpecificRateInDefault(date.getDayOfWeek(), defaultRate);
            if (defaultDaySpecific != null) {
                allRates.add(defaultDaySpecific);
            }
        }

        BigDecimal maxRate = allRates.stream()
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        log.debug("MAX nightly rate for {}: {} (from {} sources)", date, maxRate, allRates.size());
        return maxRate;
    }

    /**
     * Applies all fees (adult + child) using MAX logic with final nightly rate for percentages
     */
    private BigDecimal applyAllFees(BigDecimal baseNightlyRate, GuestsResource guests,
                                    RateTableModel rateTable, DefaultRateModel defaultRate) {

        // Calculate MAX adult fees (unchanged)
        BigDecimal rateTableAdultFees = calculateAdultFees(guests.getAdults(),
                rateTable != null ? rateTable.getAdditionalGuestFees() : null);
        BigDecimal defaultAdultFees = calculateAdultFees(guests.getAdults(),
                defaultRate != null ? defaultRate.getAdditionalGuestFees() : null);
        BigDecimal maxAdultFees = rateTableAdultFees.max(defaultAdultFees);

        // Calculate child fees by combining all buckets from both sources
        List<AdditionalGuestFeeModel> allChildFees = new ArrayList<>();

        if (rateTable != null && !CollectionUtils.isEmpty(rateTable.getAdditionalGuestFees())) {
            allChildFees.addAll(rateTable.getAdditionalGuestFees().stream()
                    .filter(fee -> GuestTypeEnum.CHILD.equals(fee.getGuestType()))
                    .collect(Collectors.toList()));
        }

        if (defaultRate != null && !CollectionUtils.isEmpty(defaultRate.getAdditionalGuestFees())) {
            allChildFees.addAll(defaultRate.getAdditionalGuestFees().stream()
                    .filter(fee -> GuestTypeEnum.CHILD.equals(fee.getGuestType()))
                    .collect(Collectors.toList()));
        }

        // Calculate child fees using all available buckets and final nightly rate
        BigDecimal totalChildFees = calculateChildFees(guests.getChildren(), allChildFees, baseNightlyRate);

        BigDecimal finalRate = baseNightlyRate.add(maxAdultFees).add(totalChildFees);

        log.debug("Applied fees: {} + {} (adult) + {} (child) = {}",
                baseNightlyRate, maxAdultFees, totalChildFees, finalRate);

        return finalRate;
    }

    /**
     * Applies fees from a single source (for default rate only scenario)
     */
    private BigDecimal applyFeesFromSingleSource(BigDecimal baseNightlyRate, GuestsResource guests,
                                                 List<AdditionalGuestFeeModel> additionalFees) {
        BigDecimal adultFees = calculateAdultFees(guests.getAdults(), additionalFees);
        BigDecimal childFees = calculateChildFees(guests.getChildren(), additionalFees, baseNightlyRate);

        return baseNightlyRate.add(adultFees).add(childFees);
    }

    /**
     * Calculates adult fees from a single source
     */
    private BigDecimal calculateAdultFees(int adultCount, List<AdditionalGuestFeeModel> additionalFees) {
        if (CollectionUtils.isEmpty(additionalFees)) {
            return BigDecimal.ZERO;
        }

        Optional<AdditionalGuestFeeModel> adultFee = additionalFees.stream()
                .filter(fee -> GuestTypeEnum.ADULT.equals(fee.getGuestType()))
                .findFirst();

        if (adultFee.isEmpty()) {
            return BigDecimal.ZERO;
        }

        AdditionalGuestFeeModel fee = adultFee.get();
        int extraAdults = Math.max(0, adultCount - fee.getGuestCount());

        return extraAdults == 0 ? BigDecimal.ZERO :
                fee.getValue().multiply(BigDecimal.valueOf(extraAdults));
    }

    /**
     * Calculates child fees from a single source using final nightly rate for percentages
     */
    private BigDecimal calculateChildFees(List<ChildResource> children,
                                          List<AdditionalGuestFeeModel> additionalFees,
                                          BigDecimal finalNightlyRate) {
        if (CollectionUtils.isEmpty(children) || CollectionUtils.isEmpty(additionalFees)) {
            return BigDecimal.ZERO;
        }

        // Get all available child fee buckets
        List<AdditionalGuestFeeModel> childFees = additionalFees.stream()
                .filter(fee -> GuestTypeEnum.CHILD.equals(fee.getGuestType()))
                .filter(fee -> fee.getAgeBucket() != null)
                .collect(Collectors.toList());

        if (childFees.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Calculate optimal fees using dynamic programming approach
        BigDecimal maxTotalFees = calculateOptimalChildFees(children, childFees, finalNightlyRate);

        log.debug("Optimal child fees: {}", maxTotalFees);
        return maxTotalFees;
    }

    /**
     *  Calcule les fees optimaux en considérant toutes les combinaisons
     */
    private BigDecimal calculateOptimalChildFees(List<ChildResource> children,
                                                 List<AdditionalGuestFeeModel> childFees,
                                                 BigDecimal finalNightlyRate) {

        // Convert children to a map for easier processing
        Map<Integer, Integer> childrenByAge = children.stream()
                .collect(Collectors.groupingBy(
                        ChildResource::getAge,
                        Collectors.summingInt(ChildResource::getQuantity)
                ));

        // Try all possible combinations of bucket assignments
        return findMaxFeesCombination(new ArrayList<>(childrenByAge.keySet()),
                childrenByAge, childFees, finalNightlyRate);
    }

    /**
     *  Trouve la combinaison optimale de buckets
     */
    private BigDecimal findMaxFeesCombination(List<Integer> ages,
                                              Map<Integer, Integer> childrenByAge,
                                              List<AdditionalGuestFeeModel> childFees,
                                              BigDecimal finalNightlyRate) {

        BigDecimal maxFees = BigDecimal.ZERO;

        // Generate all possible bucket combinations for the given ages
        List<BucketCombination> combinations = generateBucketCombinations(ages, childFees);

        for (BucketCombination combination : combinations) {
            BigDecimal totalFeesForCombination = calculateFeesForCombination(
                    combination, childrenByAge, finalNightlyRate);
            maxFees = maxFees.max(totalFeesForCombination);

            log.debug("Combination: {} -> Fees: {}", combination, totalFeesForCombination);
        }

        return maxFees;
    }

    /**
     *  Génère toutes les combinaisons possibles de buckets
     */
    private List<BucketCombination> generateBucketCombinations(List<Integer> ages,
                                                               List<AdditionalGuestFeeModel> childFees) {
        List<BucketCombination> combinations = new ArrayList<>();

        // Option 1: Individual age assignments
        combinations.add(createIndividualAgeCombination(ages, childFees));

        // Option 2: Group assignments (buckets that cover multiple ages)
        combinations.addAll(createGroupCombinations(ages, childFees));

        return combinations;
    }

    /**
     *  Crée la combinaison par âge individuel
     */
    private BucketCombination createIndividualAgeCombination(List<Integer> ages,
                                                             List<AdditionalGuestFeeModel> childFees) {
        BucketCombination combination = new BucketCombination();

        for (Integer age : ages) {
            // Find best bucket for this specific age
            AdditionalGuestFeeModel bestBucket = findBestBucketForAge(age, childFees);
            if (bestBucket != null) {
                combination.addAssignment(age, bestBucket);
            }
        }

        return combination;
    }

    /**
     * Crée les combinaisons de groupes
     */
    private List<BucketCombination> createGroupCombinations(List<Integer> ages,
                                                            List<AdditionalGuestFeeModel> childFees) {
        List<BucketCombination> groupCombinations = new ArrayList<>();

        // For each bucket, check if it can cover multiple ages
        for (AdditionalGuestFeeModel bucket : childFees) {
            List<Integer> coveredAges = ages.stream()
                    .filter(age -> age >= bucket.getAgeBucket().getFromAge() &&
                            age <= bucket.getAgeBucket().getToAge())
                    .collect(Collectors.toList());

            if (coveredAges.size() > 1) {
                // This bucket can cover multiple ages - create a group combination
                BucketCombination groupCombination = new BucketCombination();
                for (Integer age : coveredAges) {
                    groupCombination.addAssignment(age, bucket);
                }
                groupCombinations.add(groupCombination);
            }
        }

        return groupCombinations;
    }

    /**
     * Trouve le meilleur bucket pour un âge spécifique
     */
    private AdditionalGuestFeeModel findBestBucketForAge(Integer age, List<AdditionalGuestFeeModel> childFees) {
        return childFees.stream()
                .filter(fee -> age >= fee.getAgeBucket().getFromAge() &&
                        age <= fee.getAgeBucket().getToAge())
                .max((fee1, fee2) -> {
                    // Compare based on fee value (higher percentage/amount = better for hotel)
                    if (fee1.getAmountType() == fee2.getAmountType()) {
                        return fee1.getValue().compareTo(fee2.getValue());
                    }
                    // If different types, prefer PERCENT over FLAT for comparison
                    return fee1.getAmountType() == AmountTypeEnum.PERCENT ? 1 : -1;
                })
                .orElse(null);
    }

    /**
     * Calcule les fees pour une combinaison donnée
     */
    private BigDecimal calculateFeesForCombination(BucketCombination combination,
                                                   Map<Integer, Integer> childrenByAge,
                                                   BigDecimal finalNightlyRate) {
        BigDecimal totalFees = BigDecimal.ZERO;

        // Group assignments by bucket to avoid double counting
        Map<AdditionalGuestFeeModel, List<Integer>> bucketToAges = new HashMap<>();

        for (Map.Entry<Integer, AdditionalGuestFeeModel> entry : combination.getAssignments().entrySet()) {
            Integer age = entry.getKey();
            AdditionalGuestFeeModel bucket = entry.getValue();

            bucketToAges.computeIfAbsent(bucket, k -> new ArrayList<>()).add(age);
        }

        // Calculate fees for each bucket
        for (Map.Entry<AdditionalGuestFeeModel, List<Integer>> entry : bucketToAges.entrySet()) {
            AdditionalGuestFeeModel bucket = entry.getKey();
            List<Integer> agesInBucket = entry.getValue();

            // Calculate total children covered by this bucket
            int totalChildrenInBucket = agesInBucket.stream()
                    .mapToInt(age -> childrenByAge.getOrDefault(age, 0))
                    .sum();

            // Calculate fees for this bucket
            int extraChildren = Math.max(0, totalChildrenInBucket - bucket.getGuestCount());

            if (extraChildren > 0) {
                BigDecimal bucketFee;

                if (AmountTypeEnum.PERCENT.equals(bucket.getAmountType())) {
                    bucketFee = finalNightlyRate.multiply(bucket.getValue())
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(extraChildren));
                } else {
                    bucketFee = bucket.getValue().multiply(BigDecimal.valueOf(extraChildren));
                }

                totalFees = totalFees.add(bucketFee);

                log.debug("Bucket {}-{}: {} children -> {} extra -> fee: {}",
                        bucket.getAgeBucket().getFromAge(), bucket.getAgeBucket().getToAge(),
                        totalChildrenInBucket, extraChildren, bucketFee);
            }
        }

        return totalFees;
    }

    /**
     * Représente une combinaison d'assignation âges->buckets
     */
    private static class BucketCombination {
        private final Map<Integer, AdditionalGuestFeeModel> assignments = new HashMap<>();

        public void addAssignment(Integer age, AdditionalGuestFeeModel bucket) {
            assignments.put(age, bucket);
        }

        public Map<Integer, AdditionalGuestFeeModel> getAssignments() {
            return assignments;
        }

        @Override
        public String toString() {
            return assignments.entrySet().stream()
                    .map(entry -> "Age " + entry.getKey() + " -> " +
                            entry.getValue().getAgeBucket().getFromAge() + "-" +
                            entry.getValue().getAgeBucket().getToAge())
                    .collect(Collectors.joining(", "));
        }
    }

    private BigDecimal calculateMaxFeeForAge(int age, int quantity,
                                             List<AdditionalGuestFeeModel> additionalFees,
                                             BigDecimal finalNightlyRate) {
        BigDecimal maxFee = BigDecimal.ZERO;

        // Find all buckets that cover this age
        List<AdditionalGuestFeeModel> applicableFees = additionalFees.stream()
                .filter(fee -> GuestTypeEnum.CHILD.equals(fee.getGuestType()))
                .filter(fee -> fee.getAgeBucket() != null)
                .filter(fee -> age >= fee.getAgeBucket().getFromAge() && age <= fee.getAgeBucket().getToAge())
                .collect(Collectors.toList());

        // Calculate fee for each applicable bucket and take MAX
        for (AdditionalGuestFeeModel fee : applicableFees) {
            int extraChildren = Math.max(0, quantity - fee.getGuestCount());

            if (extraChildren > 0) {
                BigDecimal feeAmount;

                if (AmountTypeEnum.PERCENT.equals(fee.getAmountType())) {
                    // Use final nightly rate for percentage calculations
                    feeAmount = finalNightlyRate.multiply(fee.getValue())
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(extraChildren));
                } else {
                    // FLAT amount
                    feeAmount = fee.getValue().multiply(BigDecimal.valueOf(extraChildren));
                }

                maxFee = maxFee.max(feeAmount);

                log.debug("Fee for age {} in bucket {}-{}: {} ({}% of {} × {} extra children)",
                        age, fee.getAgeBucket().getFromAge(), fee.getAgeBucket().getToAge(),
                        feeAmount, fee.getValue(), finalNightlyRate, extraChildren);
            }
        }

        return maxFee;
    }

    private BigDecimal findDaySpecificRateInTable(DayOfWeek dayOfWeek, RateTableModel rateTable) {
        if (rateTable == null || CollectionUtils.isEmpty(rateTable.getDaySpecificRates())) {
            return null;
        }

        return rateTable.getDaySpecificRates().stream()
                .filter(dsr -> dsr.getDays().contains(dayOfWeek))
                .map(DaySpecificRateModel::getNightly)
                .findFirst()
                .orElse(null);
    }

    private BigDecimal findDaySpecificRateInDefault(DayOfWeek dayOfWeek, DefaultRateModel defaultRate) {
        if (defaultRate == null || CollectionUtils.isEmpty(defaultRate.getDaySpecificRates())) {
            return null;
        }

        return defaultRate.getDaySpecificRates().stream()
                .filter(dsr -> dsr.getDays().contains(dayOfWeek))
                .map(DaySpecificRateModel::getNightly)
                .findFirst()
                .orElse(null);
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
        return emptyPricing;
    }
}