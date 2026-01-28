package com.Credit_Based_Resource_Provisioner.repositories;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity
public class AccountCreditsEntity {
    @Id
    private String accountId;
    private BigDecimal amount;

    public AccountCreditsEntity(String accountId, BigDecimal amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public AccountCreditsEntity() {
    }

    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}