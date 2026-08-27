package com.ecommerce.plateform.orderservice.persistence.postgres.adapter;

import com.ecommerce.plateform.orderservice.persistence.port.OrderPersistencePort;
import com.ecommerce.plateform.orderservice.persistence.postgres.entity.Order;
import com.ecommerce.plateform.orderservice.persistence.postgres.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Profile("postgres")
public class PostgresOrderPersistenceAdapter implements OrderPersistencePort {

    private final OrderRepository orderRepository;


    @Override
    public Order save(Order order) {

        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }


    @Override
    public List<Order> findByCustomerId(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
}
