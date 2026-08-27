package com.ecommerce.plateform.notificationservice.persistance.mongo.mapper;

import com.ecommerce.plateform.notificationservice.persistance.mongo.entity.NotificationDocument;
import com.ecommerce.plateform.notificationservice.persistance.postgres.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class MongoNotificationMapper {

    public NotificationDocument toDocumentEntity(Notification notification) {

        NotificationDocument document = new NotificationDocument();

        document.setNotificationId(notification.getNotificationId());
        document.setOrderId(notification.getOrderId());
        document.setCustomerId(notification.getCustomerId());
        document.setMessage(notification.getMessage());
        document.setCreatedAt(notification.getCreatedAt());

        return document;
    }

    public Notification toNotificationEntity(NotificationDocument document) {

        Notification notification = new Notification();

        notification.setNotificationId(document.getNotificationId());
        notification.setOrderId(document.getOrderId());
        notification.setCustomerId(document.getCustomerId());
        notification.setMessage(document.getMessage());
        notification.setCreatedAt(document.getCreatedAt());

        return notification;
    }
}
