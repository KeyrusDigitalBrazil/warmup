/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.kymaintegrationservices.services.impl;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.URI;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedCertificateCredentialModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.validation.services.ValidationService;


@IntegrationTest
public class DefaultCertificateServiceIT extends ServicelayerTest
{
	private static final String DEFAULT_EVENTS_DESTINATION_ID = "kyma-events";
	private static final String DEFAULT_SERVICES_DESTINATION_ID = "kyma-services";
	private static final String ENCODED_VALID_TEST_CERTIFICATE = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUVjVENDQWxtZ0F3SUJBZ0lCQWpBTkJna3Foa2lHOXcwQkFRc0ZBREJxTVFzd0NRWURWUVFHRXdKUVRERUsKTUFnR0ExVUVDQXdCVGpFUU1BNEdBMVVFQnd3SFIweEpWMGxEUlRFVE1CRUdBMVVFQ2d3S1UwRlFJRWg1WW5KcApjekVOTUFzR0ExVUVDd3dFUzNsdFlURVpNQmNHQTFVRUF3d1FkMjl5YldodmJHVXVhM2x0WVM1amVEQWVGdzB4Ck9EQTNNekF4TXpJNU5EZGFGdzB4T0RFd01qZ3hNekk1TkRkYU1HVXhDekFKQmdOVkJBWVRBa1JGTVJBd0RnWUQKVlFRSUV3ZFhZV3hrYjNKbU1SQXdEZ1lEVlFRSEV3ZFhZV3hrYjNKbU1Rd3dDZ1lEVlFRS0V3TlRRVkF4RHpBTgpCZ05WQkFzVEJrTTBZMjl5WlRFVE1CRUdBMVVFQXhNS1pXTXRaR1ZtWVhWc2REQ0NBU0l3RFFZSktvWklodmNOCkFRRUJCUUFEZ2dFUEFEQ0NBUW9DZ2dFQkFLZnRLaWpOT3pEUGdKVlZIREpoNnVmMmwrMjV2enMzVE5iZW81L3cKNjdvMytvYXZWaU5EUlBtMGFIY2EvdzlaUmIwWXp3anhyS1g4cDFobU5EVGRuZjZyNzJDU0hnaDdtN0gwT2NxbQpFMk5ZZzQyWTN4Rm9ncy9JdlRmS2tMWkxzdVZEUG9mVkVrQzVRZmRVNU5ycGRoV21KV1dLZnlYM1lCOS93bzJnCjJLbE9tTmQ3RCtaN3FrQmg2ZkVLVlpBUDY5VW5oeGxuRWEzbEM1ZktjbmlTZjJYRDhhb0gxSnpXQ21UMkh1ZHAKQ3orM1Z3bUl4RWs1V0NlQmxZZEpOaUkwQnFXV3lOZ2FCc2VPSk5oN2M3ODBvV0F1ZXJ4STFaRkVSUUxHbFBYMgp5cUJVWDEvZVpVbjVMZDlDR0FsS2VJZHJGbGNNRTVvYTJIVUhNWGhBbzVVd0Jlc0NBd0VBQWFNbk1DVXdEZ1lEClZSMFBBUUgvQkFRREFnZUFNQk1HQTFVZEpRUU1NQW9HQ0NzR0FRVUZCd01DTUEwR0NTcUdTSWIzRFFFQkN3VUEKQTRJQ0FRREdHbEVvM2lFRUV3YU5YYVFoRXVuZWJBZW5icXdlMCs1NnpvK2dSK0ZlZUlPckY4TEY4N01hVVEyQQpJaWMyZ3hOTWJvM1dvT2YzVEV0ZE4yZ2NvcVdXaUVrRnZFUVZOY3h2b1c5SGtBZmlxcEFlY2hTSTMxZ3hJZXJICnpzdzJlMWRON3RrMjFpZjRvUElDbUxFZFZOZll0TWVlaS93dEV1Wk8rbEZnRlRpNDBWbzBaNEg1eVMzZnZUc28KaDdFaTVlL0NoTC9vQzNYK05MZ2JyZnhvVFl2MHlzOXEycFgwb0pKMlU3aFlmMXVLViszR0U0RHpLeWxxTmJiMwpRc3hRNGN5bVJsTElsaVE0eDBKbFB5UkhpeDMzUnpsYW9rYkhHcmR0QVlCc1hGL1lxWXl4enNBQmF1SlFBR3QrCnp6WXc0L092Zi9wa0xWY1o5NitNS2Uzb240L3lzTzR2VG83Rk51eFJlUnpXbldmTzZNR0I2aU84WjVBOFRScVUKcGlDeHJBZXdQVHdSZEZqQWxHOFdvOEp6K1FiL3NLL2lOTEZxSWE2OENHZ1huQyttaXRWd21JWGpNNkpiU0dlaAoreGJWaElKOWZtSjYzeFB4dTU5N1VNRG1RV3gyZXRpdnRkZzFqbnovUHZXRi9oQTFxR2FlK0dVekMvVC9nU2FXCnlYdjdZKzQvRDdsTjBsZ2RtTlJFT0oweHpOVTJRUStYUW9TenJEV1lieDFCcHgzS2p5bG1lbEtKUURIR0Q3b0MKMThLTGNLbVFEdkRtMDFyUFRySW9Bd1ljRnNTWG43U29Jd1ZWcnZ5ZDJrdVEzaXVDK00wcWF4Z1l5MWJiclVwTQo5UmgzR1FiWkt3aUFrV01XcC9lVFYwZW9mWUIvMC8zOFl3RThZZUZweHJudFB4Y1duQT09Ci0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0K";
	private static final String ENCODED_INVALID_TEST_CERTIFICATE = "VGVzdENlcnRpZmljYXRl";
	private static final String CSR_TEST_URL = "http://localhost:8081/kyma/v1/certificate?token=testtoken";
	private static final String CRT_TEST_URL = "http://localhost:8081/kyma/v1/certificate";
	private static final String TEST_JSON = "{\"csrUrl\": \"http://localhost:8081/kyma/v1/certificate\", "
			+ "\"api\":{\"metadataUrl\":\"https://gateway.CLUSTER_NAME.kyma.cluster.cx/test/v1/metadata/services\", "
			+ "\"eventsUrl\": \"https://gateway.CLUSTER_NAME.kyma.cluster.cx/test/v1/events\", "
			+ "\"certificatesUrl\":  \"http://localhost:8081/kyma/v1/certificate\"},"
			+ "\"certificate\":{\"subject\":\"OU=C4core,O=SAP,L=Waldorf,ST=Waldorf,C=DE,CN=ec-default\", "
			+ "\"extensions\": \"\", \"key-algorithm\": \"rsa2048\"}}";

