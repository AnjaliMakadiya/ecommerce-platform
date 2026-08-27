package com.ecommerce.plateform.notificationservice.service;

import com.ecommerce.plateform.notificationservice.persistance.port.NotificationPersistencePort;
import com.ecommerce.plateform.notificationservice.persistance.postgres.entity.Notification;
import com.ecommerce.plateform.notificationservice.kafka.OrderEvent;
import com.ecommerce.plateform.notificationservice.persistance.postgres.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
//    private NotificationRepository repository;
    private NotificationPersistencePort persistencePort;

    @InjectMocks
    private NotificationService service;

    @Test
    void testCreateNotification() {

        OrderEvent event = new OrderEvent();
        event.setOrderId(1L);
        event.setCustomerId("1L");
        event.setStatus("CREATED");

        service.createNotification(event);

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(persistencePort).save(captor.capture());

        Notification saved = captor.getValue();

        assertEquals("1L", saved.getCustomerId());
        assertEquals(1L, saved.getOrderId());
        assertTrue(saved.getMessage().contains("CREATED"));
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void testGetByCustomerId() {

        Notification notification = new Notification();
        notification.setCustomerId("1L");

        List<Notification> notifications =
                List.of(notification);

        when(persistencePort.findByCustomerId("1L"))
                .thenReturn(notifications);

        List<Notification> result =
                service.getByCustomerId("1L");

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(persistencePort, times(1))
                .findByCustomerId("1L");
    }

    @Test
    void testGetByOrderId() {

        Notification notification = new Notification();
        notification.setOrderId(1L);

        List<Notification> notifications =
                List.of(notification);

        when(persistencePort.findByOrderId(1L))
                .thenReturn(notifications);

        List<Notification> result =
                service.getByOrderId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(persistencePort, times(1))
                .findByOrderId(1L);
    }
}