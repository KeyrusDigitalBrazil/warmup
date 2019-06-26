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
package de.hybris.platform.webservicescommons.webservices;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.client.WsRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.Map;
import java.util.Optional;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions = "oauth2")
@IntegrationTest
public class OAuthAuthorizationWebServiceTest extends ServicelayerTest
{
	private static final String URI = "oauth/token";

	private static final String GRANT_TYPE_CLIENT_CRIDENTIALS = "client_credentials";
	private static final String GRANT_TYPE_PASSWORD = "password";
	private static final String CLIENT_ID = "mobile_android";
	private static final String CLIENT_SECRET = "secret";
	private static final String CUSTOMER_USERNAME = "testoauthcustomer";
	private static final String CUSTOMER_PASSWORD = "1234";

	private WsRequestBuilder wsRequestBuilder;
	private WsRequestBuilder wsRequestBuilderWO;

	@Before
	public void setUp() throws Exception
	{
		wsRequestBuilderWO = new WsRequestBuilder().extensionName("oauth2");
		wsRequestBuilder = new WsRequestBuilder().extensionName("oauth2").path(URI);

		createCoreData();
		createDefaultUsers();
		importData(new ClasspathImpExResource("/webservicescommons/test/democustomer-data.impex", "UTF-8"));
	}

	@Test
	public void testGetTokenUsingClientCredentials()
	{
		//given
		final Response result = wsRequestBuilder.queryParam("grant_type", GRANT_TYPE_CLIENT_CRIDENTIALS)
				.queryParam("client_id", CLIENT_ID).queryParam("client_secret", CLIENT_SECRET).build()
				//when
				.post(Entity.entity(null, MediaType.APPLICATION_JSON));
		result.bufferEntity();

		//then
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void testGetTokenUsingPassword()
	{
		//given
		final Response result = wsRequestBuilder.queryParam("grant_type", GRANT_TYPE_PASSWORD)
				.queryParam("username", CUSTOMER_USERNAME).queryParam("password", CUSTOMER_PASSWORD)
				.queryParam("client_id", CLIENT_ID).queryParam("client_secret", CLIENT_SECRET).build()
				.accept(MediaType.APPLICATION_JSON)
				//when
				.post(Entity.entity(null, MediaType.APPLICATION_JSON));
		result.bufferEntity();

		//then
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void testGetTokenUsingWrongClientID()
	{
		//given
		final Response result = wsRequestBuilder.queryParam("grant_type", GRANT_TYPE_CLIENT_CRIDENTIALS)
				.queryParam("client_id", "WRONG").queryParam("client_secret", CLIENT_SECRET).build()
				.accept(MediaType.APPLICATION_JSON)
				//when
				.post(Entity.entity(null, MediaType.APPLICATION_JSON));
		result.bufferEntity();

		//then
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, Optional.empty(), result);
		assertTrue(result.hasEntity());
		assertTrue(StringUtils.contains(result.readEntity(String.class), "invalid_client"));
	}

	@Test
	public void testGetTokenUsingWrongClientSecret()
	{
		//given
		final Response result = wsRequestBuilder.queryParam("grant_type", GRANT_TYPE_CLIENT_CRIDENTIALS)
				.queryParam("client_id", CLIENT_ID).queryParam("client_secret", "WRONG").build().accept(MediaType.APPLICATION_JSON)
				//when
				.post(Entity.entity(null, MediaType.APPLICATION_JSON));
		result.bufferEntity();
		//then
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, Optional.empty(), result);
		assertTrue(result.hasEntity());
		assertTrue(StringUtils.contains(result.readEntity(String.class), "invalid_client"));
	}

	@Test
	public void testGetTokenUsingWrongGrantType()
	{
		//given
		final Response result = wsRequestBuilder.queryParam("grant_type", "WRONG").queryParam("client_id", CLIENT_ID)
				.queryParam("client_secret", CLIENT_SECRET).build().accept(MediaType.APPLICATION_JSON)
				//when
				.post(Entity.entity(null, MediaType.APPLICATION_JSON));
		result.bufferEntity();
		//then
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, Optional.empty(), result);
		assertTrue(result.hasEntity());
		assertTrue(StringUtils.contains(result.readEntity(String.class), "unsupported_grant_type"));
	}
}
