package com.kaldar.kaldar.domain.repository;

import com.kaldar.kaldar.domain.entities.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerEntityRepository extends JpaRepository<CustomerEntity, Long> {
    Boolean existsByEmail(String email);

}
