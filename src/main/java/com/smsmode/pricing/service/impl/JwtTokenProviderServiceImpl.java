/**
 * Copyright (C) Calade Technologies, Inc - All Rights Reserved Unauthorized copying of this file,
 * via any medium is strictly prohibited Proprietary and confidential
 */
package com.smsmode.pricing.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.smsmode.pricing.service.JwtTokenProviderService;
import com.smsmode.pricing.util.SecurityUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Implementation of the {@link JwtTokenProviderService} interface, providing functionality for
 * handling JWT (JSON Web Token) authentication tokens.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 09 Oct 2024
 */
@Log4j2
@Service
public class JwtTokenProviderServiceImpl implements JwtTokenProviderService {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClaimFromTokenAsString(String token, String claimName) {
        if (this.getClaimsFromToken(token).getClaim(claimName).isMissing()) {
            return null;
        } else {
            return this.getClaimsFromToken(token).getClaim(claimName).asString();
        }
    }

    @Override
    public Integer getClaimFromTokenAsInteger(String token, String claimName) {
        if (this.getClaimsFromToken(token).getClaim(claimName).isMissing()) {
            return null;
        } else {
            return this.getClaimsFromToken(token).getClaim(claimName).asInt();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DecodedJWT getClaimsFromToken(String token) {
        token = token.replace(SecurityUtil.TOKEN_TYPE, "").trim();
        return JWT.decode(token);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTokenExpired(String token) {
        return this.getClaimsFromToken(token).getExpiresAt().before(new Date());
    }
}
