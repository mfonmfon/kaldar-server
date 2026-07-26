package com.kaldar.kaldar.wallet.domain.repository;

import com.kaldar.kaldar.wallet.domain.model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);

    List<WalletTransaction> findTop50ByWalletIdOrderByCreatedAtDesc(Long walletId);

    boolean existsByReference(String reference);
}
