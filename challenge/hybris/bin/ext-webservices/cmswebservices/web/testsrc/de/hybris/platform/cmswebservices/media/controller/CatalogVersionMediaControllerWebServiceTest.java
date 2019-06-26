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
package de.hybris.platform.cmswebservices.media.controller;

import static de.hybris.platform.cmsfacades.util.models.MediaModelMother.MediaTemplate.LOGO;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.MediaModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.MediaData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class CatalogVersionMediaControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String GET_ONE_ENDPOINT = "/v1/catalogs/{catalogId}/versions/{versionId}/media/{code}";
	private static final String MEDIA_CODE = "code";
	private static final String INVALID = "invalid";
	private static final String CODE_WITH_JPG_EXTENSION = "some-Media_Code.jpg";
	private static final String CODE_WITH_MULTIPATH = "/path/to/mediaCode";
	private static final String CODE_WITH_MULTIPATH_AND_EXTENSION_AND_WITH_SPACE = "/path/to/mediaCode separated by space";
	private static final String CODE_WITH_MULTIPATH_AND_EXTENSION = "//path/to/mediaCode.jpg";

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private MediaModelMother mediaModelMother;

	private CatalogVersionModel catalogVersion;

	@Before
	public void setupFixtures()
	{
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		mediaModelMother.createLogoMediaModel(catalogVersion);
		mediaModelMother.createLogoMediaModelWithCode(catalogVersion, CODE_WITH_JPG_EXTENSION);
		mediaModelMother.createLogoMediaModelWithCode(catalogVersion, CODE_WITH_MULTIPATH);
		mediaModelMother.createLogoMediaModelWithCode(catalogVersion, CODE_WITH_MULTIPATH_AND_EXTENSION);
		mediaModelMother.createLogoMediaModelWithCode(catalogVersion, CODE_WITH_MULTIPATH_AND_EXTENSION);
		mediaModelMother.createLogoMediaModelWithCode(catalogVersion, CODE_WITH_MULTIPATH_AND_EXTENSION_AND_WITH_SPACE);
	}

	@Test
	public void shouldGetMediaByCode() throws Exception
	{
		final Map<String, String> variables = new HashMap<>();
		variables.put(MEDIA_CODE, LOGO.getCode());
		final String endpoint = replaceUriVariablesWithDefaults(GET_ONE_ENDPOINT, variables);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endpoint).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final MediaData media = response.readEntity(MediaData.class);
		assertEquals(LOGO.getCode(), media.getCode());
		assertEquals(LOGO.getAltText(), media.getAltText());
		assertEquals(LOGO.getDescription(), media.getDescription());
		assertEquals(LOGO.getMimetype(), media.getMime());
	}

	@Test
	public void shoulFailGetMediaByCode_InvalidCatalog() throws Exception
	{
		final Map<String, String> variables = new HashMap<>();
		variables.put(MEDIA_CODE, LOGO.getCode());
		variables.put(CmswebservicesConstants.URI_CATALOG_ID, INVALID);
		final String endpoint = replaceUriVariablesWithDefaults(GET_ONE_ENDPOINT, variables);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endpoint).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.NOT_FOUND, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(1));
	}

	@Test
	public void shoulFailGetMediaByCode_InvalidVersion() throws Exception
	{
		final Map<String, String> variables = new HashMap<>();
		variables.put(MEDIA_CODE, LOGO.getCode());
		variables.put(CmswebservicesConstants.URI_VERSION_ID, INVALID);
		final String endpoint = replaceUriVariablesWithDefaults(GET_ONE_ENDPOINT, variables);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endpoint).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.NOT_FOUND, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(1));
	}

	@Test
	public void shoulFailGetMediaByCode_InvalidMediaCode() throws Exception
	{
		final Map<String, String> variables = new HashMap<>();
		variables.put(MEDIA_CODE, INVALID);
		final String endpoint = replaceUriVariablesWithDefaults(GET_ONE_ENDPOINT, variables);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endpoint).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.NOT_FOUND, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(1));
	}

	@Test
	public void shoulGetMediaByCode_MediaCodeWithJPGExtension() throws Exception
	{
		final Map<String, String> variables = new HashMap<>();
		variables.put(MEDIA_CODE, CODE_WITH_JPG_EXTENSION);
		final String endpoint = replaceUriVariablesWithDefaults(GET_ONE_ENDPOINT, variables);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endpoint).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final MediaData media = response.readEntity(MediaData.class);
		assertEquals(CODE_WITH_JPG_EXTENSION, media.getCode());
	}

}
