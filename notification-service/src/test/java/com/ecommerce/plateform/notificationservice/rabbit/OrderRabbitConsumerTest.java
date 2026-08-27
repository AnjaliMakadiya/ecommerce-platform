package com.ecommerce.plateform.notificationservice.rabbit;

import com.ecommerce.plateform.notificationservice.kafka.OrderEvent;
import com.ecommerce.plateform.notificationservice.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderRabbitConsumerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderRabbitConsumer orderRabbitConsumer;

    @Test
    void shouldConsumeOrderCreatedEvent() {

        OrderEvent event = new OrderEvent(
                1L,
                "CUST101",
                "CREATED",
                LocalDateTime.now()
        );

        orderRabbitConsumer.consumeOrderCreated(event);

        verify(notificationService).createNotification(event);
    }

    @Test
    void shouldConsumeOrderUpdatedEvent() {


        OrderEvent event = new OrderEvent(
                1L,
                "CUST101",
                "SHIPPED",
                LocalDateTime.now()
        );

        orderRabbitConsumer.consumeOrderUpdated(event);

        verify(notificationService).createNotification(event);
    }
}
