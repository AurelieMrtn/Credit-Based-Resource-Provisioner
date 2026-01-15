package com.Credit_Based_Resource_Allocator;

import java.math.BigDecimal;

/**
 * This class represents the request body to create an allocation.
 */
public class AllocationRequest {
    private String accountId;
    private String resourceId;
    private AllocationSide side;
    private BigDecimal quantity;

    public AllocationRequest(String accountId, String resourceId, AllocationSide side, BigDecimal quantity) {
        this.accountId = accountId;
        this.resourceId = resourceId;
        this.side = side;
        this.quantity = quantity;
    }

    public AllocationRequest() {
    }

    public String getAccountId() {
        return accountId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public AllocationSide getSide() {
        return side;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }
}
