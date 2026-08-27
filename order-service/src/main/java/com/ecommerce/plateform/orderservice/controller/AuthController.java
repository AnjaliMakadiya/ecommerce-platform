package com.ecommerce.plateform.orderservice.controller;

import com.ecommerce.plateform.orderservice.dto.auth.AuthResponse;
import com.ecommerce.plateform.orderservice.dto.auth.LoginRequest;
import com.ecommerce.plateform.orderservice.dto.auth.RegisterRequest;
import com.ecommerce.plateform.orderservice.dto.auth.SocialLoginRequest;
import com.ecommerce.plateform.orderservice.security.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request){

        return authenticationService.register(request);

    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request){

        return authenticationService.login(request);

    }

    //TODO CREATED ON 6-AUGUST
    @PostMapping("/social-login")
    public ResponseEntity<AuthResponse> socialLogin(
            @RequestBody SocialLoginRequest request) {
        System.out.println("Social login request received with access token: " + request.getAccessToken());
        return ResponseEntity.ok(
                authenticationService.socialLogin(request.getAccessToken())
        );
    }
}
