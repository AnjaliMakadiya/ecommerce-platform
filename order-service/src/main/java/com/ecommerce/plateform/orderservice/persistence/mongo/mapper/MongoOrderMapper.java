package com.ecommerce.plateform.orderservice.persistence.mongo.mapper;

import com.ecommerce.plateform.orderservice.persistence.mongo.entity.OrderDocument;
import com.ecommerce.plateform.orderservice.persistence.postgres.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class MongoOrderMapper {

    public OrderDocument toDocumentEntity(Order order) {


        if (order == null) {
            return null;
        }
        OrderDocument document = new OrderDocument();

        document.setOrderId(order.getOrderId());
        document.setCustomerId(order.getCustomerId());
        document.setStatus(order.getStatus());
        document.setCreatedAt(order.getCreatedAt());
        document.setUpdatedAt(order.getUpdatedAt());

        return document;
    }

    public Order toOrderEntity(OrderDocument document) {


        if (document == null) {
            return null;
        }
        Order order = new Order();

        order.setOrderId(document.getOrderId());
        order.setCustomerId(document.getCustomerId());
        order.setStatus(document.getStatus());
        order.setCreatedAt(document.getCreatedAt());
        order.setUpdatedAt(document.getUpdatedAt());

        return order;
    }
}
