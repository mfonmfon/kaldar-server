package com.kaldar.kaldar.verificationmodule.domain.repository;

import com.kaldar.kaldar.usermdoule.UserEntity;
import com.kaldar.kaldar.verificationmodule.domain.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByUserEntity(UserEntity userEntity);

    Optional<VerificationToken> findByUserEntityAndUsedAtIsNull(UserEntity userEntity);
}
