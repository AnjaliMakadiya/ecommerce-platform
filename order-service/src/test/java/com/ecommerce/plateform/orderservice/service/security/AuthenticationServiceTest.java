package com.ecommerce.plateform.orderservice.service.security;

import com.ecommerce.plateform.orderservice.dto.auth.AuthResponse;
import com.ecommerce.plateform.orderservice.dto.auth.LoginRequest;
import com.ecommerce.plateform.orderservice.dto.auth.RegisterRequest;
import com.ecommerce.plateform.orderservice.entity.User;
import com.ecommerce.plateform.orderservice.enums.Role;
import com.ecommerce.plateform.orderservice.repository.UserRepository;
import com.ecommerce.plateform.orderservice.security.AuthenticationService;
import com.ecommerce.plateform.orderservice.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void shouldRegisterUserSuccessfully() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("admin");
        request.setEmail("admin@gmail.com");
        request.setPassword("Admin@123");
//        request.setRole(Role.ADMIN);

        when(passwordEncoder.encode("Admin@123"))
                .thenReturn("encodedPassword");

        when(jwtService.generateToken(any(User.class)))
                .thenReturn("jwt-token");

        AuthResponse response = authenticationService.register(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());

        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
    }

    @Test
    void shouldEncodePasswordBeforeSavingUser() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("admin");
        request.setEmail("admin@gmail.com");
        request.setPassword("Admin@123");
//        request.setRole(Role.ADMIN);

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        when(jwtService.generateToken(any(User.class)))
                .thenReturn("token");

        authenticationService.register(request);

        verify(passwordEncoder).encode("Admin@123");
    }

    @Test
    void shouldSaveUserIntoDatabase() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("admin");
        request.setEmail("admin@gmail.com");
        request.setPassword("Admin@123");
//        request.setRole(Role.ADMIN);

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        when(jwtService.generateToken(any(User.class)))
                .thenReturn("token");

        authenticationService.register(request);

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();

        assertEquals("admin", savedUser.getUsername());
        assertEquals("admin@gmail.com", savedUser.getEmail());
//        assertEquals(Role.ADMIN, savedUser.getRole());
        assertEquals("encodedPassword", savedUser.getPassword());
    }

    @Test
    void shouldGenerateJwtAfterRegistration() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("admin");
        request.setEmail("admin@gmail.com");
        request.setPassword("Admin@123");
//        request.setRole(Role.ADMIN);

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        when(jwtService.generateToken(any(User.class)))
                .thenReturn("jwt-token");

        authenticationService.register(request);

        verify(jwtService).generateToken(any(User.class));
    }

    @Test
    void shouldLoginSuccessfully() {

        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("Admin@123");

        User user = User.builder()
                .username("admin")
                .password("encodedPassword")
                .role(Role.ADMIN)
                .email("admin@gmail.com")
                .build();

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(user))
                .thenReturn("jwt-token");

        AuthResponse response =
                authenticationService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());

        verify(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldAuthenticateUserDuringLogin() {

        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("Admin@123");

        User user = User.builder()
                .username("admin")
                .password("encoded")
                .email("admin@gmail.com")
                .role(Role.ADMIN)
                .build();

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(user))
                .thenReturn("token");

        authenticationService.login(request);

        verify(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldGenerateTokenAfterLogin() {

        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("Admin@123");

        User user = User.builder()
                .username("admin")
                .password("encoded")
                .email("admin@gmail.com")
                .role(Role.ADMIN)
                .build();

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(user))
                .thenReturn("jwt-token");

        authenticationService.login(request);

        verify(jwtService).generateToken(user);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("Admin@123");

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authenticationService.login(request)
        );

        assertEquals("User not found", exception.getMessage());
    }

}
