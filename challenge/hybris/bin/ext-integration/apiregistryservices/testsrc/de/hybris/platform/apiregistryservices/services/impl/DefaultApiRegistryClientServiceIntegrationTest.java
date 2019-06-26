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

package de.hybris.platform.apiregistryservices.services.impl;


import static de.hybris.platform.apiregistryservices.services.impl.DefaultApiRegistryClientService.CLIENT_SCOPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.apiregistryservices.dao.DestinationDao;
import de.hybris.platform.apiregistryservices.factory.client.ProfileDataServiceClient;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.Http;
import com.hybris.charon.annotations.OAuth;


public class DefaultApiRegistryClientServiceIntegrationTest extends ServicelayerTest
{

	@Resource(name = "defaultApiRegistryClientService")
	private ApiRegistryClientService apiRegistryClientService;

	@Resource
	private DestinationDao destinationDao;

	@Resource
	private ModelService modelService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Before
	public void before() throws ImpExException
	{
		importCsv("/test/charonConfiguration.impex", "UTF-8");
	}

	@Test
	public void testLookupClient() throws Exception
	{
		final ProfileDataServiceClient ProfileDataServiceClient = apiRegistryClientService
				.lookupClient(ProfileDataServiceClient.class);

		assertNotNull(ProfileDataServiceClient);
	}

	@Test(expected = ModelNotFoundException.class)
	public void testLookupClient_WithoutDestination() throws Exception
	{
		final ConsumedDestinationModel consumedDestination = getConsumedDestinationById("ProfileDataServiceClient");
		modelService.remove(consumedDestination);

		apiRegistryClientService.lookupClient(ProfileDataServiceClient.class);
	}

	@Test
	public void testLookupClient_WithCredentialWhichHasNoStrategy() throws Exception
	{
		final ProfileDataServiceClient_basicCreds client = apiRegistryClientService
				.lookupClient(ProfileDataServiceClient_basicCreds.class);
		assertNotNull(client);

		final ConsumedDestinationModel consumedDestination = getConsumedDestinationById("ProfileDataServiceClient_basicCreds");

		final Map<String, String> clientConfig = apiRegistryClientService
				.buildClientConfig(ProfileDataServiceClient_basicCreds.class, consumedDestination);
		assertTrue(clientConfig.containsKey(DefaultApiRegistryClientService.CLIENT_URL));
		assertTrue(clientConfig.containsKey(DefaultApiRegistryClientService.TENANT));
		assertFalse(clientConfig.containsKey(DefaultApiRegistryClientService.OAUTH_URL));
		assertFalse(clientConfig.containsKey(DefaultApiRegistryClientService.OAUTH_CLIENT_ID));
		assertFalse(clientConfig.containsKey(DefaultApiRegistryClientService.OAUTH_CLIENT_ID));
	}

	@Test
	public void testLookupClient_WithNullOAuthScope() throws Exception
	{
		final ProfileDataServiceClient_basicCreds client = apiRegistryClientService
				.lookupClient(ProfileDataServiceClient_basicCreds.class);
		assertNotNull(client);

		final ConsumedDestinationModel consumedDestination = getConsumedDestinationById("ProfileDataServiceClient_noScope");

		assertNull(consumedDestination.getAdditionalProperties().get(CLIENT_SCOPE));

		final Map<String, String> clientConfig = apiRegistryClientService
				.buildClientConfig(ProfileDataServiceClient_basicCreds.class, consumedDestination);
		assertEquals(StringUtils.EMPTY, clientConfig.get(CLIENT_SCOPE));
	}

	protected ConsumedDestinationModel getConsumedDestinationById(final String destinationId)
	{
		ConsumedDestinationModel consumedDestination = new ConsumedDestinationModel();
		consumedDestination.setId(destinationId);
		consumedDestination = flexibleSearchService.getModelByExample(consumedDestination);
		return consumedDestination;
	}


	@OAuth
	@Http
	public interface ProfileDataServiceClient_basicCreds
	{
		@GET
		@Control(retries = "${retries:0}", retriesInterval = "${retriesInterval:500}", timeout = "${timeout:2000}")
		@Path("/${tenant}/profiles/{id}")
		Object getSmth(@PathParam("id") String id);
	}
}

