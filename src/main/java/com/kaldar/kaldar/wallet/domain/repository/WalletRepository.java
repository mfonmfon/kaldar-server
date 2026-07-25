package com.kaldar.kaldar.wallet.domain.repository;

import com.kaldar.kaldar.wallet.domain.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUserId(Long userId);

    /** Used by the Anchor webhook handler to locate a wallet by virtual account number. */
    Optional<Wallet> findByVirtualAccountNumber(String virtualAccountNumber);

    /** Fallback lookup by Anchor's internal sub-account ID. */
    Optional<Wallet> findByAnchorAccountId(String anchorAccountId);
}
