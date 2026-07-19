package com.kaldar.kaldar.order.domain.repository;

import com.kaldar.kaldar.order.domain.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderEntityRepository extends JpaRepository<OrderEntity, Long> {

    java.util.List<OrderEntity> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    java.util.List<OrderEntity> findByDryCleanerIdOrderByCreatedAtDesc(Long dryCleanerId);
}
