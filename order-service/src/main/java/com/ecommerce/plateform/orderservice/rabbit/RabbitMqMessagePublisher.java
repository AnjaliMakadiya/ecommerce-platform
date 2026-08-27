package com.ecommerce.plateform.orderservice.rabbit;

import com.ecommerce.plateform.orderservice.kafka.OrderEvent;
import com.ecommerce.plateform.orderservice.messaging.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import static com.ecommerce.plateform.orderservice.rabbit.RabbitQueues.*;


@Service
@RequiredArgsConstructor
@Profile("rabbitmq")
public class RabbitMqMessagePublisher implements MessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
        public void sendOrderCreatedEvent(OrderEvent order) {
        System.out.println("Before message Send");
        rabbitTemplate.convertAndSend(
                ORDER_EXCHANGE,
                ORDER_CREATED_ROUTING_KEY,
                order
        );
        System.out.println("Message sent successfully");
    }

    @Override
    public void sendStatusUpdateEvent(OrderEvent  order) {
        System.out.println("Before message Send");
        rabbitTemplate.convertAndSend(
                ORDER_EXCHANGE,
                ORDER_UPDATED_ROUTING_KEY,
                order
        );
        System.out.println("Message sent successfully");
    }
}
