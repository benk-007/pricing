/**
 * Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this file,
 * via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * This class is an implementation of the {@link HandlerInterceptor} interface that intercepts HTTP
 * requests to manage the {@link UserContextHolder}. It is used to clear the user context after the
 * completion of request processing to ensure that no sensitive user data persists across requests.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 18 Oct 2024
 */
@Component
@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    /**
     * Intercept the execution of a handler and clean the {@link UserContextHolder}
     *
     * @param request      current HTTP request
     * @param response     current HTTP response
     * @param handler      the handler (or {@link org.springframework.web.method.HandlerMethod}) that
     *                     started asynchronous execution, for type and/or instance examination
     * @param modelAndView the ModelAndView that the handler returned (can also be null)
     */
    @Override
    public void postHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler,
            ModelAndView modelAndView) {
        log.debug("Clear the user context");
        UserContextHolder.clear();
    }

    /**
     * Callback after completion of request processing that clears the {@link UserContextHolder}
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  the handler (or {@link org.springframework.web.method.HandlerMethod}) that *
     *                 started asynchronous execution, for type and/or instance examination
     * @param ex       exception if thrown
     */
    @Override
    public void afterCompletion(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler,
            Exception ex) {
        UserContextHolder.clear();
    }
}
