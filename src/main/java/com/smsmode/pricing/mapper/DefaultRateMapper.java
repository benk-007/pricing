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
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Mapper for DefaultRate entities and resources
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@Slf4j
@Mapper(
        componentModel = "spring",
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public abstract class DefaultRateMapper {

    /**
     * Maps DefaultRatePostResource to DefaultRateModel
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unit", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "additionalGuestFees", source = "additionalGuestFees", qualifiedByName = "mapAdditionalGuestFees")
    @Mapping(target = "daySpecificRates", source = "daySpecificRates", qualifiedByName = "mapDaySpecificRates")
    public abstract DefaultRateModel postResourceToModel(DefaultRatePostResource defaultRatePostResource);

    /**
     * Maps DefaultRateModel to DefaultRateGetResource including collections
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "unit", source = "unit")
    @Mapping(target = "additionalGuestFees", source = "additionalGuestFees")
    @Mapping(target = "daySpecificRates", source = "daySpecificRates")
    public abstract DefaultRateGetResource modelToGetResource(DefaultRateModel defaultRateModel);

    /**
     * Updates existing DefaultRateModel from DefaultRatePostResource
     * Handles cascade updates for collections
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unit", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "additionalGuestFees", source = "additionalGuestFees", qualifiedByName = "mapAdditionalGuestFees")
    @Mapping(target = "daySpecificRates", source = "daySpecificRates", qualifiedByName = "mapDaySpecificRates")
    public abstract void updateModelFromPostResource(DefaultRatePostResource defaultRatePostResource, @MappingTarget DefaultRateModel defaultRateModel);

    /**
     * Maps AdditionalGuestFeeModel to AdditionalGuestFeeGetResource
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "guestCount", source = "guestCount")
    @Mapping(target = "guestType", source = "guestType")
    @Mapping(target = "ageBucket", source = "ageBucket")
    @Mapping(target = "amountType", source = "amountType")
    @Mapping(target = "value", source = "value")
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
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nightly", source = "nightly")
    @Mapping(target = "days", source = "days")
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
     * Custom mapping method for additional guest fees collection
     */
    @Named("mapAdditionalGuestFees")
    protected Set<AdditionalGuestFeeModel> mapAdditionalGuestFees(Set<AdditionalGuestFeePostResource> additionalGuestFees) {
        if (additionalGuestFees == null) {
            return new HashSet<>();
        }
        Set<AdditionalGuestFeeModel> result = new HashSet<>();
        for (AdditionalGuestFeePostResource resource : additionalGuestFees) {
            result.add(additionalGuestFeePostResourceToModel(resource));
        }
        return result;
    }

    /**
     * Custom mapping method for day specific rates collection
     */
    @Named("mapDaySpecificRates")
    protected Set<DaySpecificRateModel> mapDaySpecificRates(Set<DaySpecificRatePostResource> daySpecificRates) {
        if (daySpecificRates == null) {
            return new HashSet<>();
        }
        Set<DaySpecificRateModel> result = new HashSet<>();
        for (DaySpecificRatePostResource resource : daySpecificRates) {
            result.add(daySpecificRatePostResourceToModel(resource));
        }
        return result;
    }
}