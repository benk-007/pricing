/**
 * Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this file,
 * via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.exception;

import com.smsmode.pricing.exception.enumeration.BaseExceptionEnum;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown to indicate that the user request conflict with the current state of the
 * resource. Extends {@link AbstractBaseException} and provides a standardized structure for
 * conflict exception
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 13 Mar 2025
 */
public class ConflictException extends AbstractBaseException {
    /**
     * Constructs an instance of {@code AbstractBaseException} with the specified title, status, and
     * message.
     *
     * @param title   The title or type of the exception, represented by a {@link BaseExceptionEnum}.
     * @param message The detail message (which is saved for later retrieval by the {@link
     *                #getMessage()} method).
     */
    public ConflictException(BaseExceptionEnum title, String message) {
        super(title, HttpStatus.CONFLICT, message);
    }
}
