/**
 * Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this file,
 * via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.exception.enumeration;

/**
 * Enum representing titles for resource not found exceptions. Each enum constant should provide a
 * unique code for identifying the exception type.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 09 Oct 2024
 */
public enum ResourceNotFoundExceptionTitleEnum implements BaseExceptionEnum {


    DEFAULT_RATE_NOT_FOUND("PRC_RNF_ERR_1"),
    RATE_PLAN_NOT_FOUND("PRC_RNF_ERR_2"),
    RATE_TABLE_NOT_FOUND("PRC_RNF_ERR_3");





    private final String code;

    /**
     * Constructs a ResourceNotFoundExceptionTitleEnum with the specified code.
     *
     * @param code A string code identifying the exception type.
     */
    ResourceNotFoundExceptionTitleEnum(String code) {
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
