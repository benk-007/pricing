/**
 * Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this file,
 * via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.security;

import com.smsmode.pricing.enumeration.RoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.Set;

/**
 * Utility class for managing the context of the current user within the application.
 *
 * <p>It provides methods to retrieve and manipulate the {@link UserContext} associated with the
 * current thread. The {@link UserContext} holds information such as user ID, name, email, and
 * roles.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 17 Oct 2024
 */
@Slf4j
public class UserContextHolder {

    /**
     * The ThreadLocal variable holding the {@link UserContext} for the current thread.
     */
    private static final ThreadLocal<UserContext> currentUser = new ThreadLocal<>();

    private UserContextHolder() {
    }

    /**
     * Retrieve the {@link UserContext} instance for the current thread. If it doesn't exist, create
     * an empty one.
     *
     * @return An instance of {@link UserContext}.
     */
    public static UserContext getContext() {
        UserContext context = currentUser.get();

        if (context == null) {
            context = createEmptyContext();
            currentUser.set(context);
        }
        return currentUser.get();
    }

    /**
     * Set the {@link UserContext} for the current thread.
     *
     * @param context An instance of {@link UserContext}.
     */
    public static void setContext(UserContext context) {
        Assert.notNull(context, "Only non-null UserContext instances are permitted");
        currentUser.set(context);
    }

    /**
     * Retrieve the user ID of the current user.
     *
     * @return The user's ID.
     */
    public static String getCurrentUser() {
        return getContext().getUserId();
    }

    /**
     * Retrieve the display name (firstname and lastname) of the current user.
     *
     * @return The user's display name.
     */
    public static String getCurrentUserDisplayName() {
        return getContext().getFullName();
    }

    /**
     * Retrieve the username of the current user.
     *
     * @return The user's username.
     */
    public static String getCurrentUserEmail() {
        return getContext().getEmail();
    }

    /**
     * Retrieve the roles of the current user.
     *
     * @return The user's roles.
     */
    public static Set<RoleEnum> getCurrentUserRoles() {
        return getContext().getRoles();
    }

    /**
     * Create an empty instance of {@link UserContext}.
     *
     * @return A fresh instance of {@link UserContext}.
     */
    public static UserContext createEmptyContext() {
        return new UserContext();
    }

    /**
     * Remove all data from the thread holding the {@link UserContext}.
     */
    public static void clear() {
        currentUser.remove();
    }
}
