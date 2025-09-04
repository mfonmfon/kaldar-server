package com.kaldar.kaldar.domain.repository;

import com.kaldar.kaldar.domain.entities.CustomerEntity;
import com.kaldar.kaldar.domain.entities.UserEntity;
import com.kaldar.kaldar.domain.entities.VerificationToken;
import org.apache.juli.logging.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByUserEntity(UserEntity userEntity);

    Optional<VerificationToken> findByUserEntityAndUsedAtIsNull(UserEntity userEntity);
}
