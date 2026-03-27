package com.kaldar.kaldar.drycleaner.domain.repository;

import com.kaldar.kaldar.drycleaner.domain.model.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {
//    ServiceOffering findByDryCleanerIdAndClothesType(Long dryCleanerId, String clothType);


}
