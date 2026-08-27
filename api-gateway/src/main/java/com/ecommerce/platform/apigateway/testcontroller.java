package com.ecommerce.platform.apigateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testcontroller {

    @GetMapping("/test")
    public String test() {
        return "Gateway is working";
    }
}
