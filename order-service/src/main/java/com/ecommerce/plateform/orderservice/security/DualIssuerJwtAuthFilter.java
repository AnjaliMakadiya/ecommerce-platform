//package com.ecommerce.plateform.orderservice.security;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Base64;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//public class DualIssuerJwtAuthFilter extends OncePerRequestFilter {
//
//    // --- your existing custom-JWT verification, kept as-is ---
//    @Value("${jwt.secret}")
//    private String legacySecret;
//
//    @Value("${jwt.issuer:self}")
//    private String legacyIssuer; // whatever "iss" value your own tokens use, e.g. "ecommerce-auth"
//
//    // --- Keycloak verification, added ---
//    @Value("${keycloak.jwk-set-uri}")
//    private String keycloakJwkSetUri;
//
//    @Value("${keycloak.issuer-uri}")
//    private String keycloakIssuerUri;
//
//    private JwtDecoder keycloakDecoder; // lazily built, caches Keycloak's public keys
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        String header = request.getHeader("Authorization");
//        if (header == null || !header.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String token = header.substring(7);
//
//        try {
//            String issuer = extractIssuerWithoutVerifying(token);
//
//            if (keycloakIssuerUri.equals(issuer)) {
//                authenticateWithKeycloak(token);
//            } else {
//                authenticateWithLegacy(token);
//            }
//        } catch (Exception ex) {
//            // invalid/expired token -> leave SecurityContext empty, let Spring Security reject downstream
//            SecurityContextHolder.clearContext();
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    /** Peek at the "iss" claim WITHOUT verifying signature yet — just to decide which validator to use. */
//    private String extractIssuerWithoutVerifying(String token) throws IOException {
//        String[] parts = token.split("\\.");
//        if (parts.length < 2) throw new IllegalArgumentException("Malformed JWT");
//        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
//        JsonNode payload = objectMapper.readTree(payloadJson);
//        JsonNode iss = payload.get("iss");
//        return iss != null ? iss.asText() : legacyIssuer;
//    }
//
//    /**
//     * Existing custom-JWT path — same logic you have today, just relocated here.
//     * Note: since you're on HS256, legacySecret must be at least 256 bits (32+ chars),
//     * or parseClaimsJws will throw io.jsonwebtoken.security.WeakKeyException.
//     */
//    private void authenticateWithLegacy(String token) {
//        Claims claims = Jwts.parser()
//                .verifyWith(getLegacySigningKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//
//        String username = claims.getSubject();
//        String role = claims.get("role", String.class); // adjust claim name to match your existing tokens
//
//        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
//
//        var authToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
//        SecurityContextHolder.getContext().setAuthentication(authToken);
//    }
//    /** legacySecret is expected Base64-encoded, matching the gateway's JwtService pattern. */
//    private javax.crypto.SecretKey getLegacySigningKey() {
//        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(legacySecret);
//        return io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);
//    }
//
//    /** New Keycloak path — verifies against Keycloak's JWKS, maps realm_access.roles -> authorities. */
//    private void authenticateWithKeycloak(String token) {
//        if (keycloakDecoder == null) {
//            keycloakDecoder = NimbusJwtDecoder.withJwkSetUri(keycloakJwkSetUri).build();
//        }
//
//        Jwt jwt = keycloakDecoder.decode(token);
//
//        String username = jwt.getClaimAsString("preferred_username");
//
//        // Keycloak puts realm roles under: realm_access.roles = ["ADMIN", ...]
//        var realmAccess = jwt.getClaim("realm_access");
//        List<GrantedAuthority> authorities;
//        if (realmAccess instanceof java.util.Map<?, ?> map && map.get("roles") instanceof List<?> roles) {
//            authorities = roles.stream()
//                    .map(r -> "ROLE_" + r)
//                    .map(SimpleGrantedAuthority::new)
//                    .collect(Collectors.toList());
//        } else {
//            authorities = List.of();
//        }
//
//        var authToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
//        SecurityContextHolder.getContext().setAuthentication(authToken);
//    }
//}

package com.ecommerce.plateform.orderservice.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Base64;

@Component
public class DualIssuerJwtAuthFilter extends OncePerRequestFilter {

    // ============================================================
    // LEGACY JWT CONFIGURATION
    // ============================================================

    @Value("${jwt.secret}")
    private String legacySecret;

    @Value("${jwt.issuer:self}")
    private String legacyIssuer;


    // ============================================================
    // KEYCLOAK CONFIGURATION
    // ============================================================

    @Value("${keycloak.jwk-set-uri}")
    private String keycloakJwkSetUri;

    @Value("${keycloak.issuer-uri}")
    private String keycloakIssuerUri;


