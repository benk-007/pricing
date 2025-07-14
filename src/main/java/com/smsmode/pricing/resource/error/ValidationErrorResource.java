package com.smsmode.pricing.resource.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a validation error with a specific code and message that describes the validation
 * issue.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 * <p>Created 09 Oct 2024
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResource {
    private String code;
    private String message;
}
