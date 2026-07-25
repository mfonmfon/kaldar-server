package com.kaldar.kaldar.wallet.domain.model;

import com.kaldar.kaldar.shared.domain.model.UserEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private UserEntity user;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    /** Anchor sub-account ID — used for book transfers and payout withdrawals. */
    @Column(name = "anchor_account_id")
    private String anchorAccountId;

    /** Virtual bank account number customers transfer money to for wallet funding. */
    @Column(name = "virtual_account_number")
    private String virtualAccountNumber;

    /** Name of the bank that hosts the virtual account (e.g. "Anchor Microfinance Bank"). */
    @Column(name = "virtual_bank_name")
    private String virtualBankName;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Wallet() {}

    public Wallet(UserEntity user, BigDecimal balance) {
        this.user      = user;
        this.balance   = balance;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId()                           { return id; }
    public void setId(Long id)                    { this.id = id; }

    public UserEntity getUser()                   { return user; }
    public void setUser(UserEntity user)          { this.user = user; }

    public BigDecimal getBalance()                { return balance; }
    public void setBalance(BigDecimal balance)    { this.balance = balance; }

    public String getAnchorAccountId()                          { return anchorAccountId; }
    public void setAnchorAccountId(String anchorAccountId)      { this.anchorAccountId = anchorAccountId; }

    public String getVirtualAccountNumber()                          { return virtualAccountNumber; }
    public void setVirtualAccountNumber(String virtualAccountNumber) { this.virtualAccountNumber = virtualAccountNumber; }

    public String getVirtualBankName()                    { return virtualBankName; }
    public void setVirtualBankName(String virtualBankName){ this.virtualBankName = virtualBankName; }

    public LocalDateTime getCreatedAt()                           { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)             { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt()                           { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)             { this.updatedAt = updatedAt; }
}
