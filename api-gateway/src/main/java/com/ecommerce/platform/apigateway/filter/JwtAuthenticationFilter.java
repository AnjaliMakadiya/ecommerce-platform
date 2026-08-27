package com.ecommerce.platform.apigateway.filter;

import com.ecommerce.platform.apigateway.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//
//
//    @Override
//    protected void doFilterInternal(
//            @NonNull HttpServletRequest request,
//            @NonNull HttpServletResponse response,
//            @NonNull FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//
//            String jwt = authHeader.substring(7);
//
//            String username = jwtService.extractUsername(jwt);
//            String role = jwtService.extractRole(jwt);
//
//            List<SimpleGrantedAuthority> authorities =
//                    List.of(new SimpleGrantedAuthority("ROLE_" + role));
//
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(
//                            username,
//                            null,
//                            authorities
//                    );
//
//            authentication.setDetails(
//                    new WebAuthenticationDetailsSource().buildDetails(request));
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//int count = 0;
//            System.out.println("========== API GATEWAY ==========");
//            System.out.println("Username : " + username);
//            System.out.println("Role : " + role);
//            System.out.println("Authorities : " + authentication.getAuthorities());
//            System.out.println("=================================");
//count++;
//            System.out.println("hom many times "+count);
//        } catch (Exception e) {
//
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Invalid JWT");
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;          // legacy HS256 validator
    private final JwtDecoder keycloakJwtDecoder;   // Keycloak RS256 validator

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);

        if (tryAuthenticateWithKeycloak(jwt, request)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (tryAuthenticateWithLegacySecret(jwt, request)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Invalid JWT");
    }

    @SuppressWarnings("unchecked")
    private boolean tryAuthenticateWithKeycloak(String jwt, HttpServletRequest request) {
        try {
            Jwt decoded = keycloakJwtDecoder.decode(jwt);

            String username = decoded.getClaimAsString("preferred_username");
            if (username == null) {
                username = decoded.getSubject();
            }

            // Keycloak puts realm roles under realm_access.roles
            Map<String, Object> realmAccess = decoded.getClaim("realm_access");
            List<String> roles = realmAccess != null
                    ? (List<String>) realmAccess.getOrDefault("roles", List.of())
                    : List.of();

            String appRole = roles.stream()
                    .map(String::toUpperCase)
                    .filter(r -> r.equals("ADMIN") || r.equals("OPERATOR") || r.equals("CUSTOMER"))
                    .findFirst()
                    .orElse("CUSTOMER"); // temporary fallback until Keycloak role mapping is finalized

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + appRole))
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("========== API GATEWAY (Keycloak) ==========");
            System.out.println("Username : " + username);
            System.out.println("Role : " + appRole);
            System.out.println("=============================================");

            return true;

        } catch (Exception e) {
            // Not a valid Keycloak token (or wrong issuer/signature) - fall through to legacy check
            return false;
        }
    }

    private boolean tryAuthenticateWithLegacySecret(String jwt, HttpServletRequest request) {
        try {
            String username = jwtService.extractUsername(jwt);
            String role = jwtService.extractRole(jwt);

            List<SimpleGrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + role));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("========== API GATEWAY (Legacy) ==========");
            System.out.println("Username : " + username);
            System.out.println("Role : " + role);
            System.out.println("===========================================");

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}






