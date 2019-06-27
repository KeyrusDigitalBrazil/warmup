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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.uniqueidentifier.EncodedItemComposedKey;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.MediaModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.MediaData;
import de.hybris.platform.cmswebservices.data.MediaListData;
import de.hybris.platform.cmswebservices.dto.CMSItemUuidListWsDTO;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class MediaControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String GET_ALL_ENDPOINT = "/v1/media";

	private static final String CODE_PHONE_1 = "some.awesome.phone.from.Apple";
	private static final String CODE_PHONE_2 = "another-PHONE-from-Apple";
	private static final String CODE_PHONE_3 = "standard iPhone";
	private static final String CODE_PHONE_4 = "phone... by Apple";
	private static final String CODE_CAMERA_1 = "that-red-camera";
	private static final String CODE_CAMERA_2 = "that.phony/fake camera";
	private static final String INVALID = "invalid-id";

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private MediaModelMother mediaModelMother;
	@Resource
	private ModelService modelService;

	private CatalogVersionModel catalogVersion;

	@Before
	public void setupFixtures()
	{
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		mediaModelMother.createLogoMediaModel(catalogVersion);
	}

	@Test
	public void shouldGetMediaByQuery() throws Exception
	{
		createMediaModels();

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(GET_ALL_ENDPOINT) //
				.queryParam("namedQuery", "namedQueryMediaSearchByCodeCatalogVersion") //
				.queryParam("params", "code:phone,catalogId:ID_APPLE,catalogVersion:staged") //
				.queryParam("pageSize", "25") //
				.queryParam("currentPage", "0") //
				.queryParam("sort", "code:ASC").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final MediaListData entity = response.readEntity(MediaListData.class);
		assertThat(entity.getMedia(), hasSize(4));
	}

	@Test
	public void shouldGetMediaByUUID() throws Exception
	{
		createMediaModels();

		final Response searchResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(GET_ALL_ENDPOINT) //
				.queryParam("namedQuery", "namedQueryMediaSearchByCodeCatalogVersion") //
				.queryParam("params", "code:phone,catalogId:ID_APPLE,catalogVersion:staged") //
				.queryParam("pageSize", "1") //
				.queryParam("currentPage", "0") //
				.queryParam("sort", "code:ASC").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, searchResponse);

		final MediaListData searchEntity = searchResponse.readEntity(MediaListData.class);
		final MediaData firstMedia = searchEntity.getMedia().get(0);
		final String firstmediaUuid = firstMedia.getUuid();
		final String firstMediaCode = firstMedia.getCode();

		final Response getByUuidResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(GET_ALL_ENDPOINT + "/" + firstmediaUuid).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, getByUuidResponse);
		final MediaData getByUuidEntity = getByUuidResponse.readEntity(MediaData.class);

		assertThat(getByUuidEntity.getUuid(), equalTo(firstmediaUuid));
		assertThat(getByUuidEntity.getCode(), equalTo(firstMediaCode));
	}

	@Test
	public void shouldFailGetMediaByUUIDWithInvalidUUID() throws Exception
	{
		final Response getByUuidResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(GET_ALL_ENDPOINT) //
				.path(getUuid(INVALID)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.NOT_FOUND, getByUuidResponse);
		final ErrorListWsDTO errors = getByUuidResponse.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors(), hasSize(1));
		assertThat(errors.getErrors().get(0).getType(), equalTo("UnknownIdentifierError"));
	}

	@Test
	public void shouldFailGetMediaByQuery_ValidationErrors() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(GET_ALL_ENDPOINT) //
				.queryParam("namedQuery", "") //
				.queryParam("params", "code:banner") //
				.queryParam("pageSize", "invalid") //
				.queryParam("currentPage", "0") //
				.queryParam("sort", "code:banner").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors(), hasSize(3));
		assertThat(errors.getErrors().get(0).getSubject(), equalTo("namedQuery"));
		assertThat(errors.getErrors().get(1).getSubject(), equalTo("pageSize"));
		assertThat(errors.getErrors().get(2).getSubject(), equalTo("sort"));
	}

	@Test
	public void shouldGetMediaByQueryWithEmptyResults_CodeNoMatch() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(GET_ALL_ENDPOINT) //
				.queryParam("namedQuery", "namedQueryMediaSearchByCodeCatalogVersion") //
				.queryParam("params", "code:invalid,catalogId:ID_APPLE,catalogVersion:staged") //
				.queryParam("pageSize", "25") //
				.queryParam("currentPage", "0") //
				.queryParam("sort", "code:ASC").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final MediaListData entity = response.readEntity(MediaListData.class);
		assertThat(entity.getMedia(), empty());
	}

	@Test
	public void shouldGetMediaByQueryWithEmptyResults_CatalogVersionNoMatch() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(GET_ALL_ENDPOINT) //
				.queryParam("namedQuery", "namedQueryMediaSearchByCodeCatalogVersion") //
				.queryParam("params", "code:" + LOGO.getCode() + ",catalogId:apple,catalogVersion:invalid") //
				.queryParam("pageSize", "25") //
				.queryParam("currentPage", "0") //
				.queryParam("sort", "code:ASC").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final MediaListData entity = response.readEntity(MediaListData.class);
		assertThat(entity.getMedia(), empty());
	}

	@Test
	public void shouldGetAllMediaByUUIDs() throws Exception
	{
		// create media
		final List<String> mediaUuids = createMediaModels().stream().map(mediaModel -> getUuid(mediaModel.getCode()))
				.collect(Collectors.toList());

		final CMSItemUuidListWsDTO uuidListWsDTO = new CMSItemUuidListWsDTO();
		uuidListWsDTO.setUuids(mediaUuids);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(GET_ALL_ENDPOINT) //
				.path("uuids") //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(uuidListWsDTO, CMSItemUuidListWsDTO.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.OK, response);

		final MediaListData mediaList = response.readEntity(MediaListData.class);
		assertThat(mediaList.getMedia(), hasSize(mediaUuids.size()));
	}

	@Test
	public void shouldFailGetMediaByUUIDsWithInvalidUUID() throws Exception
	{
		// create media
		final List<String> mediaUuids = createMediaModels().stream().map(mediaModel -> getUuid(mediaModel.getCode()))
				.collect(Collectors.toList());
		mediaUuids.add(getUuid(INVALID));

		final CMSItemUuidListWsDTO uuidListWsDTO = new CMSItemUuidListWsDTO();
		uuidListWsDTO.setUuids(mediaUuids);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(GET_ALL_ENDPOINT) //
				.path("uuids") //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(uuidListWsDTO, CMSItemUuidListWsDTO.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.NOT_FOUND, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors(), hasSize(1));
		assertThat(errors.getErrors().get(0).getType(), equalTo("UnknownIdentifierError"));
	}

	protected List<MediaModel> createMediaModels()
	{
		return Stream.of(CODE_PHONE_1, CODE_PHONE_2, CODE_PHONE_3, CODE_PHONE_4, CODE_CAMERA_1, CODE_CAMERA_2)
				.map(this::createMediaModel).collect(Collectors.toList());
	}

	protected MediaModel createMediaModel(final String code)
	{
		final MediaModel media = modelService.create(MediaModel.class);
		media.setCode(code);
		media.setCatalogVersion(catalogVersion);
		modelService.save(media);
		return media;
	}

	protected String getUuid(final String mediaCode)
	{
		final EncodedItemComposedKey itemComposedKey = new EncodedItemComposedKey();
		itemComposedKey.setCatalogId(catalogVersion.getCatalog().getId());
		itemComposedKey.setCatalogVersion(catalogVersion.getVersion());
		itemComposedKey.setItemId(mediaCode);

		return itemComposedKey.toEncoded();
	}

}