	private static final String TEST_JSON_INVALID_PROTOCOL = "{\"csrUrl\": \"http://localhost:8081/kyma/v1/certificate\", "
			+ "\"api\":{\"metadataUrl\":\"http://gateway.CLUSTER_NAME.kyma.cluster.cx/test/v1/metadata/services\", "
			+ "\"eventsUrl\": \"http://gateway.CLUSTER_NAME.kyma.cluster.cx/test/v1/events\", "
			+ "\"certificatesUrl\":  \"http://localhost:8081/kyma/v1/certificate\"},"
			+ "\"certificate\":{\"subject\":\"OU=C4core,O=SAP,L=Waldorf,ST=Waldorf,C=DE,CN=ec-default\", "
			+ "\"extensions\": \"\", \"key-algorithm\": \"rsa2048\"}}";

	private static final String TEST_JSON_INVALID_SUBJECT = "{\"csrUrl\": \"http://localhost:8081/kyma/v1/certificate\", "
			+ "\"api\":{\"metadataUrl\":\"http://gateway.CLUSTER_NAME.kyma.cluster.cx/test/v1/metadata/services\", "
			+ "\"eventsUrl\": \"http://gateway.CLUSTER_NAME.kyma.cluster.cx/test/v1/events\", "
			+ "\"certificatesUrl\":  \"http://localhost:8081/kyma/v1/certificate\"},"
			+ "\"certificate\":{\"subject\":\"OU=C4core,O=SAP,L=Munich,ST=Waldorf,C=DE,CN=ec-default\", "
			+ "\"extensions\": \"\", \"key-algorithm\": \"rsa2048\"}}";

