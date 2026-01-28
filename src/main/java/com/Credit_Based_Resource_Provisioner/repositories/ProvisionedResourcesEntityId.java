package com.Credit_Based_Resource_Provisioner.repositories;

import java.io.Serializable;

public class ProvisionedResourcesEntityId implements Serializable {
    private String accountId;
    private String resourceId;

    public ProvisionedResourcesEntityId(String accountId, String resourceId) {
        this.accountId = accountId;
        this.resourceId = resourceId;
    }

    public ProvisionedResourcesEntityId() {
    }

    public String getAccountId() {
        return accountId;
    }

    public String getResourceId() {
        return resourceId;
    }
}
