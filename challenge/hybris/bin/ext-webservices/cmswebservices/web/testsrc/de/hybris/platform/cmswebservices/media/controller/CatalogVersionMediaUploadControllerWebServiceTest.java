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

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.MediaModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.util.Strings;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.google.common.collect.Maps;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class CatalogVersionMediaUploadControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String POST_ENDPOINT = "/v1/catalogs/{catalogId}/versions/{versionId}/media";

	private static final String CONTENT_TYPE = "Content-Type";
	private static final String MULTIPART_HEADER = "multipart/form-data";
	private static final String ALT_TEXT = "altText";
	private static final String CODE = "code";
	private static final String DESCRIPTION = "description";
	private static final String FILE = "file";

	private static final String CODE_WITH_JPG_EXTENSION = "some-Media_Code.jpg";
	private static final String MEDIA_CODE = "abc123";
	private static final String MEDIA_DESCRIPTION = "Some lengthy description.";
	private static final String MEDIA_ALT_TEXT = "Alternative text.";
	private static final String MEDIA_PATH = "cmswebservices/test/images/SAP-hybris-logo.png";

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
	}

	@SuppressWarnings("resource")
	@Test
	public void shouldUploadImageForMedia() throws Exception
	{
		final String endpoint = replaceUriVariablesWithDefaults(POST_ENDPOINT, Maps.newHashMap());

		final FileDataBodyPart filePart = new FileDataBodyPart(FILE, new ClassPathResource(MEDIA_PATH).getFile());
		final MultiPart multipart = new FormDataMultiPart() //
				.field(CODE, MEDIA_CODE) //
				.field(ALT_TEXT, MEDIA_ALT_TEXT) //
				.field(DESCRIPTION, MEDIA_DESCRIPTION) //
				.bodyPart(filePart);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endpoint).build() //
				.header(CONTENT_TYPE, MULTIPART_HEADER) //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA));

		assertResponse(Status.CREATED, response);
		assertThat(response.getHeaderString(CmswebservicesConstants.HEADER_LOCATION), containsString(endpoint + "/" + MEDIA_CODE));
	}

	@SuppressWarnings("resource")
	@Test
	public void shouldFailUploadImageNoCode() throws Exception
	{
		final String endpoint = replaceUriVariablesWithDefaults(POST_ENDPOINT, Maps.newHashMap());

		final FileDataBodyPart filePart = new FileDataBodyPart(FILE, new ClassPathResource(MEDIA_PATH).getFile());
		final MultiPart multipart = new FormDataMultiPart() //
				.field(CODE, Strings.EMPTY) //
				.field(ALT_TEXT, MEDIA_ALT_TEXT) //
				.field(DESCRIPTION, MEDIA_DESCRIPTION) //
				.bodyPart(filePart);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endpoint).build() //
				.header(CONTENT_TYPE, MULTIPART_HEADER) //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(1));
		assertThat(errors.getErrors().get(0).getSubject(), is(CODE));
		assertThat(errors.getErrors().get(0).getMessage(), containsString("required"));
	}

	@SuppressWarnings("resource")
	@Test
	public void shouldFailUploadImageCodeNotUnique() throws Exception
	{
		final String endpoint = replaceUriVariablesWithDefaults(POST_ENDPOINT, Maps.newHashMap());

		final FileDataBodyPart filePart = new FileDataBodyPart(FILE, new ClassPathResource(MEDIA_PATH).getFile());
		final MultiPart multipart = new FormDataMultiPart() //
				.field(CODE, CODE_WITH_JPG_EXTENSION) //
				.field(ALT_TEXT, MEDIA_ALT_TEXT) //
				.field(DESCRIPTION, MEDIA_DESCRIPTION) //
				.bodyPart(filePart);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endpoint).build() //
				.header(CONTENT_TYPE, MULTIPART_HEADER) //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(1));
		assertThat(errors.getErrors().get(0).getSubject(), is(CODE));
		assertThat(errors.getErrors().get(0).getMessage(), containsString("already in use"));
	}
}
