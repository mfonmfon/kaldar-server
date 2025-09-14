package com.kaldar.kaldar.domain.repository;

import com.kaldar.kaldar.domain.entities.ServiceOfferings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceOfferingRepository extends JpaRepository<ServiceOfferings, Long> {
}
