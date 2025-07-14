/**
 * Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this file,
 * via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.exception;

import com.smsmode.pricing.exception.enumeration.ResourceNotFoundExceptionTitleEnum;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown to indicate that a resource is not found. Extends {@link
 * AbstractBaseException} and provides a standardized structure for resource not found
 * exceptions.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 09 Oct 2024
 */
public class ResourceNotFoundException extends AbstractBaseException {
    /**
     * Constructs an instance of {@code ResourceNotFoundException} with the specified title and
     * message.
     *
     * @param title   The title or type of the exception, represented by a {@link
     *                ResourceNotFoundExceptionTitleEnum}.
     * @param message The detail message (which is saved for later retrieval by the {@link
     *                #getMessage()} method).
     */
    public ResourceNotFoundException(ResourceNotFoundExceptionTitleEnum title, String message) {
        super(title, HttpStatus.NOT_FOUND, message);
    }
}
