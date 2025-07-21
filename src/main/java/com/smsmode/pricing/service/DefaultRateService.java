/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.service;

import com.smsmode.pricing.resource.defaultrate.DefaultRateGetResource;
import com.smsmode.pricing.resource.defaultrate.DefaultRatePostResource;
import org.springframework.http.ResponseEntity;



/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
public interface DefaultRateService {

    ResponseEntity<DefaultRateGetResource> getByUnitId(String unitId);

    ResponseEntity<DefaultRateGetResource> create(DefaultRatePostResource defaultRatePostResource);

    ResponseEntity<DefaultRateGetResource> update(String rateId, DefaultRatePostResource defaultRatePostResource);

}
