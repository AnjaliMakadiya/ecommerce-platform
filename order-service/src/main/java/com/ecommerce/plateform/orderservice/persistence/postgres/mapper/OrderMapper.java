package com.ecommerce.plateform.orderservice.persistence.postgres.mapper;

import com.ecommerce.plateform.orderservice.dto.CreateOrderRequest;
import com.ecommerce.plateform.orderservice.persistence.postgres.entity.Order;
import com.ecommerce.plateform.orderservice.enums.OrderStatus;

import java.time.LocalDateTime;


public class OrderMapper {


        public static Order newOrder(String customerId) {
            Order order = new Order();
            order.setCustomerId(customerId);
            order.setStatus(OrderStatus.CREATED);
            return order;
        }
}