package com.ecommerce.plateform.orderservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

//TODO CREATED ON 6-AUGUST
@Component
@RequiredArgsConstructor
public class KeycloakTokenVerifier {

    private final JwtDecoder jwtDecoder;

    public Jwt verify(String accessToken) {
        return jwtDecoder.decode(accessToken);
    }
}
