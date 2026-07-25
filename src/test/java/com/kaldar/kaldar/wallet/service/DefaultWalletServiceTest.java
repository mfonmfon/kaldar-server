package com.kaldar.kaldar.wallet.service;

import com.kaldar.kaldar.customer.domain.model.CustomerEntity;
import com.kaldar.kaldar.shared.domain.exceptions.InsufficientWalletBalanceException;
import com.kaldar.kaldar.shared.domain.exceptions.InvalidWalletAmountException;
import com.kaldar.kaldar.shared.domain.exceptions.UserNotFoundException;
import com.kaldar.kaldar.shared.domain.repository.UserEntityRepository;
import com.kaldar.kaldar.wallet.application.dto.WalletCreditRequest;
import com.kaldar.kaldar.wallet.application.dto.WalletDebitRequest;
import com.kaldar.kaldar.wallet.application.dto.WalletSummaryResponse;
import com.kaldar.kaldar.wallet.application.service.impl.DefaultWalletService;
import com.kaldar.kaldar.wallet.domain.model.Wallet;
import com.kaldar.kaldar.wallet.domain.model.WalletTransaction;
import com.kaldar.kaldar.wallet.domain.repository.WalletRepository;
import com.kaldar.kaldar.wallet.domain.repository.WalletTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultWalletService Unit Tests (All Scenarios)")
class DefaultWalletServiceTest {

    @Mock private WalletRepository walletRepository;
    @Mock private WalletTransactionRepository transactionRepository;
    @Mock private UserEntityRepository userEntityRepository;
    @Mock private com.kaldar.kaldar.shared.infrastructure.anchor.AnchorClient anchorClient;

    private DefaultWalletService walletService;

    @BeforeEach
    void setUp() {
        walletService = new DefaultWalletService(walletRepository, transactionRepository, userEntityRepository, anchorClient);
    }

