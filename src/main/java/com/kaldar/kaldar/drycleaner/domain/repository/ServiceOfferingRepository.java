package com.kaldar.kaldar.drycleaner.domain.repository;

import com.kaldar.kaldar.drycleaner.domain.model.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {
    java.util.List<ServiceOffering> findByDryCleanerId(Long dryCleanerId);
    java.util.Optional<ServiceOffering> findByDryCleanerIdAndServiceName(Long dryCleanerId, String serviceName);
}
