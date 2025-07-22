package com.smsmode.pricing.mapper;

import com.smsmode.pricing.dao.service.RatePlanDaoService;
import com.smsmode.pricing.embeddable.RatePlanRefEmbeddable;
import com.smsmode.pricing.model.*;
import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeeGetResource;
import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeePostResource;
import com.smsmode.pricing.resource.common.dayspecificrate.DaySpecificRateGetResource;
import com.smsmode.pricing.resource.common.dayspecificrate.DaySpecificRatePostResource;
import com.smsmode.pricing.resource.ratetable.RateTableGetResource;
import com.smsmode.pricing.resource.ratetable.RateTablePatchResource;
import com.smsmode.pricing.resource.ratetable.RateTablePostResource;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Mapper for RateTable entities and resources.
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)public abstract class RateTableMapper {

    private RatePlanDaoService ratePlanDaoService;

    @Autowired
    void setRatePlanDaoService(RatePlanDaoService ratePlanDaoService) {
        this.ratePlanDaoService = ratePlanDaoService;
    }

    /**
     * Maps RateTablePostResource to RateTableModel for creation.
     */
    @Mapping(target = "additionalGuestFees", source = "additionalGuestFees", qualifiedByName = "mapAdditionalGuestFees")
    @Mapping(target = "daySpecificRates", source = "daySpecificRates", qualifiedByName = "mapDaySpecificRates")
    public abstract RateTableModel postResourceToModel(RateTablePostResource rateTablePostResource);

    /**
     * Maps RateTableModel to RateTableGetResource for response.
     */
    @Mapping(target = "ratePlan", source = ".", qualifiedByName = "mapRatePlanToRef")
    public abstract RateTableGetResource modelToGetResource(RateTableModel rateTableModel);

    /**
     * Updates existing RateTableModel from RateTablePatchResource for update operations.
     */
    @Mapping(target = "additionalGuestFees", ignore = true)
    @Mapping(target = "daySpecificRates", ignore = true)
    public abstract void updateModelFromPatchResource(RateTablePatchResource rateTablePatchResource, @MappingTarget RateTableModel rateTableModel);

    @AfterMapping
    public void afterPatchMapping(RateTablePatchResource patch, @MappingTarget RateTableModel model) {
        // Résolution automatique du RatePlan si fourni
        if (patch.getRatePlan() != null && patch.getRatePlan().getId() != null) {
            RatePlanModel ratePlan = ratePlanDaoService.findById(patch.getRatePlan().getId());
            model.setRatePlan(ratePlan);
        }
    }

    public abstract AdditionalGuestFeeGetResource additionalGuestFeeModelToGetResource(AdditionalGuestFeeModel additionalGuestFeeModel);
    public abstract AdditionalGuestFeeModel additionalGuestFeePostResourceToModel(AdditionalGuestFeePostResource additionalGuestFeePostResource);
    public abstract DaySpecificRateGetResource daySpecificRateModelToGetResource(DaySpecificRateModel daySpecificRateModel);
    public abstract DaySpecificRateModel daySpecificRatePostResourceToModel(DaySpecificRatePostResource daySpecificRatePostResource);


    public abstract void updateAdditionalGuestFeeFromResource(AdditionalGuestFeePostResource source, @MappingTarget AdditionalGuestFeeModel target);

    public abstract void updateDaySpecificRateFromResource(DaySpecificRatePostResource source, @MappingTarget DaySpecificRateModel target);


    /**
     * Custom mapping method for additional guest fees collection
     */
    @Named("mapAdditionalGuestFees")
    public List<AdditionalGuestFeeModel> mapAdditionalGuestFees(List<AdditionalGuestFeePostResource> additionalGuestFees) {
        if (CollectionUtils.isEmpty(additionalGuestFees)) {
            return new ArrayList<>();
        }
        List<AdditionalGuestFeeModel> result = new ArrayList<>();
        for (AdditionalGuestFeePostResource resource : additionalGuestFees) {
            result.add(additionalGuestFeePostResourceToModel(resource));
        }
        return result;
    }

    /**
     * Custom mapping method for day specific rates collection
     */
    @Named("mapDaySpecificRates")
    public List<DaySpecificRateModel> mapDaySpecificRates(List<DaySpecificRatePostResource> daySpecificRates) {
        if (CollectionUtils.isEmpty(daySpecificRates)) {
            return new ArrayList<>();
        }
        List<DaySpecificRateModel> result = new ArrayList<>();
        for (DaySpecificRatePostResource resource : daySpecificRates) {
            result.add(daySpecificRatePostResourceToModel(resource));
        }
        return result;
    }

    /**
     * Convert RatePlanModel to RatePlanRefEmbeddable
     */
    @Named("mapRatePlanToRef")
    public RatePlanRefEmbeddable mapRatePlanToRef(RateTableModel rateTableModel) {
        if (rateTableModel.getRatePlan() == null) {
            return null;
        }
        return new RatePlanRefEmbeddable(rateTableModel.getRatePlan().getId());
    }

    public RatePlanModel resolveRatePlan(String ratePlanUuid) {
        if (ratePlanUuid == null) {
            return null;
        }
        return ratePlanDaoService.findById(ratePlanUuid);
    }
}