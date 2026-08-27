package com.ecommerce.plateform.orderservice.controller;

import com.ecommerce.plateform.orderservice.dto.CreateOrderRequest;
import com.ecommerce.plateform.orderservice.dto.UpdateStatusRequest;
import com.ecommerce.plateform.orderservice.persistence.postgres.entity.Order;
import com.ecommerce.plateform.orderservice.enums.OrderStatus;
import com.ecommerce.plateform.orderservice.security.CustomUserDetailsService;
import com.ecommerce.plateform.orderservice.security.JwtAuthenticationFilter;
import com.ecommerce.plateform.orderservice.security.JwtService;
import com.ecommerce.plateform.orderservice.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TestOrderController {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService service;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

//    @Autowired
//    private ObjectMapper objectMapper;
    @Autowired
    private JsonMapper jsonMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

//    @Test
//    @WithMockUser(roles = {"OPERATOR"})
//    void testCreateOrder() throws Exception {
//
//        CreateOrderRequest request = new CreateOrderRequest();
//        request.setCustomerId("1L");
//
//        Order order = new Order();
//        order.setOrderId(1L);
//        order.setCustomerId("1L");
//
//        when(service.createOrder(any(CreateOrderRequest.class))).thenReturn(order);
//
//        mockMvc.perform(post("/api/orders")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.orderId").value(1));
//
//        verify(service, times(1))
//                .createOrder(any(CreateOrderRequest.class));
//    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void testGetAllOrders() throws Exception {

        Order order1 = new Order();
        order1.setOrderId(1L);

        Order order2 = new Order();
        order2.setOrderId(2L);

        List<Order> orders = List.of(order1, order2);

        when(service.getAllOrders())
                .thenReturn(orders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(service, times(1))
                .getAllOrders();
    }

//    @Test
//    @WithMockUser(roles = {"CUSTOMER"})
//    void testGetOrder() throws Exception {
//
//        Order order = new Order();
//        order.setOrderId(1L);
//        order.setCustomerId("1l");
////
//        when(service.getOrder(1L))
//                .thenReturn(order);
//
//        mockMvc.perform(get("/api/orders/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.orderId").value(1));
//
//        verify(service, times(1))
//                .getOrder(1L);
//    }

    @Test
    void testOrderNotFount() throws Exception {

        when(service.getOrder(1L)).thenThrow(new RuntimeException("Order not found"));

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isNotFound());

        verify(service, times(1))
                .getOrder(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdateStatus() throws Exception {

        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setStatus(OrderStatus.SHIPPED);

        Order order = new Order();
        order.setOrderId(1L);
        order.setStatus(OrderStatus.SHIPPED);

        when(service.updateStatus(
                eq(1L),
                any(UpdateStatusRequest.class)))
                .thenReturn(order);

        mockMvc.perform(put("/api/orders/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").value("SHIPPED"));

        verify(service, times(1))
                .updateStatus(eq(1L),
                        any(UpdateStatusRequest.class));
    }

    @Test
    void testUpdateStatusNotFound() throws Exception {

        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setStatus(OrderStatus.SHIPPED);

        when(service.updateStatus(
                eq(1L),
                any(UpdateStatusRequest.class)))
                .thenThrow(new RuntimeException("Order not found"));

        mockMvc.perform(put("/api/orders/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Order not found"));

        verify(service, times(1))
                .updateStatus(eq(1L),
                        any(UpdateStatusRequest.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteOrder() throws Exception {

        when(service.deleteOrder(1L))
                .thenReturn(
                        "Order deleted successfully with id: 1");

        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string("Order deleted successfully with id: 1"));

        verify(service, times(1))
                .deleteOrder(1L);
    }

    @Test
    void testDeleteOrderNotFound() throws Exception {

        when(service.deleteOrder(1L))
                .thenThrow(new RuntimeException("Order not found"));

        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Order not found"));

        verify(service, times(1))
                .deleteOrder(1L);
    }
}
