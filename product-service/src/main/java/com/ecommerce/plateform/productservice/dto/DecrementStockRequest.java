package com.ecommerce.plateform.productservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DecrementStockRequest {

    @NotNull
    @Min(1)
    private Integer quantity;
}
