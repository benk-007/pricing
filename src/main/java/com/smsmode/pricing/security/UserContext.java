/**
 * Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this file,
 * via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.security;

import com.smsmode.pricing.enumeration.RoleEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Set;

/**
 * Represents the context of the current user within the application.
 *
 * <p>It encapsulates information about the current user, such as user ID, name, email, and roles.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 17 Oct 2024
 */
@Getter
@Setter
public class UserContext {

    /**
     * User's identifier.
     */
    private String userId;

    /**
     * User's name.
     */
    private String fullName;

    /**
     * User's email.
     */
    private String email;

    /**
     * User's roles.
     */
    private Set<RoleEnum> roles = Collections.emptySet(); // Initialize to an empty set
}
