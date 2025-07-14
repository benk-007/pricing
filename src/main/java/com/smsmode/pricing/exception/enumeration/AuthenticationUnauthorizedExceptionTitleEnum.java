/**
 * Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this file,
 * via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.exception.enumeration;


/**
 * Enum representing titles for authentication unauthorized exceptions. Each enum constant should
 * provide a unique code for identifying the exception type.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 09 Oct 2024
 */
public enum AuthenticationUnauthorizedExceptionTitleEnum implements BaseExceptionEnum {
    /**
     * Not authorized exception title.
     */
    NOT_AUTHORIZED("UAA_AUTH_UNAU_ERR_1"),

    EXPIRED_TOKEN("UAA_AUTH_UNAU_ERR_2");

    private final String code;

    /**
     * Constructs an AuthenticationUnauthorizedExceptionTitleEnum with the specified code.
     *
     * @param code A string code identifying the exception type.
     */
    AuthenticationUnauthorizedExceptionTitleEnum(String code) {
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
