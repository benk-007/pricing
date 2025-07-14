package com.smsmode.pricing.security;

import com.smsmode.pricing.enumeration.RoleEnum;
import com.smsmode.pricing.service.JwtTokenProviderService;
import com.smsmode.pricing.util.SecurityUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.smsmode.pricing.util.SecurityUtil.AUTHORITIES_KEY;
import static com.smsmode.pricing.util.SecurityUtil.USERNAME_KEY;


/**
 * Custom JWT authentication filter for processing HTTP requests. Extends {@link
 * OncePerRequestFilter}.
 *
 * <p>This filter retrieves the authentication token from the HTTP request header if any, and sets
 * necessary user information in {@link UserContextHolder} to be used during the request lifecycle.
 *
 * @author hamzahabchi (contact: hamza.habchi@messaging-technologies.com)
 * <p>Created 21 Oct 2024
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProviderService jwtTokenProviderService;

    /**
     * This method filters Http requests. It retrieves the authentication token from the header
     * request, check its integrity and set necessary information in it in {@link UserContextHolder}
     * class to be used during the request lifecycle.
     *
     * @param request  Http request
     * @param response Http response
     * @param filterChain         Chain of a filtered request for a resource
     * @throws ServletException if error occurs
     * @throws IOException      if error occurs
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        log.debug("Processing authentication for request '{}'", request.getRequestURI());

        log.trace("Processing authentication for request '{}'", request.getRequestURI());
        String authToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!ObjectUtils.isEmpty(authToken) && authToken.startsWith(SecurityUtil.TOKEN_TYPE)) {
            try {
                String userId =
                        jwtTokenProviderService.getClaimFromTokenAsString(authToken, SecurityUtil.IDENTIFIER_KEY);
                String username = jwtTokenProviderService.getClaimFromTokenAsString(authToken, USERNAME_KEY);
                UserContextHolder.getContext().setUserId(userId);
                UserContextHolder.getContext().setEmail(username);
                UserContextHolder.getContext()
                        .setFullName(jwtTokenProviderService.getClaimsFromToken(authToken).getSubject());
                String authoritiesToken =
                        String.join(",", jwtTokenProviderService.getClaimFromTokenAsString(authToken, AUTHORITIES_KEY));
                Set<? extends GrantedAuthority> authorities =
                        Arrays.stream(authoritiesToken.split(","))
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toSet());
                UserContextHolder.getContext()
                        .setRoles(
                                authorities.stream()
                                        .map(r -> RoleEnum.valueOf(SecurityUtil.removeRolePrefix(r.getAuthority())))
                                        .collect(Collectors.toSet()));
                log.info(
                        "User is: '{}' with email: '{}' and authorities: '{}'",
                        UserContextHolder.getCurrentUserDisplayName(),
                        username,
                        authoritiesToken);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                log.warn(
                        "An error occurred during token validation to retrieve claims: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Determines whether the request should be filtered.
     *
     * <p>Excludes requests to actuator and OpenApi definition from filtering.
     *
     * @param request the HTTP request to check
     * @return true if the request matches excluded paths, false otherwise
     * @throws ServletException if an error occurs during filtering
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        return antPathMatcher.match("/actuator/**", request.getServletPath())
                || antPathMatcher.match("/v3/**", request.getServletPath());
    }
}
