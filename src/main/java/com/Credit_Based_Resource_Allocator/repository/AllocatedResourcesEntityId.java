package com.Credit_Based_Resource_Allocator.repository;

import java.io.Serializable;

public class AllocatedResourcesEntityId implements Serializable {
    private String accountId;
    private String resourceId;

    public AllocatedResourcesEntityId(String accountId, String resourceId) {
        this.accountId = accountId;
        this.resourceId = resourceId;
    }

    public AllocatedResourcesEntityId() {
    }

    public String getAccountId() {
        return accountId;
    }

    public String getResourceId() {
        return resourceId;
    }
}
