package com.Credit_Based_Resource_Allocator.repository;


import com.Credit_Based_Resource_Allocator.service.RequestStatus;
import com.Credit_Based_Resource_Allocator.service.RequestSide;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity
public class AllocationRequestEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String accountId;
    private String resourceId;
    private RequestStatus status;
    private RequestSide side;
    private BigDecimal quantity;
    private BigDecimal price;

    public AllocationRequestEntity(String accountId, String resourceId, RequestStatus orderStatus, RequestSide side, BigDecimal quantity, BigDecimal price) {
        this.accountId = accountId;
        this.resourceId = resourceId;
        this.status = orderStatus;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
    }

    public AllocationRequestEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getAccountId() { return accountId; }

    public String getResourceId() {
        return resourceId;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public RequestSide getSide() {
        return side;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
