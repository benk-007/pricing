/**
 * Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this file,
 * via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.exception.enumeration;

/**
 * Enum representing titles for method arguments exception. Each enum constant should provide a
 * unique code for identifying the exception type.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 09 Oct 2024
 */
public enum MethodArgumentsExceptionTitleEnum implements BaseExceptionEnum {
    /**
     * Method arguments not valid exception title.
     */
    METHOD_ARGUMENTS_NOT_VALID("UAA_BAD_REQ_ERR");

    private final String code;

    /**
     * Constructs a MethodArgumentsExceptionTitleEnum with the specified code.
     *
     * @param code A string code identifying the exception type.
     */
    MethodArgumentsExceptionTitleEnum(String code) {
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
