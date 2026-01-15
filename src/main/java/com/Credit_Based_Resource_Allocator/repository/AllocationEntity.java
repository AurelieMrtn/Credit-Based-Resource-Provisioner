package com.Credit_Based_Resource_Allocator.repository;


import com.Credit_Based_Resource_Allocator.AllocationStatus;
import com.Credit_Based_Resource_Allocator.AllocationSide;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity
public class AllocationEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String accountId;
    private String resourceId;
    private AllocationStatus status;
    private AllocationSide side;
    private BigDecimal quantity;
    private BigDecimal cost;

    public AllocationEntity(String accountId, String resourceId, AllocationStatus orderStatus, AllocationSide side, BigDecimal quantity, BigDecimal cost) {
        this.accountId = accountId;
        this.resourceId = resourceId;
        this.status = orderStatus;
        this.side = side;
        this.quantity = quantity;
        this.cost = cost;
    }

    public AllocationEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getAccountId() { return accountId; }

    public String getResourceId() {
        return resourceId;
    }

    public AllocationStatus getStatus() {
        return status;
    }

    public void setStatus(AllocationStatus status) {
        this.status = status;
    }

    public AllocationSide getSide() {
        return side;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getCost() {
        return cost;
    }
}
