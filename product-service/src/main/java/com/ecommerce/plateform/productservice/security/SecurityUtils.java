package com.ecommerce.plateform.productservice.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long getCurrentUserId() {
        return getPrincipal().getUserId();
    }

    public static String getCurrentRole() {
        return getPrincipal().getRole();
    }

    private static AuthenticatedUser getPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof AuthenticatedUser principal)) {
            throw new AccessDeniedException("No authenticated user in security context");
        }
        return principal;
    }
}
