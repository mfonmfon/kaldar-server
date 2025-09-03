package com.kaldar.kaldar.domain.repository;

import com.kaldar.kaldar.domain.entities.DryCleanerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DryCleanerEntityRepository extends JpaRepository<DryCleanerEntity, Long> {

    boolean existsByEmail(String email);
}
