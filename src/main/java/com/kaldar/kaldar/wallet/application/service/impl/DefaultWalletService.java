package com.kaldar.kaldar.wallet.application.service.impl;

import com.kaldar.kaldar.shared.domain.exceptions.InsufficientWalletBalanceException;
import com.kaldar.kaldar.shared.domain.exceptions.InvalidWalletAmountException;
import com.kaldar.kaldar.shared.domain.exceptions.UserNotFoundException;
import com.kaldar.kaldar.shared.domain.model.UserEntity;
import com.kaldar.kaldar.shared.domain.repository.UserEntityRepository;
import com.kaldar.kaldar.wallet.application.dto.WalletBalanceResponse;
import com.kaldar.kaldar.wallet.application.dto.WalletCreditRequest;
import com.kaldar.kaldar.wallet.application.dto.WalletDebitRequest;
import com.kaldar.kaldar.wallet.application.dto.WalletSummaryResponse;
import com.kaldar.kaldar.wallet.application.dto.WalletTransactionDto;
import com.kaldar.kaldar.wallet.application.service.WalletService;
import com.kaldar.kaldar.wallet.domain.model.Wallet;
import com.kaldar.kaldar.wallet.domain.model.WalletTransaction;
import com.kaldar.kaldar.wallet.domain.repository.WalletRepository;
import com.kaldar.kaldar.wallet.domain.repository.WalletTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultWalletService implements WalletService {

    private static final String CURRENCY = "NGN";

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final UserEntityRepository userEntityRepository;

    public DefaultWalletService(WalletRepository walletRepository,
                                WalletTransactionRepository transactionRepository,
                                UserEntityRepository userEntityRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.userEntityRepository = userEntityRepository;
    }

    /**
     * Finds the wallet for the given user, or creates one with a zero balance if it
     * does not exist yet (lazy initialisation pattern).
     */
    private Wallet getOrCreateWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserEntity user = userEntityRepository.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
                    Wallet newWallet = new Wallet(user, BigDecimal.ZERO);
                    return walletRepository.save(newWallet);
                });
    }

    @Override
    @Transactional
    public void creditWallet(WalletCreditRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidWalletAmountException("Credit amount must be greater than zero");
        }

        Wallet wallet = getOrCreateWallet(request.getUserId());
        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction(
                wallet,
                request.getAmount(),
                "CREDIT",
                request.getDescription(),
                request.getReference()
        );
        transactionRepository.save(tx);
    }

    @Override
    @Transactional
    public void debitWallet(WalletDebitRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidWalletAmountException("Debit amount must be greater than zero");
        }

        Wallet wallet = getOrCreateWallet(request.getUserId());
        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientWalletBalanceException("Insufficient wallet balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction(
                wallet,
                request.getAmount(),
                "DEBIT",
                request.getDescription(),
                request.getReference()
        );
        transactionRepository.save(tx);
    }

    @Override
    @Transactional(readOnly = true)
    public WalletBalanceResponse getWalletBalance(Long userId) {
        Wallet wallet = getOrCreateWallet(userId);
        return new WalletBalanceResponse(wallet.getBalance(), CURRENCY);
    }

    @Override
    @Transactional(readOnly = true)
    public WalletSummaryResponse getWalletSummary(Long userId) {
        Wallet wallet = getOrCreateWallet(userId);

        List<WalletTransactionDto> history = transactionRepository
                .findByWalletIdOrderByCreatedAtDesc(wallet.getId())
                .stream()
                .map(tx -> new WalletTransactionDto(
                        tx.getId(),
                        tx.getAmount(),
                        tx.getType(),
                        tx.getDescription(),
                        tx.getReference(),
                        tx.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new WalletSummaryResponse(wallet.getBalance(), history);
    }
}
