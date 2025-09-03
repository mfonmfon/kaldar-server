package com.kaldar.kaldar.domain.repository;

import com.kaldar.kaldar.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
}
