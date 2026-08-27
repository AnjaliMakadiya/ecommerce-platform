package com.ecommerce.plateform.orderservice.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent {

    private Long orderId;
    private String customerId;
    private String status;
    private LocalDateTime timestamp;

}
