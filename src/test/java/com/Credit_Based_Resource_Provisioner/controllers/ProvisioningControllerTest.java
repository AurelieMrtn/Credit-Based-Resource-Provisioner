package com.Credit_Based_Resource_Provisioner.controllers;

import com.Credit_Based_Resource_Provisioner.models.ProvisioningStatus;
import com.Credit_Based_Resource_Provisioner.repositories.*;
import com.Credit_Based_Resource_Provisioner.services.CostsDataService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
class ProvisioningControllerTest {

	@Autowired
	private ProvisioningRequestRepository provisioningRequestRepository;

	@Autowired
	private AccountCreditsRepository accountCreditsRepository;

	@Autowired
	private ProvisionedResourcesRepository provisionedResourcesRepository;

	@Autowired
	private CostsDataService costsDataService;

	@Autowired
	private MockMvc mvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@AfterEach
	public void tearDown() {
		provisioningRequestRepository.deleteAll();
		provisionedResourcesRepository.deleteAll();
		accountCreditsRepository.deleteAll();
	}

	@BeforeEach
	public void setUp() {
		AccountCreditsEntity accountCredits = new AccountCreditsEntity("account-id-1", new BigDecimal("5000.00"));
		accountCreditsRepository.save(accountCredits);
	}

	@Test
	public void shouldCreateAllocateProvisioningRequestSuccessfully() throws Exception {
		Map<String, String> object = new HashMap<>();
		object.put("accountId", "account-id-1");
		object.put("resourceId", "GPU-A100");
		object.put("side", "ALLOCATE");
		object.put("quantity", "10.00");

		createProvisioningRequest(objectMapper.writeValueAsString(object))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.status").value("CREATED"))
				.andExpect(jsonPath("$.accountId").value("account-id-1"))
				.andExpect(jsonPath("$.resourceId").value("GPU-A100"));

		verifyCredit("account-id-1", new BigDecimal("3000.00"));
		verifyResources("account-id-1", "GPU-A100", new BigDecimal("10.00"));
	}

	@Test
	public void shouldCreateReleaseProvisioningRequestSuccessfully() throws Exception {
		Map<String, String> objectToAllocate = new HashMap<>();
		objectToAllocate.put("accountId", "account-id-1");
		objectToAllocate.put("resourceId", "GPU-A100");
		objectToAllocate.put("side", "ALLOCATE");
		objectToAllocate.put("quantity", "10.00");

		createProvisioningRequest(objectMapper.writeValueAsString(objectToAllocate));

		Map<String, String> objectToRelease = new HashMap<>();
		objectToRelease.put("accountId", "account-id-1");
		objectToRelease.put("resourceId", "GPU-A100");
		objectToRelease.put("side", "RELEASE");
		objectToRelease.put("quantity", "5.00");

		createProvisioningRequest(objectMapper.writeValueAsString(objectToRelease))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.status").value("CREATED"))
				.andExpect(jsonPath("$.accountId").value("account-id-1"))
				.andExpect(jsonPath("$.resourceId").value("GPU-A100"));

		verifyCredit("account-id-1", new BigDecimal("4000.00"));
		verifyResources("account-id-1", "GPU-A100", new BigDecimal("5.00"));
	}

    @Test
	public void shouldThrowBusinessExceptionWhenInsufficientCreditsToAllocate() throws Exception {
		Map<String, String> object = new HashMap<>();
		object.put("accountId", "account-id-1");
		object.put("resourceId", "GPU-A100");
		object.put("side", "ALLOCATE");
		object.put("quantity", "100.00");

		createProvisioningRequest(objectMapper.writeValueAsString(object))
				.andExpect(status().is(400))
				.andExpect(result -> assertEquals("Insufficient credit", result.getResolvedException().getMessage()));
	}

	@Test
	public void shouldThrowBusinessExceptionWhenInsufficientQuantityToRelease() throws Exception {
		Map<String, String> objectToAllocate = new HashMap<>();
		objectToAllocate.put("accountId", "account-id-1");
		objectToAllocate.put("resourceId", "GPU-A100");
		objectToAllocate.put("side", "ALLOCATE");
		objectToAllocate.put("quantity", "5.00");

		createProvisioningRequest(objectMapper.writeValueAsString(objectToAllocate));

		Map<String, String> objectToRelease = new HashMap<>();
		objectToRelease.put("accountId", "account-id-1");
		objectToRelease.put("resourceId", "GPU-A100");
		objectToRelease.put("side", "RELEASE");
		objectToRelease.put("quantity", "10.00");

		createProvisioningRequest(objectMapper.writeValueAsString(objectToRelease))
				.andExpect(status().is(400))
				.andExpect(result -> assertEquals("Insufficient resources", result.getResolvedException().getMessage()));
	}

