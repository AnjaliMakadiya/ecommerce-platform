package com.ecommerce.plateform.orderservice.rabbit;
import com.ecommerce.plateform.orderservice.kafka.OrderEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;

import static com.ecommerce.plateform.orderservice.rabbit.RabbitQueues.*;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class RabbitMqMessagePublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitMqMessagePublisher rabbitMqMessagePublisher;

    @Test
    void shouldSendOrderCreatedEvent() {

        // Arrange
        OrderEvent event = new OrderEvent(
                1L,
                "CUST101",
                "CREATED",
                LocalDateTime.now()
        );

        // Act
        rabbitMqMessagePublisher.sendOrderCreatedEvent(event);

        // Assert
        verify(rabbitTemplate).convertAndSend(
                ORDER_EXCHANGE,
                ORDER_CREATED_ROUTING_KEY,
                event
        );
    }

    @Test
    void shouldSendStatusUpdateEvent() {

        // Arrange
        OrderEvent event = new OrderEvent(
                1L,
                "CUST101",
                "SHIPPED",
                LocalDateTime.now()
        );

        // Act
        rabbitMqMessagePublisher.sendStatusUpdateEvent(event);

        // Assert
        verify(rabbitTemplate).convertAndSend(
                ORDER_EXCHANGE,
                ORDER_UPDATED_ROUTING_KEY,
                event
        );
    }
}
