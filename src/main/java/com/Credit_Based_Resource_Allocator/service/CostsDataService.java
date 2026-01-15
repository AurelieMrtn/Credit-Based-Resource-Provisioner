package com.Credit_Based_Resource_Allocator.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CostsDataService {

    public BigDecimal getCost(String resourceId) {
        return switch (resourceId) {
            case "CPU-2CORE" -> new BigDecimal("10.00");
            case "GPU-A100" -> new BigDecimal("200.00");
            case "INSTANCE-M5-LARGE" -> new BigDecimal("50.00");
            case "CONTAINER-DOCKER-SMALL" -> new BigDecimal("5.50");
            default -> throw new IllegalArgumentException("Unknown Resource ID: " + resourceId);

        };
    }
}