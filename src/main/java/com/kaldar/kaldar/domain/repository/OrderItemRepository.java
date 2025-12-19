package com.kaldar.kaldar.domain.repository;
import com.kaldar.kaldar.domain.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {}

