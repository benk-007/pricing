/**
 * Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this file,
 * via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.exception.enumeration;

/**
 * Interface representing the base contract for exception enums. Implementing enums should provide a
 * unique code for identifying the exception type.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 09 Oct 2024
 */
public interface BaseExceptionEnum {
    /**
     * Gets the unique code representing the exception type.
     *
     * @return A string code identifying the exception type.
     */
    String getCode();
}
