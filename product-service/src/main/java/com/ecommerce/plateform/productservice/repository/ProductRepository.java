package com.ecommerce.plateform.productservice.repository;

import com.ecommerce.plateform.productservice.entity.Product;
import com.ecommerce.plateform.productservice.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    Page<Product> findByStatusAndCategoryId(ProductStatus status, Long categoryId, Pageable pageable);

    Page<Product> findByStatusAndNameContainingIgnoreCase(ProductStatus status, String keyword, Pageable pageable);

    Page<Product> findByStatusAndCategoryIdAndNameContainingIgnoreCase(
            ProductStatus status, Long categoryId, String keyword, Pageable pageable);

    List<Product> findBySellerId(Long sellerId);
}
