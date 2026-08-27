package com.ecommerce.plateform.notificationservice.persistance.postgres.adapter;

import com.ecommerce.plateform.notificationservice.persistance.port.NotificationPersistencePort;
import com.ecommerce.plateform.notificationservice.persistance.postgres.entity.Notification;
import com.ecommerce.plateform.notificationservice.persistance.postgres.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("postgres")
public class PostgresNotificationPersistenceAdapter implements NotificationPersistencePort {

    private final NotificationRepository notificationRepository;


    @Override
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> findByCustomerId(String customerId) {
        return notificationRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Notification> findByOrderId(Long orderId) {
        return notificationRepository.findByOrderId(orderId);
    }

    @Override
    public void deleteById(Long id) {
        notificationRepository.deleteById(id);
    }
}
