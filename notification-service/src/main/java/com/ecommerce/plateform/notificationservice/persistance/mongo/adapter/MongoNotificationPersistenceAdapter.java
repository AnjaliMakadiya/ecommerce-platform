package com.ecommerce.plateform.notificationservice.persistance.mongo.adapter;

import com.ecommerce.plateform.notificationservice.persistance.mongo.entity.NotificationDocument;
import com.ecommerce.plateform.notificationservice.persistance.mongo.mapper.MongoNotificationMapper;
import com.ecommerce.plateform.notificationservice.persistance.mongo.repository.MongoNotificationRepository;
import com.ecommerce.plateform.notificationservice.persistance.mongo.service.SequenceGeneratorService;
import com.ecommerce.plateform.notificationservice.persistance.port.NotificationPersistencePort;
import com.ecommerce.plateform.notificationservice.persistance.postgres.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Profile("mongo")
public class MongoNotificationPersistenceAdapter implements NotificationPersistencePort {


    private final MongoNotificationRepository mongoNotificationRepository;
    private final MongoNotificationMapper mongoNotificationMapper;
    private final SequenceGeneratorService sequenceGenerator;

    @Override
    public Notification save(Notification notification) {

        NotificationDocument document;

        // CREATE
        if (notification.getNotificationId() == null) {

            notification.setNotificationId(
                    sequenceGenerator.generateSequence("notification_sequence")
            );

            notification.setCreatedAt(LocalDateTime.now());

            document = mongoNotificationMapper.toDocumentEntity(notification);

        } else {

            // UPDATE
            NotificationDocument existing = mongoNotificationRepository
                    .findByNotificationId(notification.getNotificationId())
                    .orElseThrow(() -> new RuntimeException("Notification not found"));

            notification.setCreatedAt(existing.getCreatedAt());

            document = mongoNotificationMapper.toDocumentEntity(notification);

            // Preserve MongoDB _id
            document.setId(existing.getId());
        }

        NotificationDocument savedDocument = mongoNotificationRepository.save(document);

        return mongoNotificationMapper.toNotificationEntity(savedDocument);
    }



    @Override
    public List<Notification> findAll() {
        return mongoNotificationRepository.findAll()
                .stream()
                .map(mongoNotificationMapper::toNotificationEntity)
                .toList();
    }

    @Override
    public List<Notification> findByCustomerId(String customerId) {
        return mongoNotificationRepository.findByCustomerId(customerId)
                .stream()
                .map(mongoNotificationMapper::toNotificationEntity)
                .toList();
    }

    @Override
    public List<Notification> findByOrderId(Long orderId) {
        return mongoNotificationRepository.findByOrderId(orderId)
                .stream()
                .map(mongoNotificationMapper::toNotificationEntity)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        mongoNotificationRepository.deleteByNotificationId(id);
    }
}