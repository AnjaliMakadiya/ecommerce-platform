package com.ecommerce.plateform.orderservice.entity;

import com.ecommerce.plateform.orderservice.enums.Role;
import com.ecommerce.plateform.orderservice.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, unique = true)
//    private String username;
//
//    @Column(nullable = false)
//    private String password;
//
//    @Email
//    @Column(nullable = false, unique = true)
//    private String email;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private Role role;


    //TODO UPDATED ON 28-JULY
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Full Name
     */
    @Column(nullable = false)
    private String name;

    /**
     * Login Username
     */
    @Column(nullable = false, unique = true, length = 100)
    private String username;

    /**
     * Encrypted Password
     */
    @Column(nullable = false)
    private String password;

    /**
     * Email Address
     */
    @Email
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /**
     * CUSTOMER / OPERATOR / ADMIN
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    /**
     * Account Status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * Seller Verification Status
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean sellerVerified = false;

    /**
     * Account Creation Time
     */
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Last Updated Time
     */
    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    //TODO UPDATED ON 6-AUGUST
    @Column(unique = true)
    private String keycloakId;  // Keycloak "sub"

    @Column(length = 30)
    private String provider;    // LOCAL / GOOGLE / GITHUB / FACEBOOK
}
