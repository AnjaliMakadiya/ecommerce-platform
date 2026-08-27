package com.ecommerce.plateform.orderservice.repository;

import com.ecommerce.plateform.orderservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);

    //TODO UPDATED ON 6-AUGUST
    Optional<User> findByEmail(String email);

    Optional<User> findByKeycloakId(String keycloakId);
}
