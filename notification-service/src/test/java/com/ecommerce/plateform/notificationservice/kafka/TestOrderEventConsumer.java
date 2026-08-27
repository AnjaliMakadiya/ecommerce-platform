package com.ecommerce.plateform.notificationservice.kafka;

import com.ecommerce.plateform.notificationservice.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TestOrderEventConsumer {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderEventConsumer consumer;

    @Test
    void testConsumeOrderCreated() {

        OrderEvent event = new OrderEvent();
        event.setOrderId(1L);
        event.setCustomerId("1L");
        event.setStatus("CREATED");

        consumer.consumeOrderCreated(event);

        verify(notificationService, times(1))
                .createNotification(event);
    }

    @Test
    void testConsumeStatusUpdate() {

        OrderEvent event = new OrderEvent();
        event.setOrderId(1L);
        event.setCustomerId("1L");
        event.setStatus("SHIPPED");

        consumer.consumeOrderUpdated(event);

        verify(notificationService, times(1))
                .createNotification(event);
    }
}
