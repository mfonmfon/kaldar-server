package com.kaldar.kaldar.favourite.domain.repository;

import com.kaldar.kaldar.favourite.domain.model.FavouriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavouriteRepository extends JpaRepository<FavouriteEntity, Long> {

    List<FavouriteEntity> findByCustomerId(Long customerId);

    boolean existsByCustomerIdAndDryCleanerId(Long customerId, Long dryCleanerId);

    void deleteByCustomerIdAndDryCleanerId(Long customerId, Long dryCleanerId);
}
