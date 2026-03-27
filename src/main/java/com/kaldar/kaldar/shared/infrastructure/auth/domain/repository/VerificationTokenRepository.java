package com.kaldar.kaldar.shared.infrastructure.auth.domain.repository;

import com.kaldar.kaldar.customer.domain.model.CustomerEntity;
import com.kaldar.kaldar.shared.domain.model.UserEntity;
import com.kaldar.kaldar.shared.infrastructure.auth.domain.model.VerificationToken;
import org.apache.juli.logging.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByUserEntity(UserEntity userEntity);

    Optional<VerificationToken> findByUserEntityAndUsedAtIsNull(UserEntity userEntity);
}
