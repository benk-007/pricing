package com.smsmode.pricing.mapper;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for RateTable entities and resources.
 */
@Mapper(componentModel = "spring")
public abstract class RateTableMapper {

    /**
     * Maps RateTablePostResource to RateTableModel for creation.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "additionalGuestFees", source = "additionalGuestFees", qualifiedByName = "mapAdditionalGuestFees")
    @Mapping(target = "daySpecificRates", source = "daySpecificRates", qualifiedByName = "mapDaySpecificRates")
    public abstract RateTableModel postResourceToModel(RateTablePostResource rateTablePostResource);

    /**
     * Maps RateTableModel to RateTableGetResource for response.
     */
    public abstract RateTableGetResource modelToGetResource(RateTableModel rateTableModel);

    /**
     * Updates existing RateTableModel from RateTablePatchResource for update operations.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "additionalGuestFees", ignore = true)
    @Mapping(target = "daySpecificRates", ignore = true)
    public abstract void updateModelFromPatchResource(RateTablePatchResource rateTablePatchResource, @MappingTarget RateTableModel rateTableModel);

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
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rateTable", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    public abstract RateTableAdditionalGuestFeeModel additionalGuestFeePostResourceToModel(AdditionalGuestFeePostResource additionalGuestFeePostResource);

    /**
     * Maps RateTableDaySpecificRateModel to DaySpecificRateGetResource.
     */
    public abstract DaySpecificRateGetResource rateTableDaySpecificRateModelToGetResource(RateTableDaySpecificRateModel rateTableDaySpecificRateModel);

    /**
     * Maps DaySpecificRatePostResource to RateTableDaySpecificRateModel.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rateTable", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    public abstract RateTableDaySpecificRateModel daySpecificRatePostResourceToModel(DaySpecificRatePostResource daySpecificRatePostResource);
}