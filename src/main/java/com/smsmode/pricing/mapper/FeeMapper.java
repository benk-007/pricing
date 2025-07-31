package com.smsmode.pricing.mapper;

import com.smsmode.pricing.model.FeeModel;
import com.smsmode.pricing.resource.common.AuditGetResource;
import com.smsmode.pricing.resource.fee.FeeGetResource;
import com.smsmode.pricing.resource.fee.FeePostResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
    @Mapping(target = "units", expression = "java(Set.of(feePostResource.getUnit()))")
    public abstract FeeModel postResourceToModel(FeePostResource feePostResource);

    /**
     * Maps FeeModel to FeeGetResource for response.
     */
    @Mapping(target = "audit", source = ".")
    public abstract FeeGetResource modelToGetResource(FeeModel feeModel);

    /**
     * Maps audit fields into an audit resource.
     */
    public abstract AuditGetResource modelToAuditResource(FeeModel feeModel);
}

