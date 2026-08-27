package com.ecommerce.plateform.productservice.security;

import lombok.Getter;

@Getter
public class AuthenticatedUser {

    private final Long userId;
    private final String username;
    private final String role;

    public AuthenticatedUser(Long userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }
}
