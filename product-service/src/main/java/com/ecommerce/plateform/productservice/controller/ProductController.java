package com.ecommerce.plateform.productservice.controller;

import com.ecommerce.plateform.productservice.dto.CreateProductRequest;
import com.ecommerce.plateform.productservice.dto.DecrementStockRequest;
import com.ecommerce.plateform.productservice.dto.UpdateProductRequest;
import com.ecommerce.plateform.productservice.dto.UpdateProductStatusRequest;
import com.ecommerce.plateform.productservice.entity.Product;
import com.ecommerce.plateform.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ---------- Public browsing (customers, guests, everyone) ----------

    @GetMapping
    public Page<Product> browse(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return productService.browse(categoryId, keyword, pageable);
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return productService.getActiveById(id);
    }

    // ---------- Seller (OPERATOR) ----------

    @PreAuthorize("hasRole('OPERATOR')")
    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('OPERATOR')")
    @GetMapping("/my")
    public List<Product> myProducts() {
        return productService.getMyProducts();
    }

    // ---------- Admin moderation ----------

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public Page<Product> allForAdmin(@PageableDefault(size = 20) Pageable pageable) {
        return productService.getAllForAdmin(pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public Product updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateProductStatusRequest request) {
        return productService.updateStatus(id, request);
    }

    // ---------- Internal, service-to-service only ----------
    // Called by order-service right after an order is placed to reduce stock.
    // NOTE: this endpoint is intentionally left open (see SecurityConfig) rather than
    // requiring a customer/operator JWT, since the caller is order-service itself, not
    // an end user. In a production setup this should be locked down with a service-to-
    // service credential (mTLS, internal API key, or a service-account JWT) rather than
    // being fully open — flagging this as a follow-up hardening item.
    @PatchMapping("/{id}/decrement-stock")
    public Product decrementStock(@PathVariable Long id, @Valid @RequestBody DecrementStockRequest request) {
        return productService.decrementStock(id, request.getQuantity());
    }
}
