package com.ecommerce.plateform.orderservice.persistence.mongo.entity;

import com.ecommerce.plateform.orderservice.enums.OrderStatus;
import org.springframework.data.annotation.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class OrderDocument {


    @Id
    private String id;          // MongoDB internal _id

    private Long orderId;       // Business Order ID

    @NotBlank
    private String customerId;

    @NotNull
    private OrderStatus status;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
