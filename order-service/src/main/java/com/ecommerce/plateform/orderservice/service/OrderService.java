package com.ecommerce.plateform.orderservice.service;

import com.ecommerce.plateform.orderservice.dto.CreateOrderRequest;
import com.ecommerce.plateform.orderservice.dto.UpdateStatusRequest;
import com.ecommerce.plateform.orderservice.persistence.postgres.entity.Order;

import java.util.List;

public interface OrderService {

    Order createOrder(CreateOrderRequest request);
    Order getOrder(Long id);
    public List<Order> getAllOrders();
    Order updateStatus(Long id, UpdateStatusRequest newStatus);
    public String deleteOrder(Long id);
    public List<Order> getMyOrders();
}
