package com.ecommerce.plateform.orderservice.persistence.mongo.repository;

import com.ecommerce.plateform.orderservice.persistence.mongo.entity.OrderDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoOrderRepository extends MongoRepository<OrderDocument, String> {

    Optional<OrderDocument> findByOrderId(Long orderId);

    void deleteByOrderId(Long orderId);

    Optional<OrderDocument> findByCustomerId(String orderId);
}
