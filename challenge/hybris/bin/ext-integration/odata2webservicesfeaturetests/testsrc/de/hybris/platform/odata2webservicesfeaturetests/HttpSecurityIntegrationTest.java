/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2webservicesfeaturetests;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.odata2webservices.constants.Odata2webservicesConstants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

@NeedsEmbeddedServer(webExtensions = {Odata2webservicesConstants.EXTENSIONNAME})
@IntegrationTest
public class HttpSecurityIntegrationTest extends AbstractODataIntegrationTest
{
	private static final String SERVICE_NAME = "TestProduct";
	private static final String CONTENT_TYPE = "Content-Type";

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		// create product
		importCsv("/test/product-odata2webservicesfeaturetests.impex", UTF_8);
	}

	@Test
	public void testSuccessfulAuthorizedRequest_getSchema()
	{
		final Response response = basicAuthRequest()
				.path(SERVICE_NAME)
				.path(METADATA_URI)
				.queryParam(PRODUCT_QUERY_STRING, null)
				.credentials(TEST_ADMIN, PASSWORD)
				.build()
				.accept(APPLICATION_XML_TYPE)
				.get();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
	}

	@Test
	public void testUnauthorizedRequest_getSchema()
	{
		final Response response = request()
				.path(SERVICE_NAME)
				.path(METADATA_URI)
				.build()
				.accept(APPLICATION_XML_TYPE)
				.get();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
	}

	@Test
	public void testWrongCredentialRequest_getSchema()
	{
		final Response response = basicAuthRequest()
				.path(SERVICE_NAME)
				.path(METADATA_URI)
				.credentials(TEST_ADMIN, "wrong-password")
				.build()
				.accept(APPLICATION_XML_TYPE)
				.get();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
	}

	@Test
	public void testMissingGroupRequest_getSchema()
	{
		final Response response;
		response = basicAuthRequest()
				.path(SERVICE_NAME)
				.path(METADATA_URI)
				.credentials(TEST_USER, PASSWORD)
				.build()
				.accept(APPLICATION_XML_TYPE)
				.get();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_FORBIDDEN);
	}

	@Test
	public void testUnauthorizedRequest_handleCreateOrUpdateEntity()
	{
		final Response response = request()
				.path(SERVICE_NAME)
				.path(PRODUCTS_QUERY)
				.build()
				.post(null);

		assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
	}

	@Test
	public void testWrongCredentialRequest_handleCreateOrUpdateEntity()
	{
		final Response response = basicAuthRequest()
				.path(SERVICE_NAME)
				.path(PRODUCTS_QUERY)
				.credentials(TEST_ADMIN, "wrong-password")
				.build()
				.post(null);

		assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
	}

	@Test
	public void testMissingGroupRequest_handleCreateOrUpdateEntity()
	{
		final Response response = basicAuthRequest()
				.path(SERVICE_NAME)
				.path(PRODUCTS_QUERY)
				.credentials(TEST_USER, PASSWORD)
				.build()
				.post(null);

		assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_FORBIDDEN);
	}

	@Test
	public void testUnauthorizedRequest_GET_Entity()
	{
		final Response response = request()
				.path(SERVICE_NAME)
				.path(GET_PRODUCT_QUERY)
				.build()
				.get();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
	}

	@Test
	public void testWrongCredentialRequest_GET_Entity()
	{
		final Response response = basicAuthRequest()
				.path(SERVICE_NAME)
				.path(GET_PRODUCT_QUERY)
				.credentials(TEST_ADMIN, "wrong-password")
				.build()
				.get();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
	}

	@Test
	public void testMissingGroupRequest_GET_Entity()
	{
		final Response response = basicAuthRequest()
				.path(SERVICE_NAME)
				.path(GET_PRODUCT_QUERY)
				.credentials(TEST_USER, PASSWORD)
				.build()
				.get();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_FORBIDDEN);
	}

	@Test
	public void testUnauthorizedRequest_handleBatch()
	{
		final Response response = request()
				.path(SERVICE_NAME)
				.path(BATCH_URI)
				.build()
				.post(null);

		assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
	}

	@Test
	public void testWrongCredentialRequest_handleBatch()
	{
		final Response response = basicAuthRequest()
				.path(SERVICE_NAME)
				.path(BATCH_URI)
				.credentials(TEST_ADMIN, "wrong-password")
				.build()
				.post(null);

		assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
	}

	@Test
	public void testMissingGroupRequest_handleBatch()
	{
		final Response response = basicAuthRequest()
				.path(SERVICE_NAME)
				.path(BATCH_URI)
				.credentials(TEST_USER, PASSWORD)
				.build()
				.post(null);

		assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_FORBIDDEN);
	}

	@Test
	public void testCreateUserMakesAGetRequestForMetadata()
	{
		final Response response = basicAuthRequest()
				.path(SERVICE_NAME)
				.path(METADATA_URI)
				.queryParam(PRODUCT_QUERY_STRING, null)
				.credentials(TEST_CREATE_USER, PASSWORD)
				.build()
				.accept(APPLICATION_XML_TYPE)
				.get();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
	}

	@Test
	public void testCreateUserMakesAGetRequestForNonMetadata()
	{
		final Response response = basicAuthRequest()
				.path(SERVICE_NAME)
				.queryParam(PRODUCT_QUERY_STRING, null)
				.credentials(TEST_CREATE_USER, PASSWORD)
				.build()
				.accept(APPLICATION_XML_TYPE)
				.get();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_FORBIDDEN);
	}

	@Test
	public void testCreateUserMakesAPostRequest()
	{
		final Response response = basicAuthRequest()
				.path(SERVICE_NAME)
				.path(PRODUCTS_QUERY)
				.credentials(TEST_CREATE_USER, PASSWORD)
				.build()
				.accept(APPLICATION_JSON_TYPE)
				.header(CONTENT_TYPE, APPLICATION_JSON)
				.post(Entity.json(product()));

		assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_CREATED);
	}

	@Test
	public void testViewUserMakesAGetRequestForMetadata()
	{
		final Response response = basicAuthRequest()
				.path(SERVICE_NAME)
				.path(METADATA_URI)
				.queryParam(PRODUCT_QUERY_STRING, null)
				.credentials(TEST_VIEW_USER, PASSWORD)
				.build()
				.accept(APPLICATION_XML_TYPE)
				.get();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
	}

	@Test
	public void testViewUserMakesAGetRequestForProducts()
	{
		final Response response = basicAuthRequest()
				.path(SERVICE_NAME)
				.queryParam(PRODUCT_QUERY_STRING, null)
				.credentials(TEST_VIEW_USER, PASSWORD)
				.build()
				.accept(APPLICATION_XML_TYPE)
				.get();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
	}

	@Test
	public void testViewUserMakesAPostRequest()
	{
		final Response response = basicAuthRequest()
				.path(SERVICE_NAME)
				.path(PRODUCTS_QUERY)
				.credentials(TEST_VIEW_USER, PASSWORD)
				.build()
				.accept(APPLICATION_JSON_TYPE)
				.header(CONTENT_TYPE, APPLICATION_JSON)
				.post(Entity.json(product()));

		assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_FORBIDDEN);
	}

	private String product()
	{
		return "{\n" +
				"\t\"prodcode\": \"test_article\",\n" +
				"\t\"prodname\": \"my product\",\n" +
				"\t\"cv\": {\n" +
				"\t\t\"acatalog\": {\n" +
				"\t\t\t\"catalogId\": \"Default\"\n" +
				"\t\t},\n" +
				"\t\t\"aversion\": \"Staged\"\n" +
				"\t}\n" +
				"}";
	}
}