	@Test
	public void shouldGetProvisioningRequestById() throws Exception {
		Map<String, String> object = new HashMap<>();
		object.put("accountId", "account-id-1");
		object.put("resourceId", "GPU-A100");
		object.put("side", "ALLOCATE");
		object.put("quantity", "10.00");

		String creationResponse = createProvisioningRequest(objectMapper.writeValueAsString(object)).andReturn().getResponse().getContentAsString();

		long provisioningRequestId = objectMapper.readTree(creationResponse).get("id").asLong();

		String response = getProvisioningRequest(provisioningRequestId).andReturn().getResponse().getContentAsString();
		assertEquals(creationResponse, response);
	}

	@Test
	public void shouldCancelAllocateProvisioningRequest() throws Exception {
		Map<String, String> object = new HashMap<>();
		object.put("accountId", "account-id-1");
		object.put("resourceId", "GPU-A100");
		object.put("side", "ALLOCATE");
		object.put("quantity", "10.00");

		String creationResponse = createProvisioningRequest(objectMapper.writeValueAsString(object)).andReturn().getResponse().getContentAsString();
		long provisioningRequestId = objectMapper.readTree(creationResponse).get("id").asLong();

		cancelProvisioningRequest(provisioningRequestId);

		ProvisioningRequestEntity provisioningRequestEntity = provisioningRequestRepository.findById(provisioningRequestId).orElse(null);
		assert provisioningRequestEntity != null;
		Assertions.assertEquals(ProvisioningStatus.CANCELLED, provisioningRequestEntity.getStatus());

		verifyResources("account-id-1", "GPU-A100", new BigDecimal("0.00"));
		verifyCredit("account-id-1", new BigDecimal("5000.00"));
	}

	@Test
	public void shouldCancelReleaseProvisioningRequest() throws Exception {
		Map<String, String> objectToAllocate = new HashMap<>();
		objectToAllocate.put("accountId", "account-id-1");
		objectToAllocate.put("resourceId", "GPU-A100");
		objectToAllocate.put("side", "ALLOCATE");
		objectToAllocate.put("quantity", "10.00");

		createProvisioningRequest(objectMapper.writeValueAsString(objectToAllocate));

		Map<String, String> objectToRelease = new HashMap<>();
		objectToRelease.put("accountId", "account-id-1");
		objectToRelease.put("resourceId", "GPU-A100");
		objectToRelease.put("side", "RELEASE");
		objectToRelease.put("quantity", "5.00");

		String creationResponse = createProvisioningRequest(objectMapper.writeValueAsString(objectToRelease)).andReturn().getResponse().getContentAsString();
		long provisioningRequestId = objectMapper.readTree(creationResponse).get("id").asLong();

		cancelProvisioningRequest(provisioningRequestId);

		ProvisioningRequestEntity provisioningRequestEntity = provisioningRequestRepository.findById(provisioningRequestId).orElse(null);
		assert provisioningRequestEntity != null;
		Assertions.assertEquals(ProvisioningStatus.CANCELLED, provisioningRequestEntity.getStatus());

		verifyResources("account-id-1", "GPU-A100", new BigDecimal("10.00"));
		verifyCredit("account-id-1", new BigDecimal("3000.00"));
	}

	private ResultActions createProvisioningRequest(String provisioningRequest) throws Exception {
		MockHttpServletRequestBuilder content = post("/provisioning")
				.content(provisioningRequest)
				.contentType(MediaType.APPLICATION_JSON);

		return mvc.perform(content);
	}

	private ResultActions cancelProvisioningRequest(long id) throws Exception {
		MockHttpServletRequestBuilder content = put("/provisioning/" + id)
				.contentType(MediaType.APPLICATION_JSON);

		return mvc.perform(content);
	}

	private ResultActions getProvisioningRequest(long id) throws Exception {
		MockHttpServletRequestBuilder content = get("/provisioning/" + id)
				.contentType(MediaType.APPLICATION_JSON);

		return mvc.perform(content);
	}

	private void verifyResources(String accountId, String resourceId, BigDecimal expectedQuantity) {
		ProvisionedResourcesEntity provisionedResourcesEntity = provisionedResourcesRepository
				.findById(new ProvisionedResourcesEntityId(accountId, resourceId))
				.orElse(new ProvisionedResourcesEntity(accountId, resourceId, BigDecimal.ZERO));
		assertThat(provisionedResourcesEntity.getQuantity())
				.as("Provisioned resources of account %s and resourceId %s, should be %s.", accountId, resourceId, expectedQuantity)
				.isEqualByComparingTo(expectedQuantity);
	}

	private void verifyCredit(String accountId, BigDecimal expectedAmount) {
		AccountCreditsEntity accountCredits = accountCreditsRepository
				.findById(accountId)
				.orElse(new AccountCreditsEntity(accountId, BigDecimal.ZERO));
		assertThat(accountCredits.getAmount())
				.as("Credits of account %s should equal %s.", accountId, expectedAmount)
				.isEqualByComparingTo(expectedAmount);
	}

}
