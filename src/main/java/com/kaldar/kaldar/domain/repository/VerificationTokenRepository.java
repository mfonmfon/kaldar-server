package com.kaldar.kaldar.domain.repository;

import com.kaldar.kaldar.domain.entities.VerificationToken;
import org.apache.juli.logging.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
}
