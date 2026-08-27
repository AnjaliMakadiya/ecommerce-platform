package com.ecommerce.plateform.notificationservice.persistance.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class NotificationDocument {

    @Id
    private String id;      // MongoDB _id

    private Long notificationId;   // Business ID

    private Long orderId;

    private String customerId;

    private String message;

    @CreatedDate
    private LocalDateTime createdAt;

}
