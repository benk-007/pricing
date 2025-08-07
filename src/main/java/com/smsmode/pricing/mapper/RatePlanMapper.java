package com.smsmode.pricing.mapper;

import com.smsmode.pricing.model.RatePlanModel;
import com.smsmode.pricing.resource.common.AuditGetResource;
import com.smsmode.pricing.resource.rateplan.RatePlanGetResource;
import com.smsmode.pricing.resource.rateplan.RatePlanPatchResource;
import com.smsmode.pricing.resource.rateplan.RatePlanPostResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for RatePlan entities and resources.
 */
@Mapper(componentModel = "spring")
public abstract class RatePlanMapper {

    /**
     * Maps RatePlanPostResource to RatePlanModel for creation.
     */
    @Mapping(source = "unitId", target = "unit.id")
    public abstract RatePlanModel postResourceToModel(RatePlanPostResource ratePlanPostResource);

    /**
     * Maps RatePlanModel to RatePlanGetResource for response.
     */
    @Mapping(target = "audit", source = ".")
    public abstract RatePlanGetResource modelToGetResource(RatePlanModel ratePlanModel);


    public abstract AuditGetResource modelToAuditResource(RatePlanModel ratePlanModel);

    /**
     * Updates existing RatePlanModel from RatePlanPostResource for update operations.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "unit", ignore = true)
    public abstract void updateModelFromPatchResource(RatePlanPatchResource ratePlanPatchResource, @MappingTarget RatePlanModel ratePlanModel);
}