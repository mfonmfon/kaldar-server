package com.kaldar.kaldar.customer.domain.repository;

import com.kaldar.kaldar.customer.domain.model.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerEntityRepository extends JpaRepository<CustomerEntity, Long> {
    Boolean existsByEmail(String email);

   Optional<CustomerEntity> findByEmail(String email);

}
