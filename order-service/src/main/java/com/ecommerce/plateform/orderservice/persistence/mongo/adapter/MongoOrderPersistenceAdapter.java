package com.ecommerce.plateform.orderservice.persistence.mongo.adapter;

import com.ecommerce.plateform.orderservice.persistence.mongo.entity.OrderDocument;
import com.ecommerce.plateform.orderservice.persistence.mongo.mapper.MongoOrderMapper;
import com.ecommerce.plateform.orderservice.persistence.mongo.repository.MongoOrderRepository;
import com.ecommerce.plateform.orderservice.persistence.mongo.service.SequenceGeneratorService;
import com.ecommerce.plateform.orderservice.persistence.port.OrderPersistencePort;
import com.ecommerce.plateform.orderservice.persistence.postgres.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Profile("mongo")
public class MongoOrderPersistenceAdapter implements OrderPersistencePort {

    private final MongoOrderRepository mongoOrderRepository;
    private final MongoOrderMapper mongoOrderMapper;
    private final SequenceGeneratorService sequenceGenerator;

    @Override
    public Order save(Order order) {

        OrderDocument document;

        if (order.getOrderId() == null) {

            // CREATE
            order.setOrderId(
                    sequenceGenerator.generateSequence("order_sequence")
            );

            order.setCreatedAt(java.time.LocalDateTime.now());
            order.setUpdatedAt(java.time.LocalDateTime.now());

            document = mongoOrderMapper.toDocumentEntity(order);

        } else {

            // UPDATE
            OrderDocument existingDocument = mongoOrderRepository
                    .findByOrderId(order.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            order.setCreatedAt(existingDocument.getCreatedAt());
            order.setUpdatedAt(java.time.LocalDateTime.now());

            document = mongoOrderMapper.toDocumentEntity(order);

            // VERY IMPORTANT
            document.setId(existingDocument.getId());
        }

        OrderDocument savedDocument = mongoOrderRepository.save(document);

        return mongoOrderMapper.toOrderEntity(savedDocument);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return mongoOrderRepository.findByOrderId(id).map(mongoOrderMapper::toOrderEntity);
    }

    @Override
    public List<Order> findAll() {
        return mongoOrderRepository.findAll().stream()
                .map(mongoOrderMapper::toOrderEntity)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        mongoOrderRepository.deleteByOrderId(id);
    }

    @Override
    public List<Order> findByCustomerId(String customerId) {
        return mongoOrderRepository.findByCustomerId(customerId).stream()
                .map(mongoOrderMapper::toOrderEntity)
                .toList();
    }
}
