package com.ecommerce.plateform.notificationservice.kafka;


import com.ecommerce.plateform.notificationservice.messaging.MessageConsumer;
import com.ecommerce.plateform.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.plateform.notificationservice.kafka.KafkaTopics.ORDER_CREATED_TOPIC;
import static com.ecommerce.plateform.notificationservice.kafka.KafkaTopics.ORDER_STATUS_TOPIC;

@Service
@RequiredArgsConstructor
@Profile("kafka")
public class OrderEventConsumer implements MessageConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = ORDER_CREATED_TOPIC, groupId = "notification-group")
    public void consumeOrderCreated(OrderEvent event) {
        System.out.println("Received Event: " + event);
        notificationService.createNotification(event);
    }

    @KafkaListener(topics = ORDER_STATUS_TOPIC, groupId = "notification-group")
    public void consumeOrderUpdated(OrderEvent event) {

        notificationService.createNotification(event);
    }
}
