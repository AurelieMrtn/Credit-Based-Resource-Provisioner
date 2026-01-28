package com.Credit_Based_Resource_Provisioner.dtos;

import com.Credit_Based_Resource_Provisioner.models.ProvisioningAction;

import java.math.BigDecimal;

/**
 * This class represents the request body to create a provisioning request.
 */
public class ProvisioningRequestDto {
    private String accountId;
    private String resourceId;
    private ProvisioningAction side;
    private BigDecimal quantity;

    public ProvisioningRequestDto(String accountId, String resourceId, ProvisioningAction side, BigDecimal quantity) {
        this.accountId = accountId;
        this.resourceId = resourceId;
        this.side = side;
        this.quantity = quantity;
    }

    public ProvisioningRequestDto() {
    }

    public String getAccountId() {
        return accountId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public ProvisioningAction getSide() {
        return side;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }
}
