package com.Credit_Based_Resource_Allocator.api;

import com.Credit_Based_Resource_Allocator.AllocationRequest;
import com.Credit_Based_Resource_Allocator.repository.AllocationEntity;
import com.Credit_Based_Resource_Allocator.service.AllocatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This is the entry point for all allocation operations.
 */
@RestController
@RequestMapping("/allocation")
public class AllocatorController {

    @Autowired
    private AllocatorService allocatorService;

    /**
     * This endpoint allows to create an allocation.
     * @param allocationRequest contains the attributes needed to create an allocation.
     * @return the allocation entity and status 200 if created properly.
     */
    @PostMapping
    public ResponseEntity<AllocationEntity> createOrder(@RequestBody AllocationRequest allocationRequest) {
        AllocationEntity allocation = allocatorService.createAllocation(allocationRequest);
        return ResponseEntity.ok(allocation);
    }

    /**
     * This endpoint allows to retrieve an allocation using its id.
     * @param allocationId is the allocation id.
     * @return the allocation entity associated with the id and status 200 if it exists.
     */
    @GetMapping("/{allocationId}")
    public ResponseEntity<AllocationEntity> getOrder(@PathVariable("allocationId") Long allocationId) {
        AllocationEntity allocation = allocatorService.getAllocation(allocationId);
        return ResponseEntity.ok(allocation);
    }

    /**
     * This endpoint allows to cancel an order using its id.
     * @param allocationId is the allocation id.
     * @return the allocation entity and status 200 if cancelled properly.
     */
    @PutMapping("/{allocationId}")
    public ResponseEntity<AllocationEntity> cancelOrder(@PathVariable("allocationId") Long allocationId) {
        AllocationEntity allocation = allocatorService.cancelAllocation(allocationId);
        return ResponseEntity.ok(allocation);
    }
}
