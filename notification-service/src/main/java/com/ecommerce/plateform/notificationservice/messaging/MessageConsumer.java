package com.ecommerce.plateform.notificationservice.messaging;


import com.ecommerce.plateform.notificationservice.kafka.OrderEvent;

public interface MessageConsumer {

    void consumeOrderCreated(OrderEvent event);

    void consumeOrderUpdated(OrderEvent event);
}
