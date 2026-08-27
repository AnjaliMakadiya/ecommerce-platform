package com.ecommerce.platform.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

@Configuration
public class KeycloakJwtDecoderConfig {

//    // Same issuer your React app / Keycloak use externally.
//    // Must match the "iss" claim inside the token exactly.
//    @Value("${keycloak.issuer-uri}")
//    private String issuerUri;
//
//    @Bean
//    @Lazy
//    public JwtDecoder keycloakJwtDecoder() {
//        System.out.println("Creating Keycloak JwtDecoder with issuer URI: " + issuerUri);
//        return JwtDecoders.fromIssuerLocation(issuerUri);
//    }
//}

    // Docker-internal hostname - only used to FETCH the signing keys.
    // Reachable from inside a container; "localhost:8181" is not.
    @Value("${keycloak.jwk-set-uri}")
    private String jwkSetUri;

    // The PUBLIC-facing issuer Keycloak actually stamps into tokens
    // (the browser always talks to Keycloak via this URL).
    @Value("${keycloak.issuer-uri}")
    private String tokenIssuer;

    @Bean
    @Lazy
    public JwtDecoder keycloakJwtDecoder() {
        System.out.println("Creating Keycloak JwtDecoder with JWK Set URI: " + jwkSetUri + " and token issuer: " + tokenIssuer);
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withJwkSetUri(jwkSetUri)
                .build();

        OAuth2TokenValidator<Jwt> withIssuer =
                JwtValidators.createDefaultWithIssuer(tokenIssuer);
        decoder.setJwtValidator(withIssuer);

        return decoder;
    }
}

