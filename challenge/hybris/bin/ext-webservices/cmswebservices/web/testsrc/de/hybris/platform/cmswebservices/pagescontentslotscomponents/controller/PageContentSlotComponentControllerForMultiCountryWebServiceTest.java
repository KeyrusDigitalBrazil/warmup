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
package de.hybris.platform.cmswebservices.pagescontentslotscomponents.controller;

import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.STAGED;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.MULTI_COUNTRY_ID_EUROPE_CARS;
import static de.hybris.platform.cmsfacades.util.models.ContentPageModelMother.UID_HOMEPAGE_EU;
import static de.hybris.platform.cmsfacades.util.models.SiteModelMother.MULTI_COUNTRY_EUROPE_CARS_SITE;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_CATALOG_ID;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_SITE_ID;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_VERSION_ID;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotForPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotModelMother;
import de.hybris.platform.cmsfacades.util.models.LinkComponentModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.PageContentSlotComponentData;
import de.hybris.platform.cmswebservices.data.PageContentSlotComponentListData;
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
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class PageContentSlotComponentControllerForMultiCountryWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagescontentslotscomponents";
	private static final String UPDATE_ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagescontentslotscomponents/pages/{pageId}/contentslots/{slotId}/components/{componentId}";
	private static final String REMOVE_ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagescontentslotscomponents/contentslots/{slotId}/components/{componentId}";
	private static final String SLOT_ID = "slotId";
	private static final String PAGE_ID = "pageId";
	private static final String COMPONENT_ID = "componentId";

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private ContentSlotForPageModelMother contentSlotForPageModelMother;
	@Resource
	private LinkComponentModelMother linkComponentModelMother;
	@Resource
	private UniqueItemIdentifierService cmsUniqueItemIdentifierService;

	private CMSLinkComponentModel linkComponentModel;

	@Before
	public void start() throws ImpExException
	{
		importCsv("/cmswebservices/test/impex/essentialMultiCountryTestDataAuth.impex", "utf-8");
	}

	protected PageContentSlotComponentData buildLinkContentSlotComponentItemDto()
	{
		// create link component in Car Global Online catalog
		final CatalogVersionModel globalOnlineCV = catalogVersionModelMother.createCarGlobalOnlineCatalogVersionModel();
		linkComponentModelMother.createExternalLinkComponentModel(globalOnlineCV);

		// create a custom content slot on homepage-eu
		final CatalogVersionModel europeStagedCV = catalogVersionModelMother.createCarEuropeStagedCatalogVersionModel();
		contentSlotForPageModelMother.HeaderHomepageEurope_ParagraphOnly(europeStagedCV);

		// build DTO to add link component from Car Global to Car Europe catalog
		final PageContentSlotComponentData dto = new PageContentSlotComponentData();
		dto.setComponentId(LinkComponentModelMother.UID_EXTERNAL_LINK);
		dto.setPosition(0);
		dto.setSlotId(ContentSlotModelMother.UID_HEADER_EU);
		dto.setPageId(ContentPageModelMother.UID_HOMEPAGE_EU);
		return dto;
	}

	@Test
	public void shouldAddComponentToSlot_FirstPosition()
	{
		// GIVEN
		final PageContentSlotComponentData entity = buildLinkContentSlotComponentItemDto();

		final Map<String, String> params = new HashMap<>();
		params.put(URI_CATALOG_ID, MULTI_COUNTRY_ID_EUROPE_CARS.name());
		params.put(URI_VERSION_ID, STAGED.getVersion());
		params.put(URI_SITE_ID, MULTI_COUNTRY_EUROPE_CARS_SITE);

		// WHEN
		final Response response = getMultiCountryCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, params)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(entity, MediaType.APPLICATION_JSON));

		// THEN
		assertResponse(Status.CREATED, response);
	}

	@Test
	public void shouldMoveComponentToPositionInHeader() throws Exception
	{
		// GIVEN
		// create link component in Car Europe Staged catalog
		final CatalogVersionModel catalogVersion = catalogVersionModelMother.createCarEuropeStagedCatalogVersionModel();
		contentSlotForPageModelMother.HeaderHomepageEurope_ParagraphAndLink(catalogVersion);

		final Response initialResponse = getMultiCountryCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, getMultiCountryCarEuropeStagedUriParams())) //
				.queryParam(PAGE_ID, UID_HOMEPAGE_EU).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, initialResponse);
		final PageContentSlotComponentListData initialEntity = initialResponse.readEntity(PageContentSlotComponentListData.class);
		final int initialNumberOfComponents = initialEntity.getPageContentSlotComponentList().size();
		assertThat(initialNumberOfComponents, greaterThanOrEqualTo(2));

		//get the first item (the one at position zero)
		final PageContentSlotComponentData entity = initialEntity.getPageContentSlotComponentList().get(0);
		entity.setPosition(1);

		final Map<String, String> params = getMultiCountryCarEuropeStagedUriParams();
		params.put(PAGE_ID, ContentPageModelMother.UID_HOMEPAGE);
		params.put(SLOT_ID, ContentSlotModelMother.UID_HEADER_EU);
		params.put(COMPONENT_ID, entity.getComponentId());

		// WHEN
		final Response response = getMultiCountryCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(UPDATE_ENDPOINT, params)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(entity, MediaType.APPLICATION_JSON));

		assertResponse(Status.OK, response);

		// THEN
		final Response finalResponse = getMultiCountryCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, getMultiCountryCarEuropeStagedUriParams()))
				.queryParam(PAGE_ID, UID_HOMEPAGE_EU).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, finalResponse);
		final PageContentSlotComponentListData finalEntity = finalResponse.readEntity(PageContentSlotComponentListData.class);
		assertThat(finalEntity.getPageContentSlotComponentList().size(), greaterThanOrEqualTo(2));

		final int finalNumberOfComponents = finalEntity.getPageContentSlotComponentList().size();
		assertThat(finalNumberOfComponents, is(equalTo(initialNumberOfComponents)));
		assertThat(finalEntity.getPageContentSlotComponentList().get(1).getComponentId(), is(equalTo(entity.getComponentId())));
	}

	@Test
	public void shouldRemoveComponentFromSlot() throws Exception
	{
		// GIVEN
		// Create link component in Global Online catalog and add it to the header slot in Car Europe Staged
		final PageContentSlotComponentData entity = buildLinkContentSlotComponentItemDto();

		final Map<String, String> params = getMultiCountryCarEuropeStagedUriParams();

		Response response = getMultiCountryCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, params)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(entity, MediaType.APPLICATION_JSON));
		assertResponse(Status.CREATED, response);

		params.put(SLOT_ID, ContentSlotModelMother.UID_HEADER_EU);
		params.put(COMPONENT_ID, LinkComponentModelMother.UID_EXTERNAL_LINK);

		// WHEN
		response = getMultiCountryCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(REMOVE_ENDPOINT, params)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.delete();

		// THEN
		assertResponse(Status.NO_CONTENT, response);
	}

	protected Map<String, String> getMultiCountryCarEuropeStagedUriParams()
	{
		final Map<String, String> params = new HashMap<>();
		params.put(URI_CATALOG_ID, MULTI_COUNTRY_ID_EUROPE_CARS.name());
		params.put(URI_VERSION_ID, STAGED.getVersion());
		params.put(URI_SITE_ID, MULTI_COUNTRY_EUROPE_CARS_SITE);
		return params;
	}

}