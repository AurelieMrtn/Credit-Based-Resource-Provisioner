package com.Credit_Based_Resource_Provisioner.controllers;

import com.Credit_Based_Resource_Provisioner.dtos.ProvisioningRequestDto;
import com.Credit_Based_Resource_Provisioner.repositories.ProvisioningRequestEntity;
import com.Credit_Based_Resource_Provisioner.services.ProvisioningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This is the entry point for all provisioning requests.
 */
@RestController
@RequestMapping("/provisioning")
public class ProvisioningController {

    @Autowired
    private ProvisioningService provisioningService;

    /**
     * This endpoint allows to create a provisioning request.
     * @param provisioningRequestDto contains the attributes needed to create a provisioning request.
     * @return the provisioning request entity and status 200 if created properly.
     */
    @PostMapping
    public ResponseEntity<ProvisioningRequestEntity> createProvisioningRequest(@RequestBody ProvisioningRequestDto provisioningRequestDto) {
        ProvisioningRequestEntity provisioningRequest = provisioningService.create(provisioningRequestDto);
        return ResponseEntity.ok(provisioningRequest);
    }

    /**
     * This endpoint allows to retrieve a provisioning request using its id.
     * @param id is the provisioning request id.
     * @return the provisioning request entity associated with the id and status 200 if it exists.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProvisioningRequestEntity> getProvisioningRequest(@PathVariable("id") Long id) {
        ProvisioningRequestEntity provisioningRequest = provisioningService.get(id);
        return ResponseEntity.ok(provisioningRequest);
    }

    /**
     * This endpoint allows to cancel a provisioning request using its id.
     * @param id is the provisioning request id.
     * @return the provisioning request entity and status 200 if cancelled properly.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProvisioningRequestEntity> cancelProvisioningRequest(@PathVariable("id") Long id) {
        ProvisioningRequestEntity provisioningRequest = provisioningService.cancel(id);
        return ResponseEntity.ok(provisioningRequest);
    }
}
