package com.kaldar.kaldar.drycleaner.domain.repository;

import com.kaldar.kaldar.drycleaner.domain.model.DryCleanerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DryCleanerEntityRepository extends JpaRepository<DryCleanerEntity, Long> {

    boolean existsByEmail(String email);

    Page<DryCleanerEntity> findByIsActiveTrueAndVerifiedUserTrue(Pageable pageable);
}
