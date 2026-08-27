package com.ecommerce.plateform.orderservice.client;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Mirrors the subset of product-service's Product fields order-service needs.
 * Deliberately loose (no @JsonIgnoreProperties issues) - only fields declared here
 * are read; everything else in the JSON response from product-service is ignored.
 */
@Data
public class ProductInfo {
    private Long id;
    private String name;
    private BigDecimal price;
    private String imageUrl;
    private Integer stock;
}
