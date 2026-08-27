package com.ecommerce.plateform.orderservice.dto.auth;

import com.ecommerce.plateform.orderservice.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

//    private String token;

    //TODO UPDATED ON 28-JULY

    private String token;
    private Long userId;
    private String name;
    private String username;
    private String email;
    private Role role;

}
