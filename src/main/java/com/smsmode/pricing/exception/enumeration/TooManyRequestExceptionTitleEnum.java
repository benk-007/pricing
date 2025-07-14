/**
 * Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this file,
 * via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.exception.enumeration;

/**
 * Enum representing exception titles related to "Too Many Requests" errors. Implements the {@link
 * BaseExceptionEnum} interface for standardized exception handling.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 14 Oct 2024
 */
public enum TooManyRequestExceptionTitleEnum implements BaseExceptionEnum {
    MAX_LOGIN_ATTEMPT("UAA_TMR_ERR_1");

    private final String code;

    /**
     * Constructs a TooManyRequestExceptionTitleEnum with the specified code.
     *
     * @param code A string code identifying the exception type.
     */
    TooManyRequestExceptionTitleEnum(String code) {
        this.code = code;
    }

    /**
     * {@inheritDoc}
     *
     * @return A string code identifying the exception type.
     */
    @Override
    public String getCode() {
        return code;
    }
}
