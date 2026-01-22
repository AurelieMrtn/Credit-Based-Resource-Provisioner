package com.Credit_Based_Resource_Allocator.api;

import com.Credit_Based_Resource_Allocator.repository.*;
import com.Credit_Based_Resource_Allocator.service.CostsDataService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;


@SpringBootTest
@AutoConfigureMockMvc
class AllocatorControllerTest {

	@Autowired
	private AllocationRepository allocationRepository;

	@Autowired
	private AccountCreditsRepository accountCreditsRepository;

	@Autowired
	private AllocatedResourcesRepository allocatedResourcesRepository;

	@Autowired
	private CostsDataService costsDataService;

	@Autowired
	private MockMvc mvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@AfterEach
	public void tearDown() {
		allocationRepository.deleteAll();
		allocatedResourcesRepository.deleteAll();
		accountCreditsRepository.deleteAll();
	}

	@BeforeEach
	public void setUp() {
		AccountCreditsEntity buyingPowerEntity = new AccountCreditsEntity("account-id-1", new BigDecimal("5000.00"));
		accountCreditsRepository.save(buyingPowerEntity);
	}

}
