package com.ecommerce.plateform.orderservice.persistence.postgres.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Excluded from JSON, toString, and equals/hashCode: without this, Order -> items ->
    // OrderItem -> order -> items -> ... is a circular reference that crashes Jackson
    // serialization (StackOverflowError) the instant any order endpoint is hit, and would
    // also blow up Lombok's generated toString()/equals()/hashCode() the same way.
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Long productId;

    // Snapshot fields: captured from product-service at the moment the order is placed,
    // so the order stays accurate even if the seller later renames/reprices the product.
    @Column(nullable = false)
    private String productName;

    private String productImageUrl;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal subtotal;
}
