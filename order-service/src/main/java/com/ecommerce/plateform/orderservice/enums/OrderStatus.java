package com.ecommerce.plateform.orderservice.enums;


import lombok.Data;


public enum OrderStatus {
    CREATED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
