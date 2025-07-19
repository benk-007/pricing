package com.smsmode.pricing.mapper;

import com.smsmode.pricing.dao.service.RatePlanDaoService;
import com.smsmode.pricing.embeddable.RatePlanRefEmbeddable;
import com.smsmode.pricing.model.RatePlanModel;
import com.smsmode.pricing.model.RateTableAdditionalGuestFeeModel;
import com.smsmode.pricing.model.RateTableDaySpecificRateModel;
import com.smsmode.pricing.model.RateTableModel;
import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeeGetResource;
import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeePostResource;
import com.smsmode.pricing.resource.common.dayspecificrate.DaySpecificRateGetResource;
import com.smsmode.pricing.resource.common.dayspecificrate.DaySpecificRatePostResource;
import com.smsmode.pricing.resource.ratetable.RateTableGetResource;
import com.smsmode.pricing.resource.ratetable.RateTablePatchResource;
import com.smsmode.pricing.resource.ratetable.RateTablePostResource;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for RateTable entities and resources.
 */
@Mapper(
        componentModel = "spring",
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class RateTableMapper {

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
        // Résoudre RatePlan si fourni
        if (patch.getRatePlan() != null && patch.getRatePlan().getUuid() != null) {
            RatePlanModel ratePlan = ratePlanDaoService.findById(patch.getRatePlan().getUuid());
            model.setRatePlan(ratePlan);
        }
    }
    /**
     * Convertit RatePlanModel vers RatePlanRefEmbeddable pour les GET responses
     * L'ID technique devient l'UUID dans le JSON
     */
    @Named("mapRatePlanToRef")
    public RatePlanRefEmbeddable mapRatePlanToRef(RateTableModel rateTableModel) {
        if (rateTableModel.getRatePlan() == null) {
            return null;
        }
        return new RatePlanRefEmbeddable(rateTableModel.getRatePlan().getId());
    }

    /**
     * Résout un RatePlan à partir de son UUID (qui est en fait l'ID technique)
     */
    public RatePlanModel resolveRatePlan(String ratePlanUuid) {
        if (ratePlanUuid == null) {
            return null;
        }
        return ratePlanDaoService.findById(ratePlanUuid);
    }

    /**
     * Custom mapping method for additional guest fees collection.
     */
    @Named("mapAdditionalGuestFees")
    public List<RateTableAdditionalGuestFeeModel> mapAdditionalGuestFees(List<AdditionalGuestFeePostResource> additionalGuestFees) {
        if (additionalGuestFees == null) {
            return new ArrayList<>();
        }
        List<RateTableAdditionalGuestFeeModel> result = new ArrayList<>();
        for (AdditionalGuestFeePostResource resource : additionalGuestFees) {
            result.add(additionalGuestFeePostResourceToModel(resource));
        }
        return result;
    }

    /**
     * Custom mapping method for day specific rates collection.
     */
    @Named("mapDaySpecificRates")
    public List<RateTableDaySpecificRateModel> mapDaySpecificRates(List<DaySpecificRatePostResource> daySpecificRates) {
        if (daySpecificRates == null) {
            return new ArrayList<>();
        }
        List<RateTableDaySpecificRateModel> result = new ArrayList<>();
        for (DaySpecificRatePostResource resource : daySpecificRates) {
            result.add(daySpecificRatePostResourceToModel(resource));
        }
        return result;
    }

    /**
     * Maps RateTableAdditionalGuestFeeModel to AdditionalGuestFeeGetResource.
     */
    public abstract AdditionalGuestFeeGetResource rateTableAdditionalGuestFeeModelToGetResource(RateTableAdditionalGuestFeeModel rateTableAdditionalGuestFeeModel);

    /**
     * Maps AdditionalGuestFeePostResource to RateTableAdditionalGuestFeeModel.
     */
    public abstract RateTableAdditionalGuestFeeModel additionalGuestFeePostResourceToModel(AdditionalGuestFeePostResource additionalGuestFeePostResource);

    /**
     * Maps RateTableDaySpecificRateModel to DaySpecificRateGetResource.
     */
    public abstract DaySpecificRateGetResource rateTableDaySpecificRateModelToGetResource(RateTableDaySpecificRateModel rateTableDaySpecificRateModel);

    /**
     * Maps DaySpecificRatePostResource to RateTableDaySpecificRateModel.
     */
    public abstract RateTableDaySpecificRateModel daySpecificRatePostResourceToModel(DaySpecificRatePostResource daySpecificRatePostResource);
}