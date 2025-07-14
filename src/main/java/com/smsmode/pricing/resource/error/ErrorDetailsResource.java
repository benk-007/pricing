package com.smsmode.pricing.resource.error;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the structure of error details returned in the application's API responses.
 * It encapsulates information about an error including its title, status, detailed message,
 * timestamp, developer message, error code, and a map of field-specific validation errors.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 * <p>Created 09 Oct 2024
 */
@Data
public class ErrorDetailsResource {
    private String title;
    private int status;
    private String detail;
    private long timestamp;
    private String developerMessage;
    private String code;
    private Map<String, List<ValidationErrorResource>> errors =
            new HashMap<String, List<ValidationErrorResource>>();
}
