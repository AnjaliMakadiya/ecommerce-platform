package com.ecommerce.plateform.orderservice.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

//public class SecurityUtils {

//    public static Long getCurrentUserId() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("Authentication: " + auth);
//        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
//        System.out.println("Current User ID: " + principal.getUserId());
//        return principal.getUserId();
//    }
//
//    public static String getCurrentRole() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
//        return principal.getAuthorities().stream()
//                .map(authority -> authority.getAuthority().substring(5)) // Remove "ROLE_" prefix
//                .findFirst()
//                .orElse(null);
//    }
//
//
//    public static Long getCurrentUserId() {
//        return getPrincipal().getUserId();
//    }
//
//    public static String getCurrentRole() {
//        return getPrincipal().getAuthorities().stream()
//                .findFirst()
//                .map(GrantedAuthority::getAuthority)
//                .orElseThrow(() -> new IllegalStateException("No role found for authenticated user"));
//    }
//
//    private static CustomUserDetails getPrincipal() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
//            throw new AccessDeniedException("No authenticated user in security context");
//        }
//        if (!(auth.getPrincipal() instanceof CustomUserDetails principal)) {
//            throw new IllegalStateException("Unexpected principal type: " + auth.getPrincipal().getClass());
//        }
//        return principal;
//    }
//}


public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Long getCurrentUserId() {

        CustomUserDetails principal = getPrincipal();

        Long userId = principal.getUserId();

        if (userId == null) {
            throw new IllegalStateException(
                    "Authenticated user does not contain userId"
            );
        }

        return userId;
    }

    public static String getCurrentRole() {

        return getPrincipal()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No role found for authenticated user"
                        )
                );
    }

    private static CustomUserDetails getPrincipal() {

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (auth == null ||
                !auth.isAuthenticated() ||
                auth.getPrincipal() == null) {

            throw new AccessDeniedException(
                    "No authenticated user in security context"
            );
        }

        Object principal = auth.getPrincipal();

        if (!(principal instanceof CustomUserDetails)) {

            throw new IllegalStateException(
                    "Unexpected principal type: "
                            + principal.getClass().getName()
            );
        }

        return (CustomUserDetails) principal;
    }
}