package com.ecommerce.plateform.productservice.seed;

import com.ecommerce.plateform.productservice.entity.Category;
import com.ecommerce.plateform.productservice.entity.Product;
import com.ecommerce.plateform.productservice.enums.ProductStatus;
import com.ecommerce.plateform.productservice.repository.CategoryRepository;
import com.ecommerce.plateform.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Seeds sample categories + products so the catalog has something to display
 * before real sellers (OPERATORs) have added their own products.
 *
 * Set app.seed.enabled=false in application.yaml to skip this (e.g. in production).
 * sellerId=1 is a placeholder; replace with a real OPERATOR user id from order-service's
 * users table once you have one, or leave it -- it doesn't need to match a real user for
 * display/testing purposes, only for "my products" ownership checks on that specific seller.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Value("${app.seed.enabled:true}")
    private boolean seedEnabled;

    private static final Long PLACEHOLDER_SELLER_ID = 1L;

    @Override
    public void run(String... args) {
        if (!seedEnabled) return;
        if (productRepository.count() > 0) return; // already seeded

        Map<String, Category> categories = seedCategories();
        seedProducts(categories);

        System.out.println("Product-service: dummy data seeded ("
                + categories.size() + " categories, "
                + productRepository.count() + " products).");
    }

    private Map<String, Category> seedCategories() {
        List<String> names = List.of("Electronics", "Fashion", "Home & Kitchen", "Books", "Sports & Outdoors", "Beauty & Health");

        Map<String, Category> saved = new LinkedHashMap<>();
        for (String name : names) {
            Category c = categoryRepository.findByNameIgnoreCase(name).orElseGet(() -> {
                Category category = new Category();
                category.setName(name);
                category.setDescription(name + " products");
                return categoryRepository.save(category);
            });
            saved.put(name, c);
        }
        return saved;
    }

    private void seedProducts(Map<String, Category> categories) {
        List<Object[]> products = List.of(
                // name, price, stock, category, image keyword
                new Object[]{"Wireless Bluetooth Headphones", "2499.00", 50, "Electronics"},
                new Object[]{"Smartphone 128GB", "18999.00", 30, "Electronics"},
                new Object[]{"Smart Watch Fitness Tracker", "3499.00", 40, "Electronics"},
                new Object[]{"27-inch 4K Monitor", "22999.00", 15, "Electronics"},
                new Object[]{"Portable Power Bank 20000mAh", "1299.00", 100, "Electronics"},
                new Object[]{"Mechanical Keyboard RGB", "3999.00", 25, "Electronics"},

                new Object[]{"Men's Cotton T-Shirt", "499.00", 200, "Fashion"},
                new Object[]{"Women's Denim Jacket", "1899.00", 60, "Fashion"},
                new Object[]{"Running Shoes", "2999.00", 80, "Fashion"},
                new Object[]{"Leather Wallet", "899.00", 120, "Fashion"},
                new Object[]{"Sunglasses UV Protection", "799.00", 90, "Fashion"},

                new Object[]{"Non-stick Cookware Set", "3299.00", 35, "Home & Kitchen"},
                new Object[]{"Electric Kettle 1.5L", "1099.00", 70, "Home & Kitchen"},
                new Object[]{"Memory Foam Pillow", "999.00", 100, "Home & Kitchen"},
                new Object[]{"LED Table Lamp", "699.00", 85, "Home & Kitchen"},
                new Object[]{"Vacuum Cleaner", "4999.00", 20, "Home & Kitchen"},

                new Object[]{"The Pragmatic Programmer", "899.00", 40, "Books"},
                new Object[]{"Atomic Habits", "499.00", 150, "Books"},
                new Object[]{"Clean Code", "1099.00", 45, "Books"},
                new Object[]{"Spring Boot in Action", "1299.00", 30, "Books"},

                new Object[]{"Yoga Mat", "699.00", 90, "Sports & Outdoors"},
                new Object[]{"Adjustable Dumbbell Set", "4499.00", 25, "Sports & Outdoors"},
                new Object[]{"Camping Tent 4-Person", "5999.00", 15, "Sports & Outdoors"},

                new Object[]{"Vitamin C Serum", "599.00", 110, "Beauty & Health"},
                new Object[]{"Electric Toothbrush", "1799.00", 55, "Beauty & Health"}
        );

        for (Object[] p : products) {
            String name = (String) p[0];
            BigDecimal price = new BigDecimal((String) p[1]);
            Integer stock = (Integer) p[2];
            String categoryName = (String) p[3];

            Product product = new Product();
            product.setName(name);
            product.setDescription("High quality " + name.toLowerCase() + ". Sample seeded product for demo purposes.");
            product.setPrice(price);
            product.setStock(stock);
            product.setImageUrl("https://placehold.co/400x400?text=" + name.replace(" ", "+"));
            product.setSellerId(PLACEHOLDER_SELLER_ID);
            product.setCategory(categories.get(categoryName));
            product.setStatus(ProductStatus.ACTIVE);

            productRepository.save(product);
        }
    }
}
