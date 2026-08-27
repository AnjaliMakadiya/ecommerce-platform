package com.ecommerce.plateform.orderservice.service.security;

import com.ecommerce.plateform.orderservice.entity.User;
import com.ecommerce.plateform.orderservice.enums.Role;
import com.ecommerce.plateform.orderservice.repository.UserRepository;
import com.ecommerce.plateform.orderservice.security.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUser() {

        User user = User.builder()
                .username("admin")
                .password("password")
                .email("admin@gmail.com")
                .role(Role.ADMIN)
                .build();

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("admin");

        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
    }

    @Test
    void userNotFound() {

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("admin")
        );
    }

    @Test
    void authorities() {

        User user = User.builder()
                .username("admin")
                .password("password")
                .email("admin@gmail.com")
                .role(Role.ADMIN)
                .build();

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("admin");

        assertTrue(
                userDetails.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
        );
    }
}
