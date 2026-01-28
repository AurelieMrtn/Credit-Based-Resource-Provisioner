package com.Credit_Based_Resource_Provisioner.repositories;


import com.Credit_Based_Resource_Provisioner.models.ProvisioningStatus;
import com.Credit_Based_Resource_Provisioner.models.ProvisioningAction;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity
public class ProvisioningRequestEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String accountId;
    private String resourceId;
    private ProvisioningStatus status;
    private ProvisioningAction side;
    private BigDecimal quantity;
    private BigDecimal cost;

    public ProvisioningRequestEntity(String accountId, String resourceId, ProvisioningStatus status, ProvisioningAction side, BigDecimal quantity, BigDecimal cost) {
        this.accountId = accountId;
        this.resourceId = resourceId;
        this.status = status;
        this.side = side;
        this.quantity = quantity;
        this.cost = cost;
    }

    public ProvisioningRequestEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getAccountId() { return accountId; }

    public String getResourceId() {
        return resourceId;
    }

    public ProvisioningStatus getStatus() {
        return status;
    }

    public void setStatus(ProvisioningStatus status) {
        this.status = status;
    }

    public ProvisioningAction getSide() {
        return side;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getCost() {
        return cost;
    }
}