    // ============================================================
    // SERVICES
    // ============================================================

    private final CustomUserDetailsService userDetailsService;

    private JwtDecoder keycloakDecoder;

    private final ObjectMapper objectMapper = new ObjectMapper();


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public DualIssuerJwtAuthFilter(
            CustomUserDetailsService userDetailsService
    ) {
        this.userDetailsService = userDetailsService;
    }


    // ============================================================
    // MAIN FILTER
    // ============================================================

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {

            String issuer =
                    extractIssuerWithoutVerifying(token);

            System.out.println("======================================");
            System.out.println("ORDER SERVICE JWT");
            System.out.println("Issuer: " + issuer);
            System.out.println("======================================");


            // ====================================================
            // KEYCLOAK TOKEN
            // ====================================================

            if (keycloakIssuerUri.equals(issuer)) {

                authenticateWithKeycloak(token);

            }

            // ====================================================
            // OUR APPLICATION JWT
            // ====================================================

            else {

                authenticateWithLegacy(token);

            }

        } catch (Exception ex) {

            ex.printStackTrace();

            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }


    // ============================================================
    // EXTRACT ISSUER WITHOUT VERIFYING SIGNATURE
    // ============================================================

    private String extractIssuerWithoutVerifying(
            String token
    ) throws IOException {

        String[] parts = token.split("\\.");

        if (parts.length < 2) {
            throw new IllegalArgumentException("Malformed JWT");
        }

        String payloadJson =
                new String(
                        Base64.getUrlDecoder().decode(parts[1])
                );

        JsonNode payload =
                objectMapper.readTree(payloadJson);

        JsonNode iss =
                payload.get("iss");

        return iss != null
                ? iss.asText()
                : legacyIssuer;
    }


    // ============================================================
    // LEGACY APPLICATION JWT
    // ============================================================

    private void authenticateWithLegacy(String token) {

        Claims claims =
                Jwts.parser()
                        .verifyWith(getLegacySigningKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();


        String username =
                claims.getSubject();


        System.out.println("========== LEGACY JWT ==========");
        System.out.println("Username: " + username);


        // IMPORTANT:
        // Load the real application user from DB.
        //
        // This returns CustomUserDetails.
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(username);


        // ========================================================
        // IMPORTANT FIX
        // ========================================================
        //
        // DO NOT use:
        //
        // new UsernamePasswordAuthenticationToken(
        //      username,
        //      null,
        //      authorities
        // );
        //
        // because that makes principal = String.
        //
        // Instead use:
        //
        // principal = userDetails
        //

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );


        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);


        System.out.println(
                "Principal type: "
                        + authentication
                        .getPrincipal()
                        .getClass()
                        .getName()
        );

        System.out.println(
                "User ID: "
                        + ((CustomUserDetails)
                        authentication.getPrincipal())
                        .getUserId()
        );

        System.out.println(
                "Authorities: "
                        + authentication.getAuthorities()
        );

        System.out.println("===============================");
    }


    // ============================================================
    // LEGACY SIGNING KEY
    // ============================================================

    private SecretKey getLegacySigningKey() {

        byte[] keyBytes =
                io.jsonwebtoken.io.Decoders
                        .BASE64
                        .decode(legacySecret);

        return io.jsonwebtoken.security.Keys
                .hmacShaKeyFor(keyBytes);
    }


    // ============================================================
    // KEYCLOAK JWT
    // ============================================================

    private void authenticateWithKeycloak(String token) {

        if (keycloakDecoder == null) {

            keycloakDecoder =
                    NimbusJwtDecoder
                            .withJwkSetUri(keycloakJwkSetUri)
                            .build();
        }


        // Verify Keycloak signature
        Jwt jwt =
                keycloakDecoder.decode(token);


        String username =
                jwt.getClaimAsString(
                        "preferred_username"
                );


        if (username == null) {

            username =
                    jwt.getSubject();
        }


        System.out.println("========== KEYCLOAK JWT ==========");
        System.out.println("Username: " + username);


        // ========================================================
        // LOAD USER FROM OUR APPLICATION DATABASE
        // ========================================================

        UserDetails userDetails =
                userDetailsService
                        .loadUserByUsername(username);


        // ========================================================
        // CREATE AUTHENTICATION WITH CustomUserDetails
        // ========================================================

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );


        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);


        System.out.println(
                "Principal type: "
                        + authentication
                        .getPrincipal()
                        .getClass()
                        .getName()
        );


        System.out.println(
                "User ID: "
                        + ((CustomUserDetails)
                        authentication.getPrincipal())
                        .getUserId()
        );


        System.out.println(
                "Authorities: "
                        + authentication.getAuthorities()
        );


        System.out.println("==================================");
    }
}