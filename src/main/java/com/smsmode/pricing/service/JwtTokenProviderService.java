/**
 * Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this file,
 * via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.service;

import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * Service interface for generating and handling JWT (JSON Web Token) authentication tokens.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 09 Oct 2024
 */
public interface JwtTokenProviderService {

    /**
     * Retrieve any claim from the JWT token.
     *
     * @param token          User's JWT token.
     * @param claimsResolver Function from Claims class.
     * @param <T>            Generic type of the claim.
     * @return The value of the claim.
     */
    String getClaimFromTokenAsString(String token, String claimName);

    Integer getClaimFromTokenAsInteger(String token, String claimName);

    /**
     * Retrieve all claims from the JWT token.
     *
     * @param token User's JWT token.
     * @return All claims from the token.
     */
    DecodedJWT getClaimsFromToken(String token);

    /**
     * Check if the JWT token has expired.
     *
     * @param token User's JWT token.
     * @return {@code true} if the token is expired, {@code false} otherwise.
     */
    boolean isTokenExpired(String token);
}
