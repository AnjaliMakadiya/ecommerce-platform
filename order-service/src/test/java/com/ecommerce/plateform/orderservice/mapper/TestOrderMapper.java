package com.ecommerce.plateform.orderservice.mapper;

import com.ecommerce.plateform.orderservice.dto.CreateOrderRequest;
import com.ecommerce.plateform.orderservice.persistence.postgres.entity.Order;
import com.ecommerce.plateform.orderservice.enums.OrderStatus;
import com.ecommerce.plateform.orderservice.persistence.postgres.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class TestOrderMapper {
//
//    @Test
//    void testToEntity() {
//        CreateOrderRequest request = new CreateOrderRequest();
//        request.setCustomerId("1L");
//
//        Order order = OrderMapper.newOrder("1L");
//        assertNotNull(order);
//        assertEquals("1L", order.getCustomerId());
//        assertEquals(OrderStatus.CREATED,
//                order.getStatus());
//
//        assertNotNull(order.getCreatedAt());
//
//    }
}