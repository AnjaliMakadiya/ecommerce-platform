package com.ecommerce.plateform.notificationservice.persistance.port;

import com.ecommerce.plateform.notificationservice.persistance.postgres.entity.Notification;
import org.springframework.stereotype.Component;

import java.util.List;


public interface NotificationPersistencePort {

    Notification save(Notification notification);

    List<Notification> findAll();

    List<Notification> findByCustomerId(String customerId);

    List<Notification> findByOrderId(Long orderId);

    void deleteById(Long id);
}
