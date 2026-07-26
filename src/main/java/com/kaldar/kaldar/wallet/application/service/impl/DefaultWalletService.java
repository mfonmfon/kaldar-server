package com.kaldar.kaldar.wallet.application.service.impl;

import com.kaldar.kaldar.shared.domain.exceptions.InsufficientWalletBalanceException;
import com.kaldar.kaldar.shared.domain.exceptions.InvalidWalletAmountException;
import com.kaldar.kaldar.shared.domain.exceptions.UserNotFoundException;
import com.kaldar.kaldar.shared.domain.model.UserEntity;
import com.kaldar.kaldar.shared.domain.repository.UserEntityRepository;
import com.kaldar.kaldar.shared.infrastructure.anchor.AnchorClient;
import com.kaldar.kaldar.shared.infrastructure.anchor.dto.AnchorSubAccountRequest;
import com.kaldar.kaldar.shared.infrastructure.anchor.dto.AnchorSubAccountResponse;
import com.kaldar.kaldar.wallet.application.dto.WalletBalanceResponse;
import com.kaldar.kaldar.wallet.application.dto.WalletCreditRequest;
import com.kaldar.kaldar.wallet.application.dto.WalletDebitRequest;
import com.kaldar.kaldar.wallet.application.dto.WalletSummaryResponse;
import com.kaldar.kaldar.wallet.application.dto.WalletTransactionDto;
import com.kaldar.kaldar.wallet.application.service.WalletService;
import com.kaldar.kaldar.wallet.domain.model.Wallet;
import com.kaldar.kaldar.wallet.domain.model.WalletTransaction;
import com.kaldar.kaldar.wallet.domain.model.WalletTransactionType;
import com.kaldar.kaldar.wallet.domain.repository.WalletRepository;
import com.kaldar.kaldar.wallet.domain.repository.WalletTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DefaultWalletService implements WalletService {

    private static final Logger log = LoggerFactory.getLogger(DefaultWalletService.class);
    private static final String CURRENCY = "NGN";
    private static final String FALLBACK_PHONE = "+2340000000000";

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final UserEntityRepository userEntityRepository;
    private final AnchorClient anchorClient;

    public DefaultWalletService(WalletRepository walletRepository,
                                WalletTransactionRepository transactionRepository,
                                UserEntityRepository userEntityRepository,
                                AnchorClient anchorClient) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.userEntityRepository = userEntityRepository;
        this.anchorClient = anchorClient;
    }

    @Override
    public void createVirtualAccountForUser(Long userId) {
        Optional<Wallet> existingWallet = walletRepository.findByUserId(userId);
        if (existingWallet.isPresent() && hasVirtualAccount(existingWallet.get())) {
            log.info("Virtual account already exists for user {}", userId);
            return;
        }

        UserEntity user = userEntityRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        AnchorSubAccountRequest request = new AnchorSubAccountRequest(
                resolveFullName(user),
                user.getEmail(),
                user.getPhoneNumber() != null ? user.getPhoneNumber() : FALLBACK_PHONE);

        AnchorSubAccountResponse account = anchorClient.createSubAccount(request);

        saveVirtualAccountDetails(userId, account);
    }

    @Transactional
    public void saveVirtualAccountDetails(Long userId, AnchorSubAccountResponse account) {
        Wallet wallet = getOrCreateWalletForUpdate(userId);
        wallet.setAnchorAccountId(account.getAccountId());
        wallet.setVirtualAccountNumber(account.getAccountNumber());
        wallet.setVirtualBankName(account.getBankName());
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        log.info("Virtual account provisioned for user {}: {} ({})",
                userId, maskAccountNumber(account.getAccountNumber()), account.getBankName());
    }

    @Override
    @Transactional
    public void creditWallet(WalletCreditRequest request) {
        validateAmount(request.getAmount(), "Credit");

        if (isDuplicateTransaction(request.getReference())) {
            return;
        }

        Wallet wallet = getOrCreateWalletForUpdate(request.getUserId());
        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        recordTransaction(wallet, request.getAmount(), WalletTransactionType.CREDIT, request.getDescription(), request.getReference());
    }

    @Override
    @Transactional
    public void debitWallet(WalletDebitRequest request) {
        validateAmount(request.getAmount(), "Debit");

        if (isDuplicateTransaction(request.getReference())) { return;}
        Wallet wallet = getOrCreateWalletForUpdate(request.getUserId());
        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientWalletBalanceException("Insufficient wallet balance");
        }
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);
        recordTransaction(wallet, request.getAmount(), WalletTransactionType.DEBIT, request.getDescription(), request.getReference());
    }

    @Override
    @Transactional(readOnly = true)
    public WalletBalanceResponse getWalletBalance(Long userId) {
        Optional<Wallet> walletOpt = walletRepository.findByUserId(userId);
        if (walletOpt.isEmpty()) {
            ensureUserExists(userId);
            return new WalletBalanceResponse(BigDecimal.ZERO, CURRENCY, null, null);
        }
        Wallet wallet = walletOpt.get();
        return new WalletBalanceResponse(
                wallet.getBalance(),
                CURRENCY,
                wallet.getVirtualAccountNumber(),
                wallet.getVirtualBankName());
    }

    @Override
    @Transactional(readOnly = true)
    public WalletSummaryResponse getWalletSummary(Long userId) {
        Optional<Wallet> walletOpt = walletRepository.findByUserId(userId);
        if (walletOpt.isEmpty()) {
            ensureUserExists(userId);
            return new WalletSummaryResponse(BigDecimal.ZERO, Collections.emptyList());
        }
        Wallet wallet = walletOpt.get();
        List<WalletTransactionDto> history = transactionRepository
                .findTop50ByWalletIdOrderByCreatedAtDesc(wallet.getId())
                .stream()
                .map(this::toTransactionDto)
                .collect(Collectors.toList());
        return new WalletSummaryResponse(wallet.getBalance(), history);
    }

    private Wallet getOrCreateWalletForUpdate(Long userId) {
        return walletRepository.findByUserIdForUpdate(userId)
                .or(() -> walletRepository.findByUserId(userId))
                .orElseGet(() -> {
                    UserEntity user = userEntityRepository.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
                    return walletRepository.save(new Wallet(user, BigDecimal.ZERO));
                });
    }

    private boolean isDuplicateTransaction(String reference) {
        if (reference != null && !reference.isBlank()) {
            if (transactionRepository.existsByReference(reference)) {
                log.warn("Transaction reference '{}' has already been processed. Skipping duplicate operation.", reference);
                return true;
            }
        }
        return false;
    }

    private void ensureUserExists(Long userId) {
        if (!userEntityRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }

    private void validateAmount(BigDecimal amount, String operation) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidWalletAmountException(operation + " amount must be greater than zero");
        }
    }

    private void recordTransaction(Wallet wallet, BigDecimal amount, WalletTransactionType type,
                                   String description, String reference) {
        transactionRepository.save(new WalletTransaction(wallet, amount, type, description, reference));
    }

    private WalletTransactionDto toTransactionDto(WalletTransaction tx) {
        return new WalletTransactionDto(
                tx.getId(),
                tx.getAmount(),
                tx.getType() != null ? tx.getType().name() : null,
                tx.getDescription(),
                tx.getReference(),
                tx.getCreatedAt());
    }

    private boolean hasVirtualAccount(Wallet wallet) {
        return wallet.getVirtualAccountNumber() != null && !wallet.getVirtualAccountNumber().isBlank();
    }

    private String resolveFullName(UserEntity user) {
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";
        String fullName = (firstName + " " + lastName).trim();
        return fullName.isBlank() ? "Kaldar User" : fullName;
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "****";
        }
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }
}
