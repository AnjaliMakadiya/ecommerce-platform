package com.ecommerce.plateform.notificationservice.controller;

import com.ecommerce.plateform.notificationservice.persistance.postgres.entity.Notification;
import com.ecommerce.plateform.notificationservice.security.JwtService;
import com.ecommerce.plateform.notificationservice.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest
public class TestNotificationController {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService service;

    @MockitoBean
    private JwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

//    @Test
//    void testGetByCustomer() throws Exception {
//
//        Notification notification = new Notification();
//        notification.setNotificationId(1L);
//        notification.setCustomerId("1L");
//
//        List<Notification> notifications =
//                List.of(notification);
//
//        when(service.getByCustomerId("1L"))
//                .thenReturn(notifications);
//
//        mockMvc.perform(
//                        get("/notifications/customer/1L"))
//                .andExpect(status().isOk())
//                .andExpect( jsonPath("$.length()")
//                        .value(1))
//                .andExpect(jsonPath("$[0].customerId")
//                        .value("1L"));
//
//        verify(service, times(1))
//                .getByCustomerId("1L");
//    }

    @Test
    void testGetMyNotifications() throws Exception {

        Notification notification = new Notification();
        notification.setNotificationId(1L);
        notification.setCustomerId("1");

        when(jwtService.extractUserId("dummy-token"))
                .thenReturn(1L);

        when(service.getByCustomerId("1"))
                .thenReturn(List.of(notification));

        mockMvc.perform(get("/api/notifications/my")
                        .header("Authorization", "Bearer dummy-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value("1"));

        verify(jwtService).extractUserId("dummy-token");
        verify(service).getByCustomerId("1");
    }

    @Test
    void testGetByOrderId() throws Exception {

        Notification notification = new Notification();
        notification.setNotificationId(1L);
        notification.setOrderId(100L);

        List<Notification> notifications =
                List.of(notification);

        when(service.getByOrderId(100L))
                .thenReturn(notifications);

        mockMvc.perform(
                        get("/api/notifications/order/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                        .value(1))
                .andExpect(jsonPath("$[0].orderId")
                        .value(100));

        verify(service, times(1))
                .getByOrderId(100L);
    }
}
