package com.ecommerce.plateform.orderservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private final RestTemplate restTemplate;

    @Value("${product-service.base-url}")
    private String productServiceBaseUrl;

    /** Fetches a product's current name/price/image/stock. Throws if the product doesn't exist. */
    public ProductInfo getProduct(Long productId) {
        try {
            ProductInfo info = restTemplate.getForObject(
                    productServiceBaseUrl + "/api/products/" + productId, ProductInfo.class);
            if (info == null) {
                throw new IllegalArgumentException("Product not found with id: " + productId);
            }
            return info;
        } catch (RestClientException e) {
            throw new IllegalArgumentException("Product not found or unavailable: " + productId, e);
        }
    }

    /** Best-effort: reduces stock in product-service after the order is saved. */
    public void decrementStock(Long productId, int quantity) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Integer>> entity = new HttpEntity<>(Map.of("quantity", quantity), headers);
        restTemplate.exchange(
                productServiceBaseUrl ,
                HttpMethod.PATCH,
                entity,
                ProductInfo.class
        );
    }
}