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
package de.hybris.platform.kymaintegrationservices.populators;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.EndpointModel;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.kymaintegrationservices.dto.ApiSpecificationData;
import de.hybris.platform.kymaintegrationservices.dto.OAuthData;
import de.hybris.platform.kymaintegrationservices.dto.ServiceRegistrationData;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.Utilities;
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.ObjectMapper;


@UnitTest
public class WebserviceSpecificationPopulatorTest
{
	private static final String TEST_DESCRIPTION = "testDescription";
	private static final String TEST_NAME = "testName";
	private static final String TEST_ID = "testId";
	private static final String TEST_SOURCE_URL = "testSourceUrl";
	private static final String TEST_OAUTH_URL = "test oauth url";
	private static final String TEST_CLIENT_ID = "test client id";
	private static final String TEST_CLIENT_SECRET = "test client secret";
	private static final String TEST_PROVIDER = "SAP Hybris";
	private static final String TEST_SPEC_TEXT = "{\"specText\":\"spec text\"}";
	private static final String TEST_SPEC_URL = "test spec url";
	private static final String TEST_SPEC_TEXT_FROM_URL = "{\"spec\":\"spec text from url\"}";
	private static final String PROVIDER_PROP = "kymaintegrationservices.kyma-specification-provider";
	private static final String TEST_VERSION = "v2";

	private final WebserviceSpecificationPopulator populatorOriginal = new WebserviceSpecificationPopulator();

	private WebserviceSpecificationPopulator populator;
	@Mock
	private ExposedDestinationModel destinationModel;
	@Mock
	private EndpointModel endpointModel;
	@Mock
	private ExposedOAuthCredentialModel oAuthCredentialModel;
	@Mock
	private OAuthClientDetailsModel oauthClientDetails;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		Registry.activateStandaloneMode();
		Utilities.setJUnitTenant();


		populatorOriginal.setJacksonObjectMapper(objectMapper);
		populator = spy(populatorOriginal);
		doReturn(TEST_SPEC_TEXT_FROM_URL).when(populator).getSpecFromUrl(TEST_SPEC_URL);

		Config.setParameter(PROVIDER_PROP, TEST_PROVIDER);

		when(destinationModel.getEndpoint()).thenReturn(endpointModel);
		when(destinationModel.getId()).thenReturn(TEST_ID);
		when(destinationModel.getUrl()).thenReturn(TEST_SOURCE_URL);
		when(endpointModel.getDescription()).thenReturn(TEST_DESCRIPTION);
		when(endpointModel.getVersion()).thenReturn(TEST_VERSION);
		when(endpointModel.getSpecUrl()).thenReturn(TEST_SPEC_URL);
		when(endpointModel.getName()).thenReturn(TEST_NAME);

		when(destinationModel.getCredential()).thenReturn(oAuthCredentialModel);
		when(oAuthCredentialModel.getOAuthClientDetails()).thenReturn(oauthClientDetails);
		when(oAuthCredentialModel.getPassword()).thenReturn(TEST_CLIENT_SECRET);

		when(oauthClientDetails.getOAuthUrl()).thenReturn(TEST_OAUTH_URL);
		when(oauthClientDetails.getClientId()).thenReturn(TEST_CLIENT_ID);
		when(oauthClientDetails.getClientSecret()).thenReturn(TEST_CLIENT_SECRET);
	}

	@Test
	public void populateApiSpecificationWithSpecTextFromUrl()
	{
		final ServiceRegistrationData serviceRegistrationData = new ServiceRegistrationData();
		populator.populate(destinationModel, serviceRegistrationData);
		assertEquals(serviceRegistrationData.getDescription(), TEST_DESCRIPTION);
		assertEquals(serviceRegistrationData.getIdentifier(), TEST_ID + "-" + TEST_VERSION);
		assertEquals(serviceRegistrationData.getProvider(), TEST_PROVIDER);
		assertEquals(serviceRegistrationData.getName(), TEST_NAME);

		final ApiSpecificationData apiSpecification = serviceRegistrationData.getApi();
		assertEquals(apiSpecification.getTargetUrl(), TEST_SOURCE_URL);
		assertEquals(apiSpecification.getSpec().toString(), TEST_SPEC_TEXT_FROM_URL);

		final OAuthData oauth = apiSpecification.getCredentials().getOauth();
		assertEquals(oauth.getClientId(), TEST_CLIENT_ID);
		assertEquals(oauth.getClientSecret(), TEST_CLIENT_SECRET);
		assertEquals(oauth.getUrl(), TEST_OAUTH_URL);
	}

	@Test
	public void popupateApiSpecificationWithSpecText()
	{
		when(endpointModel.getSpecData()).thenReturn(TEST_SPEC_TEXT);
		final ServiceRegistrationData serviceRegistrationData = new ServiceRegistrationData();
		populator.populate(destinationModel, serviceRegistrationData);

		final ApiSpecificationData apiSpecification = serviceRegistrationData.getApi();
		assertEquals(apiSpecification.getTargetUrl(), TEST_SOURCE_URL);
		assertEquals(apiSpecification.getSpec().toString(), TEST_SPEC_TEXT);
	}
}
