package com.kaldar.kaldar.payment.domain.repository;

import com.kaldar.kaldar.payment.domain.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByReference(String reference);

    List<PaymentTransaction> findByUserId(Long userId);
}
