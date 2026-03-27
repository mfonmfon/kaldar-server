package com.kaldar.kaldar.order.domain.repository;

import com.kaldar.kaldar.order.domain.model.OrderServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderServiceItemRepository extends JpaRepository<OrderServiceItem, Long> {
}
