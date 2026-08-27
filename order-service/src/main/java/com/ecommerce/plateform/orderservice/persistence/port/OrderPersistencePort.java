package com.ecommerce.plateform.orderservice.persistence.port;

import com.ecommerce.plateform.orderservice.persistence.postgres.entity.Order;

import java.util.List;
import java.util.Optional;


public interface OrderPersistencePort {

    Order save(Order order);

    Optional<Order> findById(Long id);

    List<Order> findAll();

    void deleteById(Long id);

    List<Order> findByCustomerId(String customerId);

}
