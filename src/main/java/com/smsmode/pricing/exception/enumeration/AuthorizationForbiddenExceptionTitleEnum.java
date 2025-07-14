/**
 * Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this file,
 * via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.exception.enumeration;

/**
 * Enum representing titles for authorization forbidden exceptions. Each enum constant should
 * provide a unique code for identifying the exception type.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 09 Oct 2024
 */
public enum AuthorizationForbiddenExceptionTitleEnum implements BaseExceptionEnum {
    /**
     * Activation key expired exception title.
     */
    ACTIVATION_KEY_EXPIRED("UAA_AUTH_FORB_ERR_1"),

    /**
     * Forbidden exception title.
     */
    FORBIDDEN("UAA_AUTH_FORB_ERR_2"),

    /**
     * Token not valid exception title.
     */
    TOKEN_NOT_VALID("UAA_AUTH_FORB_ERR_3"),

    /**
     * User not activated exception title.
     */
    USER_NOT_ACTIVATED("UAA_AUTH_FORB_ERR_4"),

    /**
     * Reset key expired exception title.
     */
    RESET_KEY_EXPIRED("UAA_AUTH_FORB_ERR_5"),

    USER_DISABLED("UAA_AUTH_FORB_ERR_6");

    private final String code;

    /**
     * Constructs an AuthorizationForbiddenExceptionTitleEnum with the specified code.
     *
     * @param code A string code identifying the exception type.
     */
    AuthorizationForbiddenExceptionTitleEnum(String code) {
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
