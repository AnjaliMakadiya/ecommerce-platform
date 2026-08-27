package com.ecommerce.plateform.productservice.service;

import com.ecommerce.plateform.productservice.dto.CreateProductRequest;
import com.ecommerce.plateform.productservice.dto.UpdateProductRequest;
import com.ecommerce.plateform.productservice.dto.UpdateProductStatusRequest;
import com.ecommerce.plateform.productservice.entity.Category;
import com.ecommerce.plateform.productservice.entity.Product;
import com.ecommerce.plateform.productservice.enums.ProductStatus;
import com.ecommerce.plateform.productservice.exception.ResourceNotFoundException;
import com.ecommerce.plateform.productservice.repository.CategoryRepository;
import com.ecommerce.plateform.productservice.repository.ProductRepository;
import com.ecommerce.plateform.productservice.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    /** OPERATOR creates a product they own. */
    public Product createProduct(CreateProductRequest request) {
        Long sellerId = SecurityUtils.getCurrentUserId();

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        product.setSellerId(sellerId);
        product.setStatus(ProductStatus.ACTIVE);

        return productRepository.save(product);
    }

    /** OPERATOR can only update their own product; ADMIN can update any. **/
    public Product updateProduct(Long id, UpdateProductRequest request) {
        Product product = getRawById(id);
        System.out.println(product.getId()+" "+product.getSellerId()+" "+SecurityUtils.getCurrentUserId());
        assertOwnerOrAdmin(product);

        if (StringUtils.hasText(request.getName())) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getStock() != null) product.setStock(request.getStock());
        if (request.getImageUrl() != null) product.setImageUrl(request.getImageUrl());
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }

        return productRepository.save(product);
    }

    /** OPERATOR can only delete their own product; ADMIN can delete any. */
    public void deleteProduct(Long id) {
        Product product = getRawById(id);
        assertOwnerOrAdmin(product);
        productRepository.delete(product);
    }

    /** ADMIN-only: enable/disable any product (moderation). */
    public Product updateStatus(Long id, UpdateProductStatusRequest request) {
        Product product = getRawById(id);
        product.setStatus(request.getStatus());
        return productRepository.save(product);
    }

    /**
     * Internal, service-to-service call (order-service -> product-service) made right after an
     * order is placed. Not exposed to end users. Throws if stock is insufficient so order-service
     * can reject the order before it's finalized.
     */
    public Product decrementStock(Long id, int quantity) {
        Product product = getRawById(id);
        if (product.getStock() < quantity) {
            throw new IllegalStateException(
                    "Insufficient stock for product " + id + ": have " + product.getStock() + ", need " + quantity);
        }
        product.setStock(product.getStock() - quantity);
        System.out.println("Decremented stock for product " + id + ": new stock is " + product.getStock());
        return productRepository.save(product);
    }


    /** Public browsing: only ACTIVE products, paginated, optional category + keyword filters. */
    public Page<Product> browse(Long categoryId, String keyword, Pageable pageable) {
        boolean hasCategory = categoryId != null;
        boolean hasKeyword = StringUtils.hasText(keyword);

        if (hasCategory && hasKeyword) {
            return productRepository.findByStatusAndCategoryIdAndNameContainingIgnoreCase(
                    ProductStatus.ACTIVE, categoryId, keyword, pageable);
        }
        if (hasCategory) {
            return productRepository.findByStatusAndCategoryId(ProductStatus.ACTIVE, categoryId, pageable);
        }
        if (hasKeyword) {
            return productRepository.findByStatusAndNameContainingIgnoreCase(ProductStatus.ACTIVE, keyword, pageable);
        }
        return productRepository.findByStatus(ProductStatus.ACTIVE, pageable);
    }

    /** Public: get a single ACTIVE product by id. */
    public Product getActiveById(Long id) {
        Product product = getRawById(id);
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        return product;
    }

    /** ADMIN: see every product regardless of status. */
    public Page<Product> getAllForAdmin(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    /** OPERATOR: their own products regardless of status. */
    public List<Product> getMyProducts() {
        Long sellerId = SecurityUtils.getCurrentUserId();
        return productRepository.findBySellerId(sellerId);
    }

    private Product getRawById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private void assertOwnerOrAdmin(Product product) {
        String role = SecurityUtils.getCurrentRole();
        if ("ADMIN".equals(role)) return;
        System.out.println("Role "+role);
        System.out.println("SellerId "+product.getSellerId());
        System.out.println("CurrentUserId "+SecurityUtils.getCurrentUserId());

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!product.getSellerId().equals(currentUserId)) {
            throw new AccessDeniedException("You do not own this product");
        }
    }
}
