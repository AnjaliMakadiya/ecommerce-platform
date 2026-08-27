package com.ecommerce.plateform.orderservice.dto;

import com.ecommerce.plateform.orderservice.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {

    @NotNull
    private OrderStatus status;
}
