package com.ecommerce.plateform.productservice.dto;

import com.ecommerce.plateform.productservice.enums.ProductStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateProductStatusRequest {

    @NotNull
    private ProductStatus status;
}
