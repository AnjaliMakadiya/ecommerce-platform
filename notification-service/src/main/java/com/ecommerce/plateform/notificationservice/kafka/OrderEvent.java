package com.ecommerce.plateform.notificationservice.kafka;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {

    @NotNull
    private Long orderId;

    @NotNull
    private String customerId;

    @NotNull
    private String status;

    private LocalDateTime timestamp;
}
