/**
 * Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this file,
 * via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.controller.impl;

import com.smsmode.pricing.controller.ErrorController;
import com.smsmode.pricing.exception.*;
import com.smsmode.pricing.exception.enumeration.MethodArgumentsExceptionTitleEnum;
import com.smsmode.pricing.resource.error.ErrorDetailsResource;
import com.smsmode.pricing.resource.error.ValidationErrorResource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller advice class for handling exceptions thrown within the application. This class is
 * annotated with {@link ControllerAdvice} and implements the {@link ErrorController} interface,
 * providing centralized exception handling.
 *
 * <p>This class defines methods to handle specific exceptions and transform them into a
 * standardized {@link ErrorDetailsResource} format for a consistent error response.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 09 Oct 2024
 */
@ControllerAdvice
public class ErrorControllerImpl implements ErrorController {

    /**
     * {@inheritDoc}
     */
    @Override
    @ExceptionHandler({
            AuthenticationUnauthorizedException.class,
            AuthorizationForbiddenException.class,
            TooManyRequestException.class,
            ResourceNotFoundException.class,
            ConflictException.class
    })
    public ResponseEntity<ErrorDetailsResource> handleGenericExceptions(
            AbstractBaseException e, HttpServletRequest request) {
        ErrorDetailsResource errorDetailResource = new ErrorDetailsResource();
        errorDetailResource.setTimestamp(Instant.now().toEpochMilli());
        errorDetailResource.setTitle(e.getTitle().toString());
        errorDetailResource.setCode(e.getTitle().getCode());
        errorDetailResource.setDeveloperMessage(e.getClass().getName());
        errorDetailResource.setStatus(e.getStatus().value());
        errorDetailResource.setDetail(e.getMessage());
        return new ResponseEntity<>(errorDetailResource, e.getStatus());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetailsResource> handleValidationError(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        ErrorDetailsResource errorDetailResource = new ErrorDetailsResource();
        errorDetailResource.setTimestamp(Instant.now().toEpochMilli());
        errorDetailResource.setTitle(
                MethodArgumentsExceptionTitleEnum.METHOD_ARGUMENTS_NOT_VALID.toString());
        errorDetailResource.setCode(
                MethodArgumentsExceptionTitleEnum.METHOD_ARGUMENTS_NOT_VALID.getCode());
        errorDetailResource.setDeveloperMessage(e.getClass().getName());
        errorDetailResource.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDetailResource.setDetail("Input validation failed");
        // Create ValidationError instances
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        for (FieldError fe : fieldErrors) {
            List<ValidationErrorResource> validationErrorList =
                    errorDetailResource.getErrors().computeIfAbsent(fe.getField(), k -> new ArrayList<>());
            ValidationErrorResource validationError = new ValidationErrorResource();
            validationError.setCode(fe.getCode());
            validationError.setMessage(fe.getDefaultMessage());
            validationErrorList.add(validationError);
        }
        return new ResponseEntity<>(errorDetailResource, HttpStatus.BAD_REQUEST);
    }
}
