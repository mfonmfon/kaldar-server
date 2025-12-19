package com.kaldar.kaldar.customermdoule.domain.repository;

import com.kaldar.kaldar.customermdoule.domain.model.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerEntityRepository extends JpaRepository<CustomerEntity, Long> {
    Boolean existsByEmail(String email);
   Optional<CustomerEntity> findByEmail(String email);

}
