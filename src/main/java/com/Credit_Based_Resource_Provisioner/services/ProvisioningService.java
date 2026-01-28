package com.Credit_Based_Resource_Provisioner.services;

import com.Credit_Based_Resource_Provisioner.dtos.ProvisioningRequestDto;
import com.Credit_Based_Resource_Provisioner.models.ProvisioningAction;
import com.Credit_Based_Resource_Provisioner.models.ProvisioningStatus;
import com.Credit_Based_Resource_Provisioner.exceptions.ProvisioningRequestNotFoundException;
import com.Credit_Based_Resource_Provisioner.exceptions.BusinessException;
import com.Credit_Based_Resource_Provisioner.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * This is the ProvisioningService class, used in the ProvisioningController class to easily create, retrieve or cancel a provisioning request.
 */
@Service
public class ProvisioningService {
    @Autowired
    private ProvisioningRequestRepository provisioningRequestRepository;

    @Autowired
    private AccountCreditsRepository accountCreditsRepository;

    @Autowired
    private ProvisionedResourcesRepository provisionedResourcesRepository;

    @Autowired
    private CostsDataService costsDataService;

    /**
     * This method is used to create a provisioning request.
     * @param provisioningRequestDto contains the attributes needed to create a provisioning request.
     * @return the provisioning request entity if created properly, otherwise throws an error.
     */
    public ProvisioningRequestEntity create(ProvisioningRequestDto provisioningRequestDto) {
        BigDecimal cost = costsDataService.getCost(provisioningRequestDto.getResourceId());
        BigDecimal quantity = provisioningRequestDto.getQuantity();

        updateAccount(provisioningRequestDto.getAccountId(), provisioningRequestDto.getResourceId(), provisioningRequestDto.getSide(), quantity, cost, false);

        ProvisioningRequestEntity provisioningRequest = new ProvisioningRequestEntity(
                provisioningRequestDto.getAccountId(),
                provisioningRequestDto.getResourceId(),
                ProvisioningStatus.CREATED,
                provisioningRequestDto.getSide(),
                quantity,
                cost
        );

        return provisioningRequestRepository.save(provisioningRequest);
    }

    /**
     * This method is used to retrieve a provisioning request.
     * @param id is the provisioning request id.
     * @return the provisioning request entity associated with the id if it exists, otherwise throws an error.
     */
    public ProvisioningRequestEntity get(Long id) {
        return provisioningRequestRepository.findById(id).orElseThrow(() -> new ProvisioningRequestNotFoundException("Provisioning request not found"));
    }

    /**
     * This method is used to cancel a provisioning request.
     * @param id is the provisioning request id.
     * @return the provisioning request entity if cancelled properly, otherwise throws an error.
     */
    public ProvisioningRequestEntity cancel(Long id) {
        ProvisioningRequestEntity provisioningRequest = get(id);
        if (provisioningRequest.getStatus() != ProvisioningStatus.CREATED) throw new BusinessException("Provisioning request cannot be cancelled");

        BigDecimal cost = costsDataService.getCost(provisioningRequest.getResourceId());
        BigDecimal quantity = provisioningRequest.getQuantity();
        updateAccount(provisioningRequest.getAccountId(), provisioningRequest.getResourceId(), provisioningRequest.getSide(), quantity, cost, true);

        provisioningRequest.setStatus(ProvisioningStatus.CANCELLED);
        return provisioningRequestRepository.save(provisioningRequest);
    }

    /**
     * This method is used to avoid code duplication, it allows to update both accountsCredits and provisionedResources repositories while creating or cancelling a provisioning request.
     * @param accountId the account id.
     * @param resourceId the resource id.
     * @param side whether it's a ALLOCATE or RELEASE provisioning request.
     * @param quantity the quantity to provision.
     * @param cost the cost of the resource to provision.
     * @param isCancelling whether the provisioning request is being created or canceled. If true then reverts the changes.
     * @throws BusinessException if the account is not found, the amount of credits is insufficient, or the resources are insufficient.
     */
    private void updateAccount(String accountId, String resourceId, ProvisioningAction side, BigDecimal quantity, BigDecimal cost, boolean isCancelling) {
        BigDecimal totalCost = cost.multiply(quantity);

        AccountCreditsEntity accountCredits = accountCreditsRepository.findById(accountId)
                .orElse(new AccountCreditsEntity(accountId, new BigDecimal("5000.00")));
        ProvisionedResourcesEntity provisionedResources = provisionedResourcesRepository.findById(new ProvisionedResourcesEntityId(accountId, resourceId))
                .orElse(new ProvisionedResourcesEntity(accountId, resourceId, BigDecimal.ZERO));

        if ((side == ProvisioningAction.ALLOCATE && !isCancelling) || (side == ProvisioningAction.RELEASE && isCancelling)) {
            if (accountCredits.getAmount().compareTo(totalCost) < 0 ) throw new BusinessException("Insufficient credit");
            accountCredits = new AccountCreditsEntity(accountCredits.getAccountId(), accountCredits.getAmount().subtract(totalCost));
            provisionedResources = new ProvisionedResourcesEntity(provisionedResources.getAccountId(), provisionedResources.getResourceId(), provisionedResources.getQuantity().add(quantity));
        }
        else if ((side == ProvisioningAction.RELEASE && !isCancelling) || (side == ProvisioningAction.ALLOCATE && isCancelling)) {
            if (provisionedResources.getQuantity().compareTo(quantity) < 0) throw new BusinessException("Insufficient resources");
            accountCredits = new AccountCreditsEntity(accountCredits.getAccountId(), accountCredits.getAmount().add(totalCost));
            provisionedResources = new ProvisionedResourcesEntity(provisionedResources.getAccountId(), provisionedResources.getResourceId(), provisionedResources.getQuantity().subtract(quantity));
        }

        provisionedResourcesRepository.save(provisionedResources);
        accountCreditsRepository.save(accountCredits);
    }
}
