# Credit-Based-Resource-Provisioner

A Java and Spring Boot backend that manages allocation and release of cloud resources (CPU units, workers, GPU time, ...) using a credit-based model.
Credits act as a virtual currency to pay for cloud resources before they can be provisioned.

## Overview

This project implements a backend service that manages provisioning of cloud resources based on available credits.
It was originally inspired by a trading challenge, but it has been redesigned into a generic cloud resource provisioner, using:
- Spring Boot
- JPA / Hibernate
- BigDecimal for precise cost and quantity calculations

An account can allocate or release compute units as long as:
- It has enough credits to cover the cost of the requested quantity
- It owns enough resources to release them
The service ensures all updates remain consistent, reversible, and atomic, even when cancelling.

## Core Features
### Create a provisioning request
- Fetches the cost of the resource 
- Checks available credits
  - If enough credit, deducts credits and increases resource quantity 
  - If not, throws an exception: "Insufficient credit" with code 400
- Saves the provisioning request as CREATED

### Retrieve a provisioning request
- Fetches a provisioning request by its ID
- Throws an exception: "Provisioning request not found" with code 404 if not found

### Cancel a provisioning request
- Checks if the provisioning request status is CREATED
  - If not, throws an exception "Provisioning request cannot be cancelled" with code 400
- Fetches the cost of the resource
- Checks available resource quantity
  - If enough resources, release resources and restores credit
  - If not, throws an exception: "Insufficient resources" with code 400
- Saves the provisioning request as CANCELLED

### Unified Update Logic
The updateAccount() method ensures no code duplication and handles all account and resource updates consistently:
- Handles both ALLOCATE and RELEASE operations
- Supports reversal logic when cancelling
- Includes strict validation:
  - Insufficient credits 
  - Insufficient resources
  - Missing account or resource entries 

Both repositories (AccountCreditsRepository and ProvisionedResourcesRepository) are updated atomically.

## Why this lifecycle?

Because a request can be scheduled before the resource is actually assigned. Once assigned (EXECUTED), you cannot cancel it, you must instead create a RELEASE allocation.
Whenever a provisioning request is created or cancelled, the system updates both the AccountCredits table and the ProvisionedResources table.
These updates must remain consistent and reversible, so the service ensures:
- Credits never go negative
- Released resources cannot exceed owned resources
- The system never enters an inconsistent partial state
