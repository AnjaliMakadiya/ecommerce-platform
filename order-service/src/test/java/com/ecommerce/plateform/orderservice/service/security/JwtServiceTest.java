package com.ecommerce.plateform.orderservice.service.security;

import com.ecommerce.plateform.orderservice.entity.User;
import com.ecommerce.plateform.orderservice.enums.Role;
import com.ecommerce.plateform.orderservice.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService,
                "secretKey",
                "VGhpc0lzQVNlY3JldEtleUZvckpXVFRlc3RpbmdQdXJwb3NlT25seTEyMzQ1Njc4OTA=");

        ReflectionTestUtils.setField(jwtService,
                "jwtExpiration",
                3600000L);

        user = User.builder()
                .username("admin")
                .password("password")
                .email("admin@gmail.com")
                .role(Role.ADMIN)
                .build();
    }

    @Test
    void generateToken() {

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername() {

        String token = jwtService.generateToken(user);

        String username = jwtService.extractUsername(token);

        assertEquals("admin", username);
    }

    @Test
    void validateToken() {

        String token = jwtService.generateToken(user);

        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(
                        "admin",
                        "password",
                        java.util.Collections.emptyList());

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void invalidUser() {

        String token = jwtService.generateToken(user);

        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(
                        "customer",
                        "password",
                        java.util.Collections.emptyList());

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void extractExpiration() {

        String token = jwtService.generateToken(user);

        Date expiration = jwtService.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

}
