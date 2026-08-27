package com.ecommerce.plateform.orderservice.persistence.postgres.repository;

import com.ecommerce.plateform.orderservice.persistence.postgres.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByCustomerId(String customerId);
}
