package com.smsmode.pricing.mapper;

import com.smsmode.pricing.model.AdditionalGuestFeeModel;
import com.smsmode.pricing.model.FeeModel;
import com.smsmode.pricing.resource.common.AuditGetResource;
import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeeGetResource;
import com.smsmode.pricing.resource.common.additionalguestfee.AdditionalGuestFeePostResource;
import com.smsmode.pricing.resource.fee.FeeGetResource;
import com.smsmode.pricing.resource.fee.FeePatchResource;
import com.smsmode.pricing.resource.fee.FeePostResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for Fee entities and resources.
 */
@Mapper(componentModel = "spring")
public abstract class FeeMapper {

    /**
     * Maps FeePostResource to FeeModel for creation.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "active", source = "active")
    @Mapping(target = "unit", source = "unit")
    @Mapping(target = "additionalGuestPrices", source = "additionalGuestPrices", qualifiedByName = "mapAdditionalGuestPrices")
    public abstract FeeModel postResourceToModel(FeePostResource feePostResource);

    /**
     * Maps FeeModel to FeeGetResource for response.
     */
    @Mapping(target = "audit", source = ".")
    @Mapping(target = "additionalGuestPrices", source = "additionalGuestPrices")
    public abstract FeeGetResource modelToGetResource(FeeModel feeModel);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "unit",ignore = true)
    @Mapping(target = "additionalGuestPrices", ignore = true)
    public abstract void updateModelFromPatchResource(FeePatchResource patchResource, @MappingTarget FeeModel feeModel);

    /**
     * Maps audit fields into an audit resource.
     */
    public abstract AuditGetResource modelToAuditResource(FeeModel feeModel);

    /**
     * Custom mapping method for additional guest prices collection
     */
    @Named("mapAdditionalGuestPrices")
    public List<AdditionalGuestFeeModel> mapAdditionalGuestPrices(List<AdditionalGuestFeePostResource> additionalGuestPrices) {
        if (CollectionUtils.isEmpty(additionalGuestPrices)) {
            return new ArrayList<>();
        }
        List<AdditionalGuestFeeModel> result = new ArrayList<>();
        for (AdditionalGuestFeePostResource resource : additionalGuestPrices) {
            result.add(additionalGuestFeePostResourceToModel(resource));
        }
        return result;
    }

    /**
     * Maps AdditionalGuestFeeModel to AdditionalGuestFeeGetResource
     */
    public abstract AdditionalGuestFeeGetResource additionalGuestFeeModelToGetResource(AdditionalGuestFeeModel additionalGuestFeeModel);

    /**
     * Maps AdditionalGuestFeePostResource to AdditionalGuestFeeModel
     */
    public abstract AdditionalGuestFeeModel additionalGuestFeePostResourceToModel(AdditionalGuestFeePostResource additionalGuestFeePostResource);

    /**
     * Updates existing AdditionalGuestFeeModel from AdditionalGuestFeePostResource
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rate", ignore = true)
    @Mapping(target = "fee", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    public abstract void updateAdditionalGuestFeeFromResource(AdditionalGuestFeePostResource source, @MappingTarget AdditionalGuestFeeModel target);
}

