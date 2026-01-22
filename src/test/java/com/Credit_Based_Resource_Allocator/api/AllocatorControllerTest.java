package com.Credit_Based_Resource_Allocator.api;

import com.Credit_Based_Resource_Allocator.repository.*;
import com.Credit_Based_Resource_Allocator.service.CostsDataService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

	@Test
	public void shouldCreateAllocateAllocationSuccessfully() throws Exception {
		Map<String, String> object = new HashMap<>();
		object.put("accountId", "account-id-1");
		object.put("resourceId", "GPU-A100");
		object.put("side", "ALLOCATE");
		object.put("quantity", "10.00");

		createAllocation(objectMapper.writeValueAsString(object))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.status").value("CREATED"))
				.andExpect(jsonPath("$.accountId").value("account-id-1"))
				.andExpect(jsonPath("$.resourceId").value("GPU-A100"));

		verifyCredit("account-id-1", new BigDecimal("3000.00"));
		verifyResources("account-id-1", "GPU-A100", new BigDecimal("10.00"));
	}

	// Test RELEASE Allocation

	@Test
	public void shouldGetAllocationById() throws Exception {
		Map<String, String> object = new HashMap<>();
		object.put("accountId", "account-id-1");
		object.put("resourceId", "GPU-A100");
		object.put("side", "ALLOCATE");
		object.put("quantity", "10.00");

		String creationResponse = createAllocation(objectMapper.writeValueAsString(object)).andReturn().getResponse().getContentAsString();

		long allocationId = objectMapper.readTree(creationResponse).get("id").asLong();

		String response = getAllocation(allocationId).andReturn().getResponse().getContentAsString();
		assertEquals(creationResponse, response);
	}

	private ResultActions createAllocation(String allocationRequest) throws Exception {
		MockHttpServletRequestBuilder content = post("/allocation")
				.content(allocationRequest)
				.contentType(MediaType.APPLICATION_JSON);

		return mvc.perform(content);
	}

	private ResultActions getAllocation(long allocationId) throws Exception {
		MockHttpServletRequestBuilder content = get("/allocation/" + allocationId)
				.contentType(MediaType.APPLICATION_JSON);

		return mvc.perform(content);
	}

	private void verifyResources(String accountId, String resourceId, BigDecimal expectedQuantity) {
		AllocatedResourcesEntity allocatedResourcesEntity = allocatedResourcesRepository
				.findById(new AllocatedResourcesEntityId(accountId, resourceId))
				.orElse(new AllocatedResourcesEntity(accountId, resourceId, BigDecimal.ZERO));
		assertThat(allocatedResourcesEntity.getQuantity())
				.as("Allocated resources of account %s and resourceId %s, should be %s.", accountId, resourceId, expectedQuantity)
				.isEqualByComparingTo(expectedQuantity);
	}

	private void verifyCredit(String accountId, BigDecimal expectedAmount) {
		AccountCreditsEntity buyingPowerEntity = accountCreditsRepository
				.findById(accountId)
				.orElse(new AccountCreditsEntity(accountId, BigDecimal.ZERO));
		assertThat(buyingPowerEntity.getAmount())
				.as("Credits of account %s should equal %s.", accountId, expectedAmount)
				.isEqualByComparingTo(expectedAmount);
	}
}
