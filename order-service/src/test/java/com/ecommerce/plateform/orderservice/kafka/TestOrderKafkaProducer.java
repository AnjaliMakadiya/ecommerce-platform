package com.ecommerce.plateform.orderservice.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TestOrderKafkaProducer {

    @Mock
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @InjectMocks
    private KafkaProducer producer;

    @Test
    void testSendOrderCreatedEvent() {

        OrderEvent order = new OrderEvent();
        order.setOrderId(1L);
        order.setCustomerId("1L");
        order.setStatus("CREATED");

        producer.sendOrderCreatedEvent(order);

        verify(kafkaTemplate, times(1))
                .send(eq(KafkaTopics.ORDER_CREATED_TOPIC),
                        any(OrderEvent.class));
    }

    @Test
    void testSendStatusUpdateEvent() {

        OrderEvent order = new OrderEvent();
        order.setOrderId(1L);
        order.setCustomerId("1L");
        order.setStatus("SHIPPED");

        producer.sendStatusUpdateEvent(order);

        verify(kafkaTemplate, times(1))
                .send(eq(KafkaTopics.ORDER_STATUS_TOPIC),
                        any(OrderEvent.class));
    }
}
