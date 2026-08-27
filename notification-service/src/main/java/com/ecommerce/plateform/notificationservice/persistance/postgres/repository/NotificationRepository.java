package com.ecommerce.plateform.notificationservice.persistance.postgres.repository;

import com.ecommerce.plateform.notificationservice.persistance.postgres.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {

    List<Notification> findByCustomerId(String customerId);
    List<Notification> findByOrderId(Long OrderId);
}
