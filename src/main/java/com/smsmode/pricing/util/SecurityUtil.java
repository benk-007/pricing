/**
 * <p>Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.util;


import java.util.UUID;

/**
 * TODO: add your documentation
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 24 Mar 2025</p>
 */
public class SecurityUtil {

    /**
     * Constant representing the token type used in authentication.
     */
    public static final String TOKEN_TYPE = "Bearer";


    /**
     * Constant representing the role prefix used in security roles.
     */
    public static final String ROLE_PREFIX = "ROLE_";

    /**
     * Constant representing the key for authorities in authentication tokens.
     */
    public static final String AUTHORITIES_KEY = "authorities";

    /**
     * Constant representing the key for username information in authentication tokens.
     */
    public static final String USERNAME_KEY = "username";

    /**
     * Constant representing the key for identifier information in authentication tokens.
     */
    public static final String IDENTIFIER_KEY = "identifier";

    /**
     * Constant representing the system account identifier.
     */
    public static final String SYSTEM_ACCOUNT = "SYSTEM";

    /**
     * Generates a unique key using a UUID without hyphens.
     *
     * @return A unique key as a {@link String}.
     */
    public static String generateKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Removes the role prefix from a given role.
     *
     * @param role The role from which the prefix will be removed.
     * @return The role with the removed prefix.
     */
    public static String removeRolePrefix(String role) {
        return role.replace(ROLE_PREFIX, "");
    }

}
