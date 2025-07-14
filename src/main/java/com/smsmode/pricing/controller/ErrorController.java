/**
 * Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this file,
 * via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.controller;

import com.smsmode.pricing.exception.AbstractBaseException;
import com.smsmode.pricing.resource.error.ErrorDetailsResource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * Interface defining methods for handling various exceptions within the application.
 * Implementations of this interface are expected to provide centralized exception handling and
 * transform exceptions into standardized {@link ErrorDetailsResource} format for consistent error
 * responses.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 09 Oct 2024
 */
public interface ErrorController {

    /**
     * Handles exceptions of types {@link AbstractBaseException}.
     *
     * @param e       The exception to be handled.
     * @param request The HTTP request where the exception occurred.
     * @return A {@link ResponseEntity} containing an {@link ErrorDetailsResource} with details about
     * the error.
     */
    ResponseEntity<ErrorDetailsResource> handleGenericExceptions(
            AbstractBaseException e, HttpServletRequest request);

    /**
     * Handles exceptions of type {@link MethodArgumentNotValidException}.
     *
     * @param e       The exception to be handled.
     * @param request The HTTP request where the exception occurred.
     * @return A {@link ResponseEntity} containing an {@link ErrorDetailsResource} with details about
     * the validation error.
     */
    ResponseEntity<ErrorDetailsResource> handleValidationError(
            MethodArgumentNotValidException e, HttpServletRequest request);
}
