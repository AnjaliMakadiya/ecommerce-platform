package com.ecommerce.plateform.orderservice.messaging;

import com.ecommerce.plateform.orderservice.kafka.OrderEvent;


public interface MessagePublisher {

    void sendOrderCreatedEvent(OrderEvent  order);

    void sendStatusUpdateEvent(OrderEvent  order);
}
