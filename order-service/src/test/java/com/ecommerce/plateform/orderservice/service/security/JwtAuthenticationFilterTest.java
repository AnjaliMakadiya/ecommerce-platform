package com.ecommerce.plateform.orderservice.service.security;

import com.ecommerce.plateform.orderservice.security.CustomUserDetailsService;
import com.ecommerce.plateform.orderservice.security.JwtAuthenticationFilter;
import com.ecommerce.plateform.orderservice.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    void headerMissing() throws ServletException, IOException {

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void invalidHeader() throws ServletException, IOException {

        request.addHeader("Authorization", "Basic abc123");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void validToken() throws ServletException, IOException {

        request.addHeader("Authorization", "Bearer jwt-token");

        UserDetails userDetails =
                new org.springframework.security.core.userdetails.User(
                        "admin",
                        "password",
                        Collections.emptyList());

        when(jwtService.extractUsername("jwt-token"))
                .thenReturn("admin");

        when(userDetailsService.loadUserByUsername("admin"))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid("jwt-token", userDetails))
                .thenReturn(true);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void invalidToken() throws ServletException, IOException {

        request.addHeader("Authorization", "Bearer jwt-token");

        UserDetails userDetails =
                new org.springframework.security.core.userdetails.User(
                        "admin",
                        "password",
                        Collections.emptyList());

        when(jwtService.extractUsername(anyString()))
                .thenReturn("admin");

        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid(anyString(), eq(userDetails)))
                .thenReturn(false);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void continueFilterChain() throws ServletException, IOException {

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1))
                .doFilter(request, response);
    }
}
