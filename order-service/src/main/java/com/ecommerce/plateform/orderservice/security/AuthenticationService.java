package com.ecommerce.plateform.orderservice.security;

import com.ecommerce.plateform.orderservice.dto.auth.AuthResponse;
import com.ecommerce.plateform.orderservice.dto.auth.LoginRequest;
import com.ecommerce.plateform.orderservice.dto.auth.RegisterRequest;
import com.ecommerce.plateform.orderservice.entity.User;
import com.ecommerce.plateform.orderservice.enums.Role;
import com.ecommerce.plateform.orderservice.enums.UserStatus;
import com.ecommerce.plateform.orderservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JwtDecoder jwtDecoder;

    public AuthResponse register(RegisterRequest request) {
        System.out.println(LocalDateTime.now()+"  come into auth response registration");
        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .status(UserStatus.ACTIVE) //TODO UPDATED 28-JULY
                .sellerVerified(false) //TODO UPDATED 28-JULY
//                .createdAt(LocalDateTime.now()) //TODO UPDATED 28-JULY
                .build();

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);

         return AuthResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )

        );
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
    //TODO CREATED ON 6-AUGUST
    public AuthResponse socialLogin(String accessToken) {

        Jwt jwt = jwtDecoder.decode(accessToken);

        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");
        String username = jwt.getClaimAsString("preferred_username");

        System.out.println("Social login details - Email: " + email + ", Name: " + name + ", Username: " + username);
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {

                    User newUser = User.builder()
                            .name(name)
                            .username(username)
                            .email(email)
                            .password("")
                            .role(Role.CUSTOMER)
                            .status(UserStatus.ACTIVE)
                            .sellerVerified(false)
                            .build();

                    return userRepository.save(newUser);
                });

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
