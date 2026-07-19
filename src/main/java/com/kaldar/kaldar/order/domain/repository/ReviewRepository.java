package com.kaldar.kaldar.order.domain.repository;

import com.kaldar.kaldar.order.domain.model.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    Optional<ReviewEntity> findByOrderId(Long orderId);
    List<ReviewEntity> findByDryCleanerIdOrderByCreatedAtDesc(Long dryCleanerId);
}
