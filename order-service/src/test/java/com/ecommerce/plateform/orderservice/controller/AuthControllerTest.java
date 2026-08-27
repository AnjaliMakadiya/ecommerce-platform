package com.ecommerce.plateform.orderservice.controller;

import com.ecommerce.plateform.orderservice.dto.auth.AuthResponse;
import com.ecommerce.plateform.orderservice.dto.auth.LoginRequest;
import com.ecommerce.plateform.orderservice.dto.auth.RegisterRequest;
import com.ecommerce.plateform.orderservice.security.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthController authController;

//    @Test
//    void register() {
//
//        RegisterRequest request = new RegisterRequest();
//
//        AuthResponse response = new AuthResponse("jwt-token");
//
//        when(authenticationService.register(request))
//                .thenReturn(response);
//
//        AuthResponse result = authController.register(request);
//
//        assertEquals("jwt-token", result.getToken());
//
//        verify(authenticationService).register(request);
//    }

//    @Test
//    void login() {
//
//        LoginRequest request = new LoginRequest();
//
//        AuthResponse response = new AuthResponse("jwt-token");
//
//        when(authenticationService.login(request))
//                .thenReturn(response);
//
//        AuthResponse result = authController.login(request);
//
//        assertEquals("jwt-token", result.getToken());
//
//        verify(authenticationService).login(request);
//    }
}