	private static final String TEST_JSON_INVALID_PUBLICKEY_ALGORITHM = "{\"csrUrl\": \"http://localhost:8081/kyma/v1/certificate\", "
			+ "\"api\":{\"metadataUrl\":\"http://gateway.CLUSTER_NAME.kyma.cluster.cx/test/v1/metadata/services\", "
			+ "\"eventsUrl\": \"http://gateway.CLUSTER_NAME.kyma.cluster.cx/test/v1/events\", "
			+ "\"certificatesUrl\":  \"http://localhost:8081/kyma/v1/certificate\"},"
			+ "\"certificate\":{\"subject\":\"OU=C4core,O=SAP,L=Waldorf,ST=Waldorf,C=DE,CN=ec-default\", "
			+ "\"extensions\": \"\", \"key-algorithm\": \"rsa1024\"}}";

	@Resource
	private ModelService modelService;
	@Resource
	private DestinationService<AbstractDestinationModel> destinationService;
	@Resource(name = "kymaCertificateRestTemplate")
	private RestTemplate restTemplate;
	@Resource
	private ValidationService validationService;
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private DefaultCertificateService defaultCertificateService;


	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/test/apiConfigurations.impex", "UTF-8");
		importCsv("/test/constraints.impex", "UTF-8");

		defaultCertificateService = new DefaultCertificateService();
		defaultCertificateService.setModelService(modelService);
		defaultCertificateService.setDestinationService(destinationService);
		defaultCertificateService.setRestTemplate(restTemplate);

	}

	@Test
	public void testRetrieveCertificate() throws CredentialException
	{
		final URI testUri = URI.create(CSR_TEST_URL);
		final MockRestServiceServer mockServer;
		mockServer = MockRestServiceServer.bindTo(restTemplate).build();

		mockServer.expect(times(1), requestTo(testUri)).andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(TEST_JSON, MediaType.APPLICATION_JSON));

		mockServer.expect(times(1), requestTo(CRT_TEST_URL)).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess(generateJsonForCertificate(ENCODED_VALID_TEST_CERTIFICATE), MediaType.APPLICATION_JSON));

		defaultCertificateService = Mockito.spy(new DefaultCertificateService());
		defaultCertificateService.setModelService(modelService);
		defaultCertificateService.setDestinationService(destinationService);
		defaultCertificateService.setRestTemplate(restTemplate);
		doNothing().when(defaultCertificateService).verifyCredential(anyString(), any(), any());

		final ConsumedCertificateCredentialModel consumedCertificateCredentialModel = modelService
				.create(ConsumedCertificateCredentialModel.class);
		consumedCertificateCredentialModel.setId("testCertificate");

		defaultCertificateService.retrieveCertificate(testUri, consumedCertificateCredentialModel);

		assertEquals(consumedCertificateCredentialModel.getCertificateData(), ENCODED_VALID_TEST_CERTIFICATE);
	}

	@Test
	public void testRetrieveCertificateWithInvalidProtocol() throws CredentialException
	{
		final URI testUri = URI.create(CSR_TEST_URL);
		final MockRestServiceServer mockServer;
		mockServer = MockRestServiceServer.bindTo(restTemplate).build();

		mockServer.expect(times(1), requestTo(testUri)).andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(TEST_JSON_INVALID_PROTOCOL, MediaType.APPLICATION_JSON));

		mockServer.expect(times(1), requestTo(CRT_TEST_URL)).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess(generateJsonForCertificate(ENCODED_INVALID_TEST_CERTIFICATE), MediaType.APPLICATION_JSON));

		defaultCertificateService = Mockito.spy(new DefaultCertificateService());
		defaultCertificateService.setModelService(modelService);
		defaultCertificateService.setDestinationService(destinationService);
		defaultCertificateService.setRestTemplate(restTemplate);
		doNothing().when(defaultCertificateService).verifyCredential(anyString(), any(), any());

		final ConsumedCertificateCredentialModel consumedCertificateCredentialModel = modelService
				.create(ConsumedCertificateCredentialModel.class);
		consumedCertificateCredentialModel.setId("testCertificate");

		final AbstractDestinationModel eventsDestination = destinationService.getDestinationById(DEFAULT_EVENTS_DESTINATION_ID);
		final AbstractDestinationModel servicesDestination = destinationService.getDestinationById(DEFAULT_SERVICES_DESTINATION_ID);
		eventsDestination.setCredential(consumedCertificateCredentialModel);
		servicesDestination.setCredential(consumedCertificateCredentialModel);

		validationService.reloadValidationEngine();

		expectedException.expectCause(allOf(instanceOf(ModelSavingException.class)));

		defaultCertificateService.retrieveCertificate(testUri, consumedCertificateCredentialModel);
	}

	@Test
	public void testRetrieveCertificateWithInvalidKeyPairs() throws CredentialException
	{
		final URI testUri = URI.create(CSR_TEST_URL);
		final MockRestServiceServer mockServer;

		mockServer = MockRestServiceServer.bindTo(restTemplate).build();

		mockServer.expect(times(1), requestTo(testUri)).andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(TEST_JSON, MediaType.APPLICATION_JSON));

		mockServer.expect(times(1), requestTo(CRT_TEST_URL)).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess(generateJsonForCertificate(ENCODED_VALID_TEST_CERTIFICATE), MediaType.APPLICATION_JSON));

		final ConsumedCertificateCredentialModel consumedCertificateCredentialModel = modelService
				.create(ConsumedCertificateCredentialModel.class);
		consumedCertificateCredentialModel.setId("testCertificate");

		expectedException.expect(CredentialException.class);
		expectedException.expectMessage("Credential verification is failed. Public key and private key don't match");

		defaultCertificateService.retrieveCertificate(testUri, consumedCertificateCredentialModel);
	}

	@Test
	public void testRetrieveCertificateWithInvalidSubject() throws CredentialException
	{
		final URI testUri = URI.create(CSR_TEST_URL);
		final MockRestServiceServer mockServer;
		mockServer = MockRestServiceServer.bindTo(restTemplate).build();

		mockServer.expect(times(1), requestTo(testUri)).andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(TEST_JSON_INVALID_SUBJECT, MediaType.APPLICATION_JSON));

		mockServer.expect(times(1), requestTo(CRT_TEST_URL)).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess(generateJsonForCertificate(ENCODED_VALID_TEST_CERTIFICATE), MediaType.APPLICATION_JSON));

		final ConsumedCertificateCredentialModel consumedCertificateCredentialModel = modelService
				.create(ConsumedCertificateCredentialModel.class);
		consumedCertificateCredentialModel.setId("testCertificate");

		expectedException.expect(CredentialException.class);
		expectedException.expectMessage("Credential verification is failed. Certificate subject is not valid");

		defaultCertificateService.retrieveCertificate(testUri, consumedCertificateCredentialModel);
	}

	@Test
	public void testRetrieveCertificateWithInvalidPublicKeyAlgorithm() throws CredentialException
	{
		final URI testUri = URI.create(CSR_TEST_URL);
		final MockRestServiceServer mockServer;
		mockServer = MockRestServiceServer.bindTo(restTemplate).build();

		mockServer.expect(times(1), requestTo(testUri)).andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(TEST_JSON_INVALID_PUBLICKEY_ALGORITHM, MediaType.APPLICATION_JSON));

		mockServer.expect(times(1), requestTo(CRT_TEST_URL)).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess(generateJsonForCertificate(ENCODED_VALID_TEST_CERTIFICATE), MediaType.APPLICATION_JSON));

		final ConsumedCertificateCredentialModel consumedCertificateCredentialModel = modelService
				.create(ConsumedCertificateCredentialModel.class);
		consumedCertificateCredentialModel.setId("testCertificate");

		expectedException.expect(CredentialException.class);
		expectedException.expectMessage("Credential verification is failed. Public key algorithm is not valid");

		defaultCertificateService.retrieveCertificate(testUri, consumedCertificateCredentialModel);
	}

	private String generateJsonForCertificate(final String certificate)
	{
		return "{\"crt\":\"" + certificate + "\"}";
	}

}
