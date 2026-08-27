package com.ecommerce.plateform.notificationservice.persistance.mongo.repository;

import com.ecommerce.plateform.notificationservice.persistance.mongo.entity.NotificationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MongoNotificationRepository extends MongoRepository<NotificationDocument, String> {

    Optional<NotificationDocument> findByNotificationId(Long notificationId);

    List<NotificationDocument> findByCustomerId(String customerId);

    List<NotificationDocument> findByOrderId(Long orderId);

    void deleteByNotificationId(Long notificationId);
}
