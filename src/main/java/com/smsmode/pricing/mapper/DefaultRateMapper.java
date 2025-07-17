/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.mapper;

import com.smsmode.pricing.model.AdditionalGuestFeeModel;
import com.smsmode.pricing.model.DaySpecificRateModel;
import com.smsmode.pricing.model.DefaultRateModel;
import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeeGetResource;
import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeePostResource;
import com.smsmode.pricing.resource.common.dayspecificrate.DaySpecificRateGetResource;
import com.smsmode.pricing.resource.common.dayspecificrate.DaySpecificRatePostResource;
import com.smsmode.pricing.resource.defaultrate.DefaultRateGetResource;
import com.smsmode.pricing.resource.defaultrate.DefaultRatePostResource;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Mapper for DefaultRate entities and resources
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@Mapper(componentModel = "spring")
public abstract class DefaultRateMapper {

    /**
     * Maps DefaultRatePostResource to DefaultRateModel
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "additionalGuestFees", source = "additionalGuestFees", qualifiedByName = "mapAdditionalGuestFees")
    @Mapping(target = "daySpecificRates", source = "daySpecificRates", qualifiedByName = "mapDaySpecificRates")
    public abstract DefaultRateModel postResourceToModel(DefaultRatePostResource defaultRatePostResource);

    /**
     * Custom mapping method for additional guest fees collection
     */
    @Named("mapAdditionalGuestFees")
    public List<AdditionalGuestFeeModel> mapAdditionalGuestFees(List<AdditionalGuestFeePostResource> additionalGuestFees) {
        if (additionalGuestFees == null) {
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
        if (daySpecificRates == null) {
            return new ArrayList<>();
        }
        List<DaySpecificRateModel> result = new ArrayList<>();
        for (DaySpecificRatePostResource resource : daySpecificRates) {
            result.add(daySpecificRatePostResourceToModel(resource));
        }
        return result;
    }

    /**
     * Maps DefaultRateModel to DefaultRateGetResource including collections
     */
    public abstract DefaultRateGetResource modelToGetResource(DefaultRateModel defaultRateModel);

    /**
     * Updates existing DefaultRateModel from DefaultRatePostResource
     * Handles cascade updates for collections
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "additionalGuestFees", ignore = true)
    @Mapping(target = "daySpecificRates", ignore = true)
    public abstract void updateModelFromPostResource(DefaultRatePostResource defaultRatePostResource, @MappingTarget DefaultRateModel defaultRateModel);

    /**
     * Maps AdditionalGuestFeeModel to AdditionalGuestFeeGetResource
     */
    public abstract AdditionalGuestFeeGetResource additionalGuestFeeModelToGetResource(AdditionalGuestFeeModel additionalGuestFeeModel);

    /**
     * Maps AdditionalGuestFeePostResource to AdditionalGuestFeeModel
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    public abstract AdditionalGuestFeeModel additionalGuestFeePostResourceToModel(AdditionalGuestFeePostResource additionalGuestFeePostResource);

    /**
     * Maps DaySpecificRateModel to DaySpecificRateGetResource
     */
    public abstract DaySpecificRateGetResource daySpecificRateModelToGetResource(DaySpecificRateModel daySpecificRateModel);

    /**
     * Maps DaySpecificRatePostResource to DaySpecificRateModel
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    public abstract DaySpecificRateModel daySpecificRatePostResourceToModel(DaySpecificRatePostResource daySpecificRatePostResource);

    /**
     * Updates existing AdditionalGuestFeeModel from AdditionalGuestFeePostResource
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    public abstract void updateAdditionalGuestFeeFromResource(AdditionalGuestFeePostResource source, @MappingTarget AdditionalGuestFeeModel target);

    /**
     * Updates existing DaySpecificRateModel from DaySpecificRatePostResource
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    public abstract void updateDaySpecificRateFromResource(DaySpecificRatePostResource source, @MappingTarget DaySpecificRateModel target);
}