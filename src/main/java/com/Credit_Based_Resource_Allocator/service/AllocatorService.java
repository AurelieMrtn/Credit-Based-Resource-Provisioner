package com.Credit_Based_Resource_Allocator.service;

import com.Credit_Based_Resource_Allocator.AllocationRequest;
import com.Credit_Based_Resource_Allocator.AllocationSide;
import com.Credit_Based_Resource_Allocator.AllocationStatus;
import com.Credit_Based_Resource_Allocator.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * This is the AllocatorService class, used in the AllocatorController class to easily create, retrieve or cancel an allocation.
 */
@Service
public class AllocatorService {
    @Autowired
    private AllocationRepository allocationRepository;

    @Autowired
    private AccountCreditsRepository accountCreditsRepository;

    @Autowired
    private AllocatedResourcesRepository allocatedResourcesRepository;

    @Autowired
    private CostsDataService costsDataService;

    /**
     * This method is used to create an allocation.
     * @param allocationRequest contains the attributes needed to create an allocation.
     * @return the allocation entity if created properly, otherwise throws an error.
     */
    public AllocationEntity createAllocation(AllocationRequest allocationRequest) {
        BigDecimal price = costsDataService.getCost(allocationRequest.getResourceId());
        BigDecimal quantity = allocationRequest.getQuantity();

        updatePortfolio(allocationRequest.getAccountId(), allocationRequest.getResourceId(), allocationRequest.getSide(), quantity, price, false);

        AllocationEntity allocation = new AllocationEntity(
                allocationRequest.getAccountId(),
                allocationRequest.getResourceId(),
                AllocationStatus.CREATED,
                allocationRequest.getSide(),
                quantity,
                price
        );

        return allocationRepository.save(allocation);
    }

    /**
     * This method is used to retrieve an allocation.
     * @param allocationId is the allocation id.
     * @return the allocation entity associated with the id if it exists, otherwise throws an error.
     */
    public AllocationEntity getAllocation(Long allocationId) {
        return allocationRepository.findById(allocationId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    /**
     * This method is used to cancel an allocation.
     * @param allocationId is the allocation id.
     * @return the allocation entity if cancelled properly, otherwise throws an error.
     */
    public AllocationEntity cancelAllocation(Long allocationId) {
        AllocationEntity allocation = getAllocation(allocationId);
        if (allocation.getStatus() != AllocationStatus.CREATED) throw new IllegalArgumentException("Order cannot be cancelled");

        BigDecimal price = costsDataService.getCost(allocation.getResourceId());
        BigDecimal quantity = allocation.getQuantity();
        updatePortfolio(allocation.getAccountId(), allocation.getResourceId(), allocation.getSide(), quantity, price, true);

        allocation.setStatus(AllocationStatus.CANCELLED);
        return allocationRepository.save(allocation);
    }

    /**
     * This method is used to avoid code duplication, it allows to update both accountsCredits and allocatedResources repositories while creating or cancelling an allocation.
     * @param accountId the account id.
     * @param resourceId the resource id.
     * @param side whether it's a BUY or SELL order.
     * @param quantity the quantity of the order.
     * @param price the price of the order.
     * @param isCancelling whether the order is being created or canceled. If true then reverts the changes.
     * @throws IllegalArgumentException if the portfolio is not found, the buying power is insufficient, or the inventory doesn't have the required stocks quantity.
     */
    private void updatePortfolio(String accountId, String resourceId, AllocationSide side, BigDecimal quantity, BigDecimal price, boolean isCancelling) {
        BigDecimal totalPrice = price.multiply(quantity);

        AccountCreditsEntity accountCredits = accountCreditsRepository.findById(accountId)
                .orElse(new AccountCreditsEntity(accountId, new BigDecimal("5000.00")));
        AllocatedResourcesEntity allocatedResources = allocatedResourcesRepository.findById(new AllocatedRessourcesEntityId(accountId, resourceId))
                .orElse(new AllocatedResourcesEntity(accountId, resourceId, BigDecimal.ZERO));

        if ((side == AllocationSide.ALLOCATE && !isCancelling) || (side == AllocationSide.RELEASE && isCancelling)) {
            if (accountCredits.getAmount().compareTo(totalPrice) < 0 ) throw new IllegalArgumentException("Insufficient buying power");
            accountCredits = new AccountCreditsEntity(accountCredits.getAccountId(), accountCredits.getAmount().subtract(totalPrice));
            allocatedResources = new AllocatedResourcesEntity(allocatedResources.getAccountId(), allocatedResources.getResourceId(), allocatedResources.getQuantity().add(quantity));
        }
        else if ((side == AllocationSide.RELEASE && !isCancelling) || (side == AllocationSide.ALLOCATE && isCancelling)) {
            if (allocatedResources.getQuantity().compareTo(quantity) < 0) throw new IllegalArgumentException("Insufficient inventory");
            accountCredits = new AccountCreditsEntity(accountCredits.getAccountId(), accountCredits.getAmount().add(totalPrice));
            allocatedResources = new AllocatedResourcesEntity(allocatedResources.getAccountId(), allocatedResources.getResourceId(), allocatedResources.getQuantity().subtract(quantity));
        }

        allocatedResourcesRepository.save(allocatedResources);
        accountCreditsRepository.save(accountCredits);
    }
}
