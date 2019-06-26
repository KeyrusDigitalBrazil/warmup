/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.smarteditwebservices.controllers;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.smarteditwebservices.constants.SmarteditwebservicesConstants;
import de.hybris.platform.smarteditwebservices.data.ConfigurationData;
import de.hybris.platform.smarteditwebservices.dto.ConfigurationDataListWsDto;
import de.hybris.platform.smarteditwebservices.model.SmarteditConfigurationModel;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ SmarteditwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class ConfigurationControllerWebServiceTest extends ServicelayerTest
{
	public static final String OAUTH_CLIENT_ID = "trusted_client";
	public static final String OAUTH_CLIENT_PASS = "secret";
	public static final String OAUTH_ADMIN_ID = "admin";
	public static final String OAUTH_ADMIN_PASS = "nimda";
	public static final String OAUTH_CMSMANAGER_ID = "cmsmanager";
	public static final String OAUTH_CMSMANAGER_PASS = "1234";
	public static final String OAUTH_NOBODY_ID = "nobody";
	public static final String OAUTH_NOBODY_PASS = "1234";

	private static final String URI = "v1/configurations";
	private static final String CONFIG_KEY_1 = "key1";
	private static final String CONFIG_VALUE_1 = "value1";

	@Resource
	private ModelService modelService;

	private WsSecuredRequestBuilder wsSecuredRequestBuilder;

	@Before
	public void setUpUsers() throws Exception
	{
		importCsv("/smarteditwebservices/test/impex/essentialTestDataAuth.impex", "utf-8");
	}

	@Before
	public void setUpConfigurations()
	{
		final SmarteditConfigurationModel config1 = modelService.create(SmarteditConfigurationModel.class);
		config1.setKey(CONFIG_KEY_1);
		config1.setValue(CONFIG_VALUE_1);

		final SmarteditConfigurationModel config2 = modelService.create(SmarteditConfigurationModel.class);
		config2.setKey("key2");
		config2.setValue("value2");

		modelService.saveAll(config1, config2);
	}

	protected void setUpWebRequest(final String username, final String password)
	{
		wsSecuredRequestBuilder = new WsSecuredRequestBuilder() //
				.extensionName(SmarteditwebservicesConstants.EXTENSIONNAME) //
				.client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS) //
				.resourceOwner(username, password) //
				.grantResourceOwnerPasswordCredentials();
	}

	@Test
	public void shouldGetConfigurations_CmsManagerGroup()
	{
		setUpWebRequest(OAUTH_CMSMANAGER_ID, OAUTH_CMSMANAGER_PASS);
		final Response allResponse = wsSecuredRequestBuilder //
				.path(URI) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, allResponse);

		final ConfigurationDataListWsDto allEntity = allResponse.readEntity(ConfigurationDataListWsDto.class);
		assertEquals(2, allEntity.getConfigurations().size());

		final Response singleResponse = wsSecuredRequestBuilder //
				.path(CONFIG_KEY_1) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, singleResponse);

		final ConfigurationData singleEntity = singleResponse.readEntity(ConfigurationData.class);
		assertEquals(CONFIG_VALUE_1, singleEntity.getValue());
	}

	@Test
	public void shouldFailGetConfigurations_NoAccess()
	{
		setUpWebRequest(OAUTH_NOBODY_ID, OAUTH_NOBODY_PASS);
		final Response allResponse = wsSecuredRequestBuilder //
				.path(URI) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();
		Assert.assertEquals(401, allResponse.getStatus());

		final Response singleResponse = wsSecuredRequestBuilder //
				.path(CONFIG_KEY_1) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();
		Assert.assertEquals(401, singleResponse.getStatus());
	}

	@Test
	public void shouldCreateUpdateDeleteConfigurations_AdminGroup()
	{
		setUpWebRequest(OAUTH_ADMIN_ID, OAUTH_ADMIN_PASS);

		final ConfigurationData entity = buildConfig();

		final Response createResponse = wsSecuredRequestBuilder //
				.path(URI) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(entity, MediaType.APPLICATION_JSON));
		assertResponse(Status.OK, createResponse);

		entity.setValue("value4");
		final ConfigurationData updateResponse = wsSecuredRequestBuilder //
				.path("key3") //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(entity, MediaType.APPLICATION_JSON), ConfigurationData.class);
		assertEquals("value4", updateResponse.getValue());

		final Response deleteResponse = wsSecuredRequestBuilder //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.delete();
		assertResponse(Status.OK, deleteResponse);

		final Response getResponse = wsSecuredRequestBuilder //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();
		assertEquals(404, getResponse.getStatus());
	}

	@Test
	public void shouldFailCreateUpdateDeleteConfigurations_CmsManagerNoAccess()
	{
		setUpWebRequest(OAUTH_CMSMANAGER_ID, OAUTH_CMSMANAGER_PASS);

		final ConfigurationData entity = buildConfig();

		final Response createResponse = wsSecuredRequestBuilder //
				.path(URI) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(entity, MediaType.APPLICATION_JSON));
		assertEquals(401, createResponse.getStatus());

		final Response updateResponse = wsSecuredRequestBuilder //
				.path(CONFIG_KEY_1) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(entity, MediaType.APPLICATION_JSON));
		assertEquals(401, updateResponse.getStatus());

		final Response deleteResponse = wsSecuredRequestBuilder //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.delete();
		assertEquals(401, deleteResponse.getStatus());
	}

	@Test
	public void shouldFailCreateConfigurations_DuplicateKey()
	{
		setUpWebRequest(OAUTH_ADMIN_ID, OAUTH_ADMIN_PASS);

		final ConfigurationData entity = buildConfig();
		entity.setKey(CONFIG_KEY_1);
		final Response response = wsSecuredRequestBuilder //
				.path(URI) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(entity, MediaType.APPLICATION_JSON));
		assertEquals(409, response.getStatus());

	}

	protected ConfigurationData buildConfig()
	{
		final ConfigurationData entity = new ConfigurationData();
		entity.setKey("key3");
		entity.setValue("value3");
		return entity;
	}

}
