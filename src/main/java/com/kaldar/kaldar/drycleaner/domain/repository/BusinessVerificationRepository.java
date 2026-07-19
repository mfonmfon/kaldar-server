package com.kaldar.kaldar.drycleaner.domain.repository;

import com.kaldar.kaldar.drycleaner.domain.model.BusinessVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessVerificationRepository extends JpaRepository<BusinessVerification, Long> {
    
    Optional<BusinessVerification> findByDryCleanerId(Long dryCleanerId);
    
    boolean existsByRegistrationNumber(String registrationNumber);
    
    boolean existsByTaxIdentificationNumber(String taxIdentificationNumber);
}