    private CustomerEntity buildUser() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);
        customer.setFirstName("Jane");
        customer.setLastName("Smith");
        return customer;
    }

    private Wallet buildWallet(CustomerEntity user) {
        Wallet wallet = new Wallet(user, BigDecimal.ZERO);
        wallet.setId(10L);
        return wallet;
    }

    @Nested
    @DisplayName("creditWallet()")
    class CreditWallet {

        @Test
        @DisplayName("should credit wallet successfully and log transaction details")
        void shouldCreditWalletSuccessfully() {
            CustomerEntity user = buildUser();
            Wallet wallet = buildWallet(user);

            when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));
            when(walletRepository.save(any(Wallet.class))).thenAnswer(inv -> inv.getArgument(0));

            walletService.creditWallet(new WalletCreditRequest(1L, BigDecimal.valueOf(5000.0), "Earnings", "REF123"));

            assertThat(wallet.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(5000.0));
            verify(walletRepository).save(wallet);
            verify(transactionRepository).save(any(WalletTransaction.class));
        }

        @Test
        @DisplayName("should lazily initialize new wallet if none exists when crediting")
        void shouldInitNewWalletWhenNoneExists() {
            CustomerEntity user = buildUser();
            when(walletRepository.findByUserId(1L)).thenReturn(Optional.empty());
            when(userEntityRepository.findById(1L)).thenReturn(Optional.of(user));
            when(walletRepository.save(any(Wallet.class))).thenAnswer(inv -> inv.getArgument(0));

            walletService.creditWallet(new WalletCreditRequest(1L, BigDecimal.valueOf(1000.0), "Welcome Bonus", "REF000"));

            verify(userEntityRepository).findById(1L);
            verify(walletRepository, times(2)).save(any(Wallet.class)); 
        }

        @Test
        @DisplayName("should throw UserNotFoundException if user profile does not exist on lazy wallet creation")
        void shouldThrowUserNotFoundOnLazyInit() {
            when(walletRepository.findByUserId(99L)).thenReturn(Optional.empty());
            when(userEntityRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> walletService.creditWallet(new WalletCreditRequest(99L, BigDecimal.valueOf(1000.0), "Bonus", "REF")))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("User not found with ID: 99");
        }

        @Test
        @DisplayName("should throw InvalidWalletAmountException when credit amount is zero")
        void shouldThrowWhenCreditAmountIsZero() {
            assertThatThrownBy(() -> walletService.creditWallet(new WalletCreditRequest(1L, BigDecimal.ZERO, "Zero", "REF")))
                    .isInstanceOf(InvalidWalletAmountException.class)
                    .hasMessageContaining("Credit amount must be greater than zero");
        }

        @Test
        @DisplayName("should throw InvalidWalletAmountException when credit amount is negative")
        void shouldThrowWhenCreditAmountIsNegative() {
            assertThatThrownBy(() -> walletService.creditWallet(new WalletCreditRequest(1L, BigDecimal.valueOf(-10.50), "Negative", "REF")))
                    .isInstanceOf(InvalidWalletAmountException.class)
                    .hasMessageContaining("Credit amount must be greater than zero");
        }
    }

    @Nested
    @DisplayName("debitWallet()")
    class DebitWallet {

        @Test
        @DisplayName("should debit wallet successfully when balance is sufficient")
        void shouldDebitWalletSuccessfully() {
            CustomerEntity user = buildUser();
            Wallet wallet = buildWallet(user);
            wallet.setBalance(BigDecimal.valueOf(10000.0));

            when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));
            when(walletRepository.save(any(Wallet.class))).thenAnswer(inv -> inv.getArgument(0));

            walletService.debitWallet(new WalletDebitRequest(1L, BigDecimal.valueOf(3500.0), "Checkout", "REF987"));

            assertThat(wallet.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(6500.0));
            verify(walletRepository).save(wallet);
            verify(transactionRepository).save(any(WalletTransaction.class));
        }

        @Test
        @DisplayName("should throw InsufficientWalletBalanceException when balance is insufficient")
        void shouldThrowWhenInsufficientBalance() {
            CustomerEntity user = buildUser();
            Wallet wallet = buildWallet(user);
            wallet.setBalance(BigDecimal.valueOf(1000.0));

            when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));

            assertThatThrownBy(() -> walletService.debitWallet(new WalletDebitRequest(1L, BigDecimal.valueOf(2000.0), "Checkout", "REF")))
                    .isInstanceOf(InsufficientWalletBalanceException.class)
                    .hasMessageContaining("Insufficient wallet balance");
        }

        @Test
        @DisplayName("should throw InvalidWalletAmountException when debit amount is zero")
        void shouldThrowWhenDebitAmountIsZero() {
            assertThatThrownBy(() -> walletService.debitWallet(new WalletDebitRequest(1L, BigDecimal.ZERO, "Zero", "REF")))
                    .isInstanceOf(InvalidWalletAmountException.class)
                    .hasMessageContaining("Debit amount must be greater than zero");
        }

        @Test
        @DisplayName("should throw InvalidWalletAmountException when debit amount is negative")
        void shouldThrowWhenDebitAmountIsNegative() {
            assertThatThrownBy(() -> walletService.debitWallet(new WalletDebitRequest(1L, BigDecimal.valueOf(-5.0), "Negative", "REF")))
                    .isInstanceOf(InvalidWalletAmountException.class)
                    .hasMessageContaining("Debit amount must be greater than zero");
        }
    }

    @Nested
    @DisplayName("getWalletSummary()")
    class GetWalletSummary {

        @Test
        @DisplayName("should return balance and transaction history in descending order")
        void shouldReturnWalletSummary() {
            CustomerEntity user = buildUser();
            Wallet wallet = buildWallet(user);
            wallet.setBalance(BigDecimal.valueOf(25000.0));

            WalletTransaction tx1 = new WalletTransaction(wallet, BigDecimal.valueOf(5000.0), "CREDIT", "Earnings", "REF1");
            tx1.setId(101L);
            tx1.setCreatedAt(LocalDateTime.now().minusDays(1));

            when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));
            when(transactionRepository.findByWalletIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(tx1));

            WalletSummaryResponse response = walletService.getWalletSummary(1L);

            assertThat(response.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(25000.0));
            assertThat(response.getTransactionHistory()).hasSize(1);
            assertThat(response.getTransactionHistory().get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(5000.0));
        }

        @Test
        @DisplayName("should initialize new wallet and return zero balance when no wallet exists")
        void shouldReturnZeroSummaryWhenNoWalletExists() {
            CustomerEntity user = buildUser();
            when(walletRepository.findByUserId(1L)).thenReturn(Optional.empty());
            when(userEntityRepository.findById(1L)).thenReturn(Optional.of(user));
            when(walletRepository.save(any(Wallet.class))).thenAnswer(inv -> inv.getArgument(0));

            WalletSummaryResponse response = walletService.getWalletSummary(1L);

            assertThat(response.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(response.getTransactionHistory()).isEmpty();
        }
    }
}