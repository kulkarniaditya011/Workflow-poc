package com.example.backend.utilService;

import com.example.backend.exceptions.RestApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility class for extracting tenant and user information from the security context.
 *
 * This is the single source of truth for tenant resolution in the application.
 * TenantId is extracted from the JWT token (via SecurityUser) and NOT from request body or ThreadLocal.
 *
 * This ensures:
 * - Request-scoped tenant isolation
 * - Thread-safe operation in reactive environments
 * - Immutable tenant per request
 */
@Slf4j
@Component
public class SecurityUtils {

    /**
     * Retrieves the tenant ID from the authenticated user's security context.
     *
     * The tenant ID comes from the JWT token, which was set during authentication in JwtAuthenticationFilter.
     *
     * @return the tenant ID of the current authenticated user
     * @throws RestApiException if user is not authenticated or tenant ID is missing
     */
    public static String getTenantId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Attempt to get tenant ID for unauthenticated request");
            throw new RestApiException(
                "User is not authenticated",
                HttpStatus.UNAUTHORIZED
            );
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof SecurityUser)) {
            log.warn("Principal is not a SecurityUser: {}", principal.getClass().getName());
            throw new RestApiException(
                "Invalid authentication principal",
                HttpStatus.UNAUTHORIZED
            );
        }

        SecurityUser securityUser = (SecurityUser) principal;
        String tenantId = securityUser.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            log.warn("Tenant ID is missing from security context for user: {}", securityUser.getUsername());
            throw new RestApiException(
                "Tenant ID is missing from security context",
                HttpStatus.UNAUTHORIZED
            );
        }

        return tenantId;
    }

    /**
     * Retrieves the username (email) of the authenticated user.
     *
     * @return the email of the current authenticated user
     * @throws RestApiException if user is not authenticated
     */
    public static String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RestApiException(
                "User is not authenticated",
                HttpStatus.UNAUTHORIZED
            );
        }

        return authentication.getName();
    }

    /**
     * Retrieves the SecurityUser object from the security context.
     *
     * @return the SecurityUser principal
     * @throws RestApiException if user is not authenticated or principal is invalid
     */
    public static SecurityUser getSecurityUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RestApiException(
                "User is not authenticated",
                HttpStatus.UNAUTHORIZED
            );
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof SecurityUser)) {
            throw new RestApiException(
                "Invalid authentication principal",
                HttpStatus.UNAUTHORIZED
            );
        }

        return (SecurityUser) principal;
    }
}
