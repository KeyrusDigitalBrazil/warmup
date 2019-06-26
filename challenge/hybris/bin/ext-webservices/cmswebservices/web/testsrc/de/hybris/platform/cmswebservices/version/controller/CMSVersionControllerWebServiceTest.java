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
package de.hybris.platform.cmswebservices.version.controller;

import static de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite.ELECTRONICS;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_CURRENT_PAGE;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_PAGE_SIZE;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.dto.CMSVersionListWsDTO;
import de.hybris.platform.cmswebservices.dto.CMSVersionWsDTO;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class CMSVersionControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String ENDPOINT = "/v1/sites/{siteId}/cmsitems/{itemUUID}/versions";
	private static final String ENDPOINT_ROLLBACK = "/v1/sites/{siteId}/cmsitems/{itemUUID}/versions/{versionId}/rollbacks";

	private static final String URI_ITEM_UUID = "itemUUID";
	private static final String URI_VERSION_UID = "versionId";
	private static final String URI_MASK = "mask";

	private static final String UNKNOWN_VERSION_UID = "someInvalidUid";

	private static final String LABEL_1 = "someLabel1";
	private static final String LABEL_2 = "someLabel2";
	private static final String DESCRIPTION_1 = "someDescription1";
	private static final String DESCRIPTION_2 = "someDescription2";

	private static final int DEFAULT_PAGE_SIZE = 10;
	private static final int DEFAULT_CURRENT_PAGE = 0;

	@Resource
	private CMSSiteModelMother cmsSiteModelMother;

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;

	@Resource
	private ContentPageModelMother contentPageModelMother;

	private CatalogVersionModel catalogVersionModel;

	enum CMSVersionFields
	{
		UID("uid"), ITEM_UUID("itemUUID"), LABEL("label"), DESCRIPTION("description");

		private final String property;

		CMSVersionFields(final String property)
		{
			this.property = property;
		}
	}

	private String itemUUID;
	private String versionEndPoint;

	@Before
	public void setUp() throws ImpExException
	{
		createElectronicsSiteAndEmptyAppleCatalog();
		importCsv("/test/cmsTypePermissionTestData.impex", "UTF-8");
		itemUUID = getUuidForAppleStage(ContentPageModelMother.UID_HOMEPAGE);
		versionEndPoint = replaceUriVariablesWithDefaults(ENDPOINT, buildUrlVariables(itemUUID));
	}

	@Test
	public void shouldCreateCMSVersion() throws Exception
	{
		final CMSVersionWsDTO dto = new CMSVersionWsDTO();
		dto.setLabel(LABEL_1);
		dto.setDescription(DESCRIPTION_1);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(versionEndPoint) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(dto, CMSVersionWsDTO.class), MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.CREATED, response);

		final CMSVersionWsDTO entity = response.readEntity(CMSVersionWsDTO.class);
		assertThat(entity, allOf(hasProperty(CMSVersionFields.ITEM_UUID.property, is(itemUUID)), //
				hasProperty(CMSVersionFields.LABEL.property, is(LABEL_1)), //
				hasProperty(CMSVersionFields.DESCRIPTION.property, is(DESCRIPTION_1))));

		assertThat(response.getHeaderString(CmswebservicesConstants.HEADER_LOCATION),
				endsWith(versionEndPoint + "/" + entity.getUid()));
	}

	@Test
	public void shouldFailCreateCMSVersionWithNoLabel() throws Exception
	{
		final CMSVersionWsDTO dto = new CMSVersionWsDTO();
		dto.setDescription(DESCRIPTION_1);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(versionEndPoint) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(dto, CMSVersionWsDTO.class), MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.BAD_REQUEST, response);
	}

	@Test
	public void shouldUpdateCMSVersion() throws Exception
	{
		final CMSVersionWsDTO createdEntity = createVersion(itemUUID, LABEL_1, DESCRIPTION_1);

		final CMSVersionWsDTO dto = new CMSVersionWsDTO();
		dto.setLabel(LABEL_1);
		//Change the description
		dto.setDescription(DESCRIPTION_2);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(versionEndPoint) //
				.path(createdEntity.getUid()) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(marshallDto(dto, CMSVersionWsDTO.class), MediaType.APPLICATION_JSON));

		final CMSVersionWsDTO entity = response.readEntity(CMSVersionWsDTO.class);
		assertThat(entity, allOf(hasProperty(CMSVersionFields.ITEM_UUID.property, is(itemUUID)), //
				hasProperty(CMSVersionFields.UID.property, is(createdEntity.getUid())), //
				hasProperty(CMSVersionFields.LABEL.property, is(LABEL_1)), //
				hasProperty(CMSVersionFields.DESCRIPTION.property, is(DESCRIPTION_2))));
	}

	@Test
	public void shouldFailUpdateCMSVersionWithNoLabel() throws Exception
	{
		final CMSVersionWsDTO createdEntity = createVersion(itemUUID, LABEL_1, DESCRIPTION_1);

		final CMSVersionWsDTO dto = new CMSVersionWsDTO();
		//Change the label
		dto.setLabel(null);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(versionEndPoint) //
				.path(createdEntity.getUid()) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(marshallDto(dto, CMSVersionWsDTO.class), MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.BAD_REQUEST, response);
	}

	@Test
	public void shouldGetAllCMSVersions() throws Exception
	{
		createVersion(itemUUID, LABEL_1, DESCRIPTION_1);
		createVersion(itemUUID, LABEL_2, DESCRIPTION_2);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(versionEndPoint) //
				.queryParam(URI_PAGE_SIZE, DEFAULT_PAGE_SIZE) //
				.queryParam(URI_CURRENT_PAGE, DEFAULT_CURRENT_PAGE) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final CMSVersionListWsDTO entity = response.readEntity(CMSVersionListWsDTO.class);
		assertThat(entity.getPagination().getCount(), is(2));
		assertThat(entity.getPagination().getTotalCount(), is(2L));
		assertThat(entity.getPagination().getPage(), is(0));
	}

	@Test
	public void shouldGetAllCMSVersionsFilteredByMask() throws Exception
	{
		final CMSVersionWsDTO createdEntity = createVersion(itemUUID, LABEL_1, DESCRIPTION_1);
		createVersion(itemUUID, LABEL_2, DESCRIPTION_2);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(versionEndPoint) //
				.queryParam(URI_MASK, "label1") //
				.queryParam(URI_PAGE_SIZE, DEFAULT_PAGE_SIZE) //
				.queryParam(URI_CURRENT_PAGE, DEFAULT_CURRENT_PAGE) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final CMSVersionListWsDTO entity = response.readEntity(CMSVersionListWsDTO.class);
		assertThat(entity.getPagination().getCount(), is(1));
		assertThat(entity.getPagination().getTotalCount(), is(1L));
		assertThat(entity.getPagination().getPage(), is(0));

		assertThat(entity.getResults().get(0), allOf(hasProperty(CMSVersionFields.ITEM_UUID.property, is(itemUUID)), //
				hasProperty(CMSVersionFields.UID.property, is(createdEntity.getUid())), //
				hasProperty(CMSVersionFields.LABEL.property, is(LABEL_1)), //
				hasProperty(CMSVersionFields.DESCRIPTION.property, is(DESCRIPTION_1))));
	}

	@Test
	public void shouldGetCMSVersion() throws Exception
	{
		final CMSVersionWsDTO createdEntity = createVersion(itemUUID, LABEL_1, DESCRIPTION_1);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(versionEndPoint) //
				.path(createdEntity.getUid()) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final CMSVersionWsDTO entity = response.readEntity(CMSVersionWsDTO.class);
		assertThat(entity, allOf(hasProperty(CMSVersionFields.ITEM_UUID.property, is(itemUUID)), //
				hasProperty(CMSVersionFields.UID.property, is(createdEntity.getUid())), //
				hasProperty(CMSVersionFields.LABEL.property, is(LABEL_1)), //
				hasProperty(CMSVersionFields.DESCRIPTION.property, is(DESCRIPTION_1))));
	}

	@Test
	public void shouldFailGetCMSVersionWithInvalidUID()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(versionEndPoint) //
				.path(UNKNOWN_VERSION_UID) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.NOT_FOUND, response);
	}

	@Test
	public void shouldRollbackCMSVersion() throws Exception
	{
		final CMSVersionWsDTO createdEntity = createVersion(itemUUID, LABEL_1, DESCRIPTION_1);
		final String rollbackEndPoint = replaceUriVariablesWithDefaults(ENDPOINT_ROLLBACK,
				buildUrlVariables(itemUUID, createdEntity.getUid()));

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(rollbackEndPoint) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(null);

		assertResponse(Response.Status.NO_CONTENT, response);
	}

	@Test
	public void shouldFailRollbackCMSVersionWithInvalidVersionId() throws Exception
	{
		final CMSVersionWsDTO createdEntity = createVersion(itemUUID, LABEL_1, DESCRIPTION_1);
		final String rollbackEndPoint = replaceUriVariablesWithDefaults(ENDPOINT_ROLLBACK,
				buildUrlVariables(itemUUID, UNKNOWN_VERSION_UID));

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(rollbackEndPoint) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(null);

		assertResponse(Response.Status.BAD_REQUEST, response);
	}

	@Test
	public void shouldDeleteCMSVersion() throws Exception
	{
		final CMSVersionWsDTO createdEntity = createVersion(itemUUID, LABEL_1, DESCRIPTION_1);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(versionEndPoint).path(createdEntity.getUid()) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.delete();

		assertResponse(Response.Status.NO_CONTENT, response);
	}

	@Test
	public void shouldFailDeleteCMSVersionWithInvalidVersionId() throws Exception
	{
		final CMSVersionWsDTO createdEntity = createVersion(itemUUID, LABEL_1, DESCRIPTION_1);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(versionEndPoint).path(UNKNOWN_VERSION_UID) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.delete();

		assertResponse(Response.Status.BAD_REQUEST, response);
	}

	@Test
	public void shouldThrowTypePermissionExceptionIfUserDoesNotHaveCMSVersionPermission()
	{
		// WHEN
		final Response response = getCmsTranslatorWsSecuredRequestBuilder() //
				.path(versionEndPoint) //
				.queryParam(URI_PAGE_SIZE, DEFAULT_PAGE_SIZE) //
				.queryParam(URI_CURRENT_PAGE, DEFAULT_CURRENT_PAGE) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		// THEN
		assertResponse(Response.Status.FORBIDDEN, response);
	}

	protected CMSVersionWsDTO createVersion(final String itemUUID, final String label, final String description) throws Exception
	{
		final CMSVersionWsDTO dto = new CMSVersionWsDTO();
		dto.setLabel(label);
		dto.setDescription(description);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(versionEndPoint) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(dto, CMSVersionWsDTO.class), MediaType.APPLICATION_JSON));

		assertResponse(Response.Status.CREATED, response);

		return response.readEntity(CMSVersionWsDTO.class);
	}

	protected void createElectronicsSiteAndEmptyAppleCatalog()
	{
		cmsSiteModelMother.createSiteWithTemplate(ELECTRONICS);
		catalogVersionModel = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		contentPageModelMother.homePage(catalogVersionModel);
	}

	protected Map<String, String> buildUrlVariables(final String itemUUID)
	{
		return buildUrlVariables(itemUUID, null);
	}

	protected Map<String, String> buildUrlVariables(final String itemUUID, final String versionId)
	{
		final Map<String, String> variables = new HashMap<>();
		variables.put(URI_ITEM_UUID, itemUUID);
		if (versionId != null)
		{
			variables.put(URI_VERSION_UID, versionId);
		}

		return variables;
	}

	protected String getUuidForAppleStage(final String uid)
	{
		return getUuid(ContentCatalogModelMother.CatalogTemplate.ID_APPLE.name(),
				CatalogVersionModelMother.CatalogVersion.STAGED.getVersion(), uid);
	}
}
