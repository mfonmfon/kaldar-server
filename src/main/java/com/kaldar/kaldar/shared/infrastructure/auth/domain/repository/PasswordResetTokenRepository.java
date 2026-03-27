package com.kaldar.kaldar.shared.infrastructure.auth.domain.repository;

import com.kaldar.kaldar.shared.infrastructure.auth.domain.model.PasswordResetToken;
import com.kaldar.kaldar.shared.domain.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByUserEntityAndUsedAtIsNull(UserEntity userEntity);
}

