package com.ecommerce.plateform.notificationservice.controller;

import com.ecommerce.plateform.notificationservice.persistance.postgres.entity.Notification;
import com.ecommerce.plateform.notificationservice.security.JwtService;
import com.ecommerce.plateform.notificationservice.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;
    private final JwtService jwtService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Notification> getAllNotifications() {

        return service.getAllNotifications();
    }


    @GetMapping("/customer/{customerId}")
    public List<Notification> getByCustomer(@PathVariable String customerId) {
        return service.getByCustomerId(customerId);
    }

    @GetMapping("/order/{orderId}")
    public List<Notification> getByOrderId(@PathVariable Long orderId) {
        return service.getByOrderId(orderId);
    }

    @GetMapping("/my")
    public List<Notification> getMyNotifications(HttpServletRequest request) {
        System.out.println("Request"+ request);
        String authHeader = request.getHeader("Authorization");

        String token = authHeader.substring(7);

        Long customerId = jwtService.extractUserId(token);

        List<Notification> customer = service.getByCustomerId(customerId.toString());
        return customer;
    }
}
