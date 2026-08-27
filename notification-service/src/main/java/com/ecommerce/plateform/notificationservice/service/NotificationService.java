package com.ecommerce.plateform.notificationservice.service;

import com.ecommerce.plateform.notificationservice.kafka.OrderEvent;
import com.ecommerce.plateform.notificationservice.persistance.port.NotificationPersistencePort;
import com.ecommerce.plateform.notificationservice.persistance.postgres.entity.Notification;
import com.ecommerce.plateform.notificationservice.persistance.postgres.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

//    private final NotificationRepository repository;

    private final NotificationPersistencePort notificationPersistencePort;

    public List<Notification> getAllNotifications() {
        System.out.println("get notifications");
        List<Notification> all = notificationPersistencePort.findAll();
        System.out.println(all.size());

        return all;
    }

    public void createNotification(OrderEvent event) {

        Notification n = new Notification();
        n.setCustomerId(event.getCustomerId());
        n.setOrderId(event.getOrderId());
        n.setMessage("Your order " + event.getOrderId()
                + " is now " + event.getStatus());

        n.setCreatedAt(LocalDateTime.now());

        notificationPersistencePort.save(n);
    }

    public List<Notification> getByCustomerId(String customerId) {

        return notificationPersistencePort.findByCustomerId(customerId);
    }

    public List<Notification> getByOrderId(Long orderId) {

        return notificationPersistencePort.findByOrderId(orderId);
    }
}
