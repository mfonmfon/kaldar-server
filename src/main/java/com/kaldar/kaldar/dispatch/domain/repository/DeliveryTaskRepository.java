package com.kaldar.kaldar.dispatch.domain.repository;

import com.kaldar.kaldar.dispatch.domain.model.DeliveryTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryTaskRepository extends JpaRepository<DeliveryTask, Long> {
    List<DeliveryTask> findByOrderId(Long orderId);
    Optional<DeliveryTask> findByProviderAndExternalDeliveryId(String provider, String externalDeliveryId);
}
