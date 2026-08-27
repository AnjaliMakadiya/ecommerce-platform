package com.ecommerce.plateform.orderservice.controller;

import com.ecommerce.plateform.orderservice.dto.CreateOrderRequest;
import com.ecommerce.plateform.orderservice.dto.UpdateStatusRequest;
import com.ecommerce.plateform.orderservice.persistence.postgres.entity.Order;
import com.ecommerce.plateform.orderservice.security.SecurityUtils;
import com.ecommerce.plateform.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;


    @PreAuthorize("hasAnyRole('CUSTOMER','OPERATOR','ADMIN')")
    @GetMapping("/debug/whoami")
    public ResponseEntity<?> whoAmI() {
        System.out.println("calling");
        Long userId = SecurityUtils.getCurrentUserId();
        String role = SecurityUtils.getCurrentRole();
        return ResponseEntity.ok(Map.of("userId", userId, "role", role));
    }

    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @PostMapping
    public Order createOrder(@Valid @RequestBody CreateOrderRequest request) {

        System.out.println("come into controller");
        return service.createOrder(request);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @GetMapping("/my")
    public List<Order> getMyOrder() {
        return service.getMyOrders();
    }

    @PreAuthorize("hasAnyRole('CUSTOMER','OPERATOR','ADMIN')")
    @GetMapping
    public List<Order> getAllOrders() {

        return service.getAllOrders();
    }

    @PreAuthorize("hasAnyRole('CUSTOMER','OPERATOR','ADMIN')")
    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        Order order = service.getOrder(id);
        // Customers may only view their own orders. Admins/operators can view any.
        if ("CUSTOMER".equals(SecurityUtils.getCurrentRole())
                && !order.getCustomerId().equals(String.valueOf(SecurityUtils.getCurrentUserId()))) {
            throw new AccessDeniedException("You do not have access to this order");
        }
        return order;
    }

    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id,
                              @RequestBody UpdateStatusRequest request) {

        return service.updateStatus(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Long id) {
        return service.deleteOrder(id);
    }
}
