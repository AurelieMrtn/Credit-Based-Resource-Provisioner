package com.Credit_Based_Resource_Allocator.repository;

import java.io.Serializable;

public class AllocatedRessourcesEntityId implements Serializable {
    private String accountId;
    private String resourceId;

    public AllocatedRessourcesEntityId(String accountId, String resourceId) {
        this.accountId = accountId;
        this.resourceId = resourceId;
    }

    public AllocatedRessourcesEntityId() {
    }

    public String getAccountId() {
        return accountId;
    }

    public String getResourceId() {
        return resourceId;
    }
}
