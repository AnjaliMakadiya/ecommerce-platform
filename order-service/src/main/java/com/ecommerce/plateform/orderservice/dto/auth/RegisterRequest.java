package com.ecommerce.plateform.orderservice.dto.auth;

import com.ecommerce.plateform.orderservice.enums.Role;
import lombok.Data;

@Data
public class RegisterRequest {

    private String name; // TODO UPDATED ON 28-JULY
    private String username;
    private String email;
    private String password;
//    private Role role;  //TODO UPDATED ON 28-JULY
}
