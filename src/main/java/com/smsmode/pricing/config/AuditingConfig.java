/**
 * Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this file,
 * via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.config;

import com.smsmode.pricing.security.UserContextHolder;
import com.smsmode.pricing.util.SecurityUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Configuration class for enabling JPA auditing in the application. This class is annotated with
 * {@link Configuration} and {@link EnableJpaAuditing}, allowing the automatic population of
 * audit-related fields in JPA entities.
 *
 * <p>This configuration uses a custom implementation of {@link AuditorAware} to define the current
 * auditor of the application. The auditor is determined based on the currently authenticated user
 * or a system account if no user is authenticated.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 18 Oct 2024
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditingConfig {

    /**
     * Provides an {@link AuditorAware} bean in the Spring context.
     *
     * @return An instance of the {@link AuditorAwareImpl} class for determining the current auditor
     * during JPA auditing.
     */
    @Bean
    AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    /**
     * Class to define the actual auditor implementation for JPA auditing. It implements the {@link
     * AuditorAware} interface with the type parameter set to {@code String} for the auditor type.
     */
    private static class AuditorAwareImpl implements AuditorAware<String> {

        /**
         * Returns the current auditor of the application.
         *
         * @return An {@link Optional} containing the current auditor's identifier. If an authenticated
         * user is present, their user ID is returned. Otherwise, the system account identifier is
         * used.
         */
        @NonNull
        @Override
        public Optional<String> getCurrentAuditor() {
            if (StringUtils.hasText(UserContextHolder.getCurrentUser())) {
                return Optional.of(UserContextHolder.getCurrentUser().concat("-").concat(UserContextHolder.getCurrentUserDisplayName()));
            }
            return Optional.of(SecurityUtil.SYSTEM_ACCOUNT);
        }
    }
}
