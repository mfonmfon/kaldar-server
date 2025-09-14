package com.kaldar.kaldar.domain.repository;

import com.kaldar.kaldar.domain.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderEntityRepository extends JpaRepository<OrderEntity, Long> {

}
