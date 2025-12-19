package com.kaldar.kaldar.domain.repository;
import com.kaldar.kaldar.domain.entities.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {
   Optional<ServiceOffering> findByIdAndDryCleanerId(ServiceOffering serviceOffering, Long dryCleanerId);



}
