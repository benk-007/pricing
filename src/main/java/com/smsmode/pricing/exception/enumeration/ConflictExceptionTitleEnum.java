/**
 * Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this file,
 * via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.exception.enumeration;


/**
 * Enum representing titles for conflict exceptions. Each enum constant should provide a unique code
 * for identifying the exception type.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 13 Mar 2025
 */
public enum ConflictExceptionTitleEnum implements BaseExceptionEnum {
    PASSWORD_MISMATCH("UAA_CFT_ERR_1"),
    OVERLAPPING_RATE_TABLE_DATES("PRC_CFT_ERR_2"),
    SEGMENT_ALREADY_EXISTS("PRC_CFT_ERR_3");
    private final String code;

    /**
     * Constructs an AuthenticationUnauthorizedExceptionTitleEnum with the specified code.
     *
     * @param code A string code identifying the exception type.
     */
    ConflictExceptionTitleEnum(String code) {
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
