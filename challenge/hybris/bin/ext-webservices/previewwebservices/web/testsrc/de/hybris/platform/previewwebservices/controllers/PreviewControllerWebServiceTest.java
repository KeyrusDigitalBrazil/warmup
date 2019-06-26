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
package de.hybris.platform.previewwebservices.controllers;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.previewwebservices.constants.PreviewwebservicesConstants;
import de.hybris.platform.previewwebservices.dto.PreviewTicketWsDTO;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ PreviewwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class PreviewControllerWebServiceTest extends ServicelayerTest
{

	private static final String URI = "v1/preview";

	public static final String OAUTH_CLIENT_ID = "mobile_android";
	public static final String OAUTH_CLIENT_PASS = "secret";
	public static final String USER = "previewmanager";
	public static final String PASSWORD = "1234";
	public static final String RESOURCE_PATH = "https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite";
	public static final String BAD_RESOURCE_PATH = "https://127.0.0.1:9002/yacceleratorstorefront?site=notexistingsite";
	private static final String NOT_EXISTING_SCOPE = "not_existing_scope";
	private static final String BASIC_SCOPE = "basic";

	public static final String ADMIN_USER = "admin";
	public static final String ADMIN_PASSWORD = "nimda";

	public static final String REASON_MISSING = "missing";
	public static final String REASON_INVALID = "invalid";

	public static final String TICKETID = "ticketId";
	public static final String INVALID_TICKETID = "invalidTicketId";

	public static final String CATALOG = "catalog";
	public static final String CATALOG_VERSION = "catalogVersion";
	public static final String CATALOG_VERSIONS = "catalogVersions";
	public static final String TIME = "time";
	public static final String CATALOG_ID = "testContentCatalog";
	public static final String WRONG_CATALOG_ID = "testwrongcatalog";
	public static final String ONLINE_CATALOG_VERSION = "Online";

	public static final String PAGEID = "pageId";
	public static final String HOMEPAGE = "homepage";

	public static final String USER_KEY = "user";
	public static final String USER_ID = "testoauthcustomer";
	public static final String USER_GROUP_KEY = "userGroup";
	public static final String USER_GROUP_ID = "regulargroup";

	public static final String VALIDATION_ERROR = "ValidationError";
	public static final String CONVERSION_ERROR = "ConversionError";


	private WsSecuredRequestBuilder wsSecuredRequestBuilder;

	@Before
	public void setUp() throws Exception
	{
		wsSecuredRequestBuilder = new WsSecuredRequestBuilder()//
				.extensionName(PreviewwebservicesConstants.EXTENSIONNAME)//
				.path(URI)//
				.client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS);

		importCsv("/previewwebservices/test/essentialTestDataAuth.impex", "utf-8");
	}

	@Test
	public void testPostEmptyEntityForValidationErrors()
	{
		// WHEN
		final Response response = authorizeAndPost(new HashMap<String, String>());

		// THEN
		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO result = response.readEntity(ErrorListWsDTO.class);
		assertThat(result.getErrors(), iterableWithSize(1));

		final Set<String> subjects = result.getErrors().stream().map(ErrorWsDTO::getSubject).collect(Collectors.toSet());
		final Set<String> reasons = result.getErrors().stream().map(ErrorWsDTO::getReason).collect(Collectors.toSet());
		assertThat(subjects.contains(PreviewDataModel.RESOURCEPATH), is(true));
		assertThat(reasons.contains(REASON_MISSING), is(true));

	}

	@Test
	public void testPostForTicketMinimum()
	{
		// GIVEN
		final Map requestBody = new HashMap<String, Object>();
		requestBody.put(PreviewDataModel.RESOURCEPATH, RESOURCE_PATH);

		// WHEN
		final Response response = authorizeAndPost(requestBody);

		// THEN
		assertResponse(Response.Status.CREATED, response);

		final PreviewTicketWsDTO result = response.readEntity(PreviewTicketWsDTO.class);
		assertThat(result, allOf(hasProperty(PreviewDataModel.RESOURCEPATH, is(RESOURCE_PATH)), //
				hasProperty(TICKETID)));
	}

	@Test
	public void testPostWrongCatalogWillThrowValidationError()
	{
		// GIVEN
		final Map catalogVersion = new HashMap<String, Object>();
		catalogVersion.put(CATALOG, WRONG_CATALOG_ID);
		catalogVersion.put(CATALOG_VERSION, ONLINE_CATALOG_VERSION);

		final Map requestBody = new HashMap<String, Object>();
		requestBody.put(PreviewDataModel.RESOURCEPATH, RESOURCE_PATH);
		requestBody.put(CATALOG_VERSIONS, Arrays.asList(catalogVersion));

		// WHEN
		final Response response = authorizeAndPost(requestBody);

		// THEN
		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO result = response.readEntity(ErrorListWsDTO.class);
		assertThat(result.getErrors(), iterableWithSize(1));

		final Set<String> types = result.getErrors().stream().map(ErrorWsDTO::getType).collect(Collectors.toSet());
		final Set<String> reasons = result.getErrors().stream().map(ErrorWsDTO::getReason).collect(Collectors.toSet());
		assertThat(types.contains(VALIDATION_ERROR), is(true));
		assertThat(reasons.contains(REASON_INVALID), is(true));
	}

	@Test
	public void testBadResourcePathWillThrowConversionError()
	{
		// GIVEN
		final Map requestBody = new HashMap<String, Object>();
		requestBody.put(PreviewDataModel.RESOURCEPATH, BAD_RESOURCE_PATH);

		// WHEN
		final Response response = authorizeAndPost(requestBody);

		// THEN
		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO result = response.readEntity(ErrorListWsDTO.class);
		assertThat(result.getErrors(), iterableWithSize(1));

		final Set<String> types = result.getErrors().stream().map(ErrorWsDTO::getType).collect(Collectors.toSet());
		assertThat(types.contains(CONVERSION_ERROR), is(true));

	}

	@Test
	public void testPostForTicketWithPageWillBeSuccessful()
	{
		// GIVEN
		final Map catalogVersion = new HashMap<String, Object>();
		catalogVersion.put(CATALOG, CATALOG_ID);
		catalogVersion.put(CATALOG_VERSION, ONLINE_CATALOG_VERSION);

		final Map requestBody = new HashMap<String, Object>();
		requestBody.put(PreviewDataModel.RESOURCEPATH, RESOURCE_PATH);
		requestBody.put(CATALOG_VERSIONS, Arrays.asList(catalogVersion));
		requestBody.put(PAGEID, HOMEPAGE);

		// WHEN
		final Response response = authorizeAndPost(requestBody);

		// THEN
		assertResponse(Response.Status.CREATED, response);

		final PreviewTicketWsDTO result = response.readEntity(PreviewTicketWsDTO.class);
		assertThat(result, allOf(hasProperty(PreviewDataModel.RESOURCEPATH, is(RESOURCE_PATH)), //
				hasProperty(TICKETID), //
				hasProperty(PAGEID, is(HOMEPAGE))));
	}

	@Test
	public void testPostForTicketWithEverything()
	{
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		final String currentTime = dateFormat.format(new Date().getTime());

		// GIVEN
		final Map catalogVersion = new HashMap<String, Object>();
		catalogVersion.put(CATALOG, CATALOG_ID);
		catalogVersion.put(CATALOG_VERSION, ONLINE_CATALOG_VERSION);

		final Map requestBody = new HashMap<String, Object>();
		requestBody.put(PreviewDataModel.RESOURCEPATH, RESOURCE_PATH);
		requestBody.put(CATALOG_VERSIONS, Arrays.asList(catalogVersion));
		requestBody.put(PAGEID, HOMEPAGE);
		requestBody.put(USER_KEY, USER_ID);
		requestBody.put(USER_GROUP_KEY, USER_GROUP_ID);
		requestBody.put(TIME, currentTime);

		// WHEN
		final Response response = authorizeAndPost(requestBody);

		// THEN
		assertResponse(Response.Status.CREATED, response);

		final PreviewTicketWsDTO result = response.readEntity(PreviewTicketWsDTO.class);
		assertThat(result, allOf(hasProperty(PreviewDataModel.RESOURCEPATH, is(RESOURCE_PATH)), //
				hasProperty(TICKETID), //
				hasProperty(TIME), //
				hasProperty(USER_KEY, is(USER_ID)), //
				hasProperty(PAGEID, is(HOMEPAGE))));

	}

	@Test
	public void testPostShouldReturn400ForRandomUser()
	{
		// GIVEN
		final Map requestBody = new HashMap<String, Object>();
		requestBody.put(PreviewDataModel.RESOURCEPATH, BAD_RESOURCE_PATH);

		// WHEN
		final Response response = authorizeAndPost(requestBody);

		// THEN
		assertResponse(Status.BAD_REQUEST, response);
	}

	@Test
	public void testPostShouldShouldReturn401ForAdminWithNotExistingScope() throws IOException
	{
		// GIVEN
		final Map requestBody = new HashMap<String, Object>();
		requestBody.put(PreviewDataModel.RESOURCEPATH, RESOURCE_PATH);

		// WHEN
		final Response response = wsSecuredRequestBuilder//
				.resourceOwner(ADMIN_USER, ADMIN_PASSWORD)//
				.scope(NOT_EXISTING_SCOPE).grantResourceOwnerPasswordCredentials()//
				.build()//
				.post(Entity.entity(requestBody, MediaType.APPLICATION_JSON));

		// THEN
		assertResponse(Response.Status.UNAUTHORIZED, response);
	}

	@Test
	public void testPostShouldReturn201ForAdminWithProperScope() throws IOException
	{
		// GIVEN
		final Map requestBody = new HashMap<String, Object>();
		requestBody.put(PreviewDataModel.RESOURCEPATH, RESOURCE_PATH);

		// WHEN
		final Response response = wsSecuredRequestBuilder//
				.resourceOwner(ADMIN_USER, ADMIN_PASSWORD)//
				.scope(PreviewwebservicesConstants.EXTENSIONNAME).grantResourceOwnerPasswordCredentials()//
				.build()//
				.post(Entity.entity(requestBody, MediaType.APPLICATION_JSON));

		// THEN
		assertResponse(Response.Status.CREATED, response);
	}

	@Test
	public void testPostShouldReturn403ForAdminWithBasicScope() throws IOException
	{
		// GIVEN
		final Map requestBody = new HashMap<String, Object>();
		requestBody.put(PreviewDataModel.RESOURCEPATH, RESOURCE_PATH);

		// WHEN
		final Response response = wsSecuredRequestBuilder//
				.resourceOwner(ADMIN_USER, ADMIN_PASSWORD)//
				.scope(BASIC_SCOPE).grantResourceOwnerPasswordCredentials()//
				.build()//
				.post(Entity.entity(requestBody, MediaType.APPLICATION_JSON));

		// THEN
		assertResponse(Response.Status.FORBIDDEN, response);
	}

	@Test
	public void testGetWithValidTicketIdShouldBeSuccessful()
	{
		// GIVEN
		final Map requestBody = new HashMap<String, Object>();
		requestBody.put(PreviewDataModel.RESOURCEPATH, RESOURCE_PATH);
		Response response = authorizeAndPost(requestBody);

		final PreviewTicketWsDTO result = response.readEntity(PreviewTicketWsDTO.class);
		final String ticketId = result.getTicketId();

		// WHEN
		response = wsSecuredRequestBuilder.resourceOwner(USER, PASSWORD)//
				.grantResourceOwnerPasswordCredentials()//
				.path(ticketId)//
				.build()//
				.get();

		// THEN
		assertResponse(Response.Status.OK, response);

	}

	@Test
	public void testGetWithInvalidTicketIdShouldFail()
	{
		// WHEN
		final Response response = wsSecuredRequestBuilder.resourceOwner(USER, PASSWORD)//
				.grantResourceOwnerPasswordCredentials()//
				.path(INVALID_TICKETID)//
				.build()//
				.get();

		// THEN
		assertResponse(Response.Status.NOT_FOUND, response);

	}

	@Test
	public void testPutWithValidTicketIdShouldBeSuccessful()
	{
		// GIVEN
		final Map postRequestBody = new HashMap<String, Object>();
		postRequestBody.put(PreviewDataModel.RESOURCEPATH, RESOURCE_PATH);
		Response response = authorizeAndPost(postRequestBody);

		final PreviewTicketWsDTO result = response.readEntity(PreviewTicketWsDTO.class);
		final String ticketId = result.getTicketId();

		// WHEN
		final Map putRequestBody = new HashMap<String, Object>();
		putRequestBody.put(PreviewDataModel.RESOURCEPATH, RESOURCE_PATH);
		putRequestBody.put(PAGEID, HOMEPAGE);

		response = authorizeAndPut(ticketId, putRequestBody);

		// THEN
		assertResponse(Response.Status.OK, response);

		final PreviewTicketWsDTO putResult = response.readEntity(PreviewTicketWsDTO.class);
		assertThat(putResult, allOf(hasProperty(PreviewDataModel.RESOURCEPATH, is(RESOURCE_PATH)), //
				hasProperty(TICKETID), //
				hasProperty(PAGEID, is(HOMEPAGE))));

	}

	@Test
	public void testPutWithInvalidTicketIdShouldFail()
	{
		// WHEN
		final Map putRequestBody = new HashMap<String, Object>();
		putRequestBody.put(PreviewDataModel.RESOURCEPATH, RESOURCE_PATH);
		putRequestBody.put(PAGEID, HOMEPAGE);

		final Response response = authorizeAndPut(INVALID_TICKETID, putRequestBody);

		// THEN
		assertResponse(Response.Status.NOT_FOUND, response);

	}

	protected Response authorizeAndPost(final Map requestBody)
	{
		final Response result = wsSecuredRequestBuilder//
				.resourceOwner(USER, PASSWORD)//
				.grantResourceOwnerPasswordCredentials()//
				.build()//
				.post(Entity.entity(requestBody, MediaType.APPLICATION_JSON));
		return result;
	}

	protected Response authorizeAndPut(final String ticketId, final Map requestBody)
	{
		final Response result = wsSecuredRequestBuilder//
				.resourceOwner(USER, PASSWORD)//
				.grantResourceOwnerPasswordCredentials()//
				.path(ticketId)//
				.build()//
				.put(Entity.entity(requestBody, MediaType.APPLICATION_JSON));
		return result;
	}

}
