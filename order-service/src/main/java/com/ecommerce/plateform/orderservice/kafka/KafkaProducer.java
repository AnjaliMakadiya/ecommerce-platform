package com.ecommerce.plateform.orderservice.kafka;

import com.ecommerce.plateform.orderservice.messaging.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.ecommerce.plateform.orderservice.kafka.KafkaTopics.ORDER_CREATED_TOPIC;
import static com.ecommerce.plateform.orderservice.kafka.KafkaTopics.ORDER_STATUS_TOPIC;

@Service
@RequiredArgsConstructor
@Profile("kafka")
public class KafkaProducer implements MessagePublisher {


    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Override
    public void sendOrderCreatedEvent(OrderEvent  order) {

        OrderEvent event = new OrderEvent(
                order.getOrderId(),
                order.getCustomerId(),
                order.getStatus(),
                LocalDateTime.now()
        );
        System.out.println(event);
        System.out.println("Before Kafka Send");
        try{
            kafkaTemplate.send(ORDER_CREATED_TOPIC, event).get();
            System.out.println("After Kafka Send");
            System.out.println("Message sent successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void sendStatusUpdateEvent(OrderEvent  order) {

        OrderEvent event = new OrderEvent(
                order.getOrderId(),
                order.getCustomerId(),
                order.getStatus(),
                LocalDateTime.now()
        );

        kafkaTemplate.send(ORDER_STATUS_TOPIC, event);
    }
}
