/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.embeddable;

import com.smsmode.pricing.validator.ValidAgeRange;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 15 Jul 2025</p>
 */
@Data
@Embeddable
@ValidAgeRange
public class AgeBucketEmbeddable {
    @PositiveOrZero
    @NotNull
    private Integer fromAge;
    @Positive
    @NotNull
    private Integer toAge;
}
