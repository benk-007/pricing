/**
 * Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this file,
 * via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.exception;

import com.smsmode.pricing.exception.enumeration.TooManyRequestExceptionTitleEnum;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a client has sent too many requests within a given time frame. Extends
 * {@link AbstractBaseException} and is used to indicate rate limiting conditions.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 14 Oct 2024
 */
public class TooManyRequestException extends AbstractBaseException {
    /**
     * Constructs an instance of {@code TooManyRequestException} with the specified title and
     * message.
     *
     * @param title   The title or type of the exception, represented by a {@link
     *                TooManyRequestExceptionTitleEnum}.
     * @param message The detail message (which is saved for later retrieval by the {@link
     *                #getMessage()} method).
     */
    public TooManyRequestException(TooManyRequestExceptionTitleEnum title, String message) {
        super(title, HttpStatus.TOO_MANY_REQUESTS, message);
    }
}
