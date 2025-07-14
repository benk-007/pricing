/**
 * Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this file,
 * via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.exception;

import com.smsmode.pricing.exception.enumeration.BaseExceptionEnum;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * An abstract base exception class that extends {@link RuntimeException}. Provides a standardized
 * structure for custom exceptions in the application.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 09 Oct 2024
 */
@Getter
public abstract class AbstractBaseException extends RuntimeException {

    /**
     * HTTP status associated with the exception.
     */
    private final HttpStatus status;

    /**
     * Enumeration representing the title or type of the exception.
     */
    private final BaseExceptionEnum title;

    /**
     * Constructs an instance of {@code AbstractBaseException} with the specified title, status, and
     * message.
     *
     * @param title   The title or type of the exception, represented by a {@link BaseExceptionEnum}.
     * @param status  The HTTP status associated with the exception.
     * @param message The detail message (which is saved for later retrieval by the {@link
     *                #getMessage()} method).
     */
    public AbstractBaseException(BaseExceptionEnum title, HttpStatus status, String message) {
        super(message);
        this.title = title;
        this.status = status;
    }
}
