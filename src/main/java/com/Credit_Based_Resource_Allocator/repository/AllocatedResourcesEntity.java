package com.Credit_Based_Resource_Allocator.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

import java.math.BigDecimal;

@Entity
@IdClass(AllocatedResourcesEntityId.class)
public class AllocatedResourcesEntity {
    @Id
    private String accountId;
    @Id
    private String resourceId;
    private BigDecimal quantity;

    public AllocatedResourcesEntity() {
    }

    public AllocatedResourcesEntity(String accountId, String resourceId, BigDecimal quantity) {
        this.accountId = accountId;
        this.resourceId = resourceId;
        this.quantity = quantity;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }
}

