/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.mapper;

import com.smsmode.pricing.model.DefaultRateModel;
import com.smsmode.pricing.resource.defaultrate.DefaultRateGetResource;
import com.smsmode.pricing.resource.defaultrate.DefaultRatePostResource;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * TODO: add your documentation
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

    public abstract DefaultRateModel postResourceToModel(DefaultRatePostResource defaultRatePostResource);

    public abstract DefaultRateGetResource modelToGetResource(DefaultRateModel defaultRateModel);
}
