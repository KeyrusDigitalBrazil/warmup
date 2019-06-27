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

import static de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite.ELECTRONICS;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotForPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotNameModelMother;
import de.hybris.platform.cmsfacades.util.models.PageTemplateModelMother;
import de.hybris.platform.cmsfacades.util.models.ParagraphComponentModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.PageContentSlotComponentData;
import de.hybris.platform.cmswebservices.data.PageContentSlotComponentListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class PageContentSlotComponentControllerUpdateWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String INVALID_SLOT_ID = "INVALID_SLOT_ID";
	private static final String GET_ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagescontentslotscomponents";
	private static final String UPDATE_ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagescontentslotscomponents/pages/{pageId}/contentslots/{slotId}/components/{componentId}";
	private static final String SLOT_ID = "slotId";
	private static final String COMPONENT_ID = "componentId";
	private static final String PAGE_ID = "pageId";
	private static final String COMPONENT_ALREADY_EXIST_SLOT = "You cannot add more than one instance of a component to a content slot.";

	@Resource
	private CMSSiteModelMother cmsSiteModelMother;
	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private ContentSlotForPageModelMother contentSlotForPageModelMother;
	@Resource
	private ContentSlotNameModelMother contentSlotNameModelMother;
	@Resource
	private ContentPageModelMother contentPageModelMother;
	@Resource
	private ParagraphComponentModelMother paragraphComponentModelMother;
	@Resource
	private ContentSlotModelMother contentSlotModelMother;
	@Resource
	private PageTemplateModelMother pageTemplateModelMother;

	private CatalogVersionModel catalogVersion;

	@Test
	public void shouldMoveComponentFromHeaderSlotToFootSlot() throws Exception
	{
		createParagraphWithinHeaderSlot_electronicsSite_appleCatalog_emptyFootSlot();

		final PageContentSlotComponentData entity = new PageContentSlotComponentData();
		entity.setSlotId(ContentSlotModelMother.UID_FOOTER);
		entity.setComponentId(ParagraphComponentModelMother.UID_HEADER);
		entity.setPosition(0);
		entity.setPageId(ContentPageModelMother.UID_HOMEPAGE);

		final Map<String, String> params = new HashMap<>();
		params.put(PAGE_ID, ContentPageModelMother.UID_HOMEPAGE);
		params.put(SLOT_ID, ContentSlotModelMother.UID_HEADER);
		params.put(COMPONENT_ID, ParagraphComponentModelMother.UID_HEADER);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(UPDATE_ENDPOINT, params)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(entity, MediaType.APPLICATION_JSON));

		assertResponse(Status.OK, response);
	}

	@Test
	public void shouldMoveComponentToPositionInHeader() throws Exception
	{
		createParagraphAndLinkWithinHeaderSlot_electronicsSite_appleCatalog();

		final Response initialResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(GET_ENDPOINT, new HashMap<>()))
				.queryParam(PAGE_ID, ContentPageModelMother.UID_HOMEPAGE) //
				.queryParam(SLOT_ID, ContentSlotModelMother.UID_HEADER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, initialResponse);
		final PageContentSlotComponentListData initialEntity = initialResponse.readEntity(PageContentSlotComponentListData.class);
		final int initialNumberOfComponents = initialEntity.getPageContentSlotComponentList().size();
		assertThat(initialNumberOfComponents, greaterThanOrEqualTo(2));

		//get the first item (the one at position zero)
		final PageContentSlotComponentData entity = initialEntity.getPageContentSlotComponentList().get(0);
		entity.setPosition(1);

		final Map<String, String> params = new HashMap<>();
		params.put(PAGE_ID, ContentPageModelMother.UID_HOMEPAGE);
		params.put(SLOT_ID, ContentSlotModelMother.UID_HEADER);
		params.put(COMPONENT_ID, entity.getComponentId());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(UPDATE_ENDPOINT, params)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(entity, MediaType.APPLICATION_JSON));

		assertResponse(Status.OK, response);

		final Response finalResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(GET_ENDPOINT, new HashMap<>()))
				.queryParam(PAGE_ID, ContentPageModelMother.UID_HOMEPAGE) //
				.queryParam(SLOT_ID, ContentSlotModelMother.UID_HEADER).build() //
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
	public void shouldNotMoveComponentInvalidSlot() throws Exception
	{
		createParagraphWithinHeaderSlot_electronicsSite_appleCatalog_emptyFootSlot();

		final PageContentSlotComponentData entity = new PageContentSlotComponentData();
		//invalid uid
		entity.setSlotId(INVALID_SLOT_ID);
		entity.setComponentId(ParagraphComponentModelMother.UID_FOOTER);
		entity.setPosition(0);
		entity.setPageId(ContentPageModelMother.UID_HOMEPAGE);

		final Map<String, String> params = new HashMap<>();
		params.put(PAGE_ID, ContentPageModelMother.UID_HOMEPAGE);
		params.put(SLOT_ID, ContentSlotModelMother.UID_HEADER);
		params.put(COMPONENT_ID, ParagraphComponentModelMother.UID_HEADER);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(UPDATE_ENDPOINT, params)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(entity, MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);
	}

	@Test
	public void shouldNotMoveComponentValidationErrors() throws Exception
	{
		createParagraphWithinHeaderSlot_electronicsSite_appleCatalog_emptyFootSlot();

		final PageContentSlotComponentData entity = new PageContentSlotComponentData();

		final Map<String, String> params = new HashMap<>();
		params.put(PAGE_ID, ContentPageModelMother.UID_HOMEPAGE);
		params.put(SLOT_ID, ContentSlotModelMother.UID_HEADER);
		params.put(COMPONENT_ID, ParagraphComponentModelMother.UID_HEADER);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(UPDATE_ENDPOINT, params)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(entity, MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(4));
	}

	@Test
	public void shouldNotMoveComponentIntoASlotThatAlreadyHasAnInstanceValidationErrors() throws Exception
	{
		createParagraphWithinHeaderSlot_electronicsSite_appleCatalog_emptyFootSlot();

		final PageContentSlotComponentData entity = new PageContentSlotComponentData();
		entity.setComponentId(ParagraphComponentModelMother.UID_HEADER);
		entity.setPosition(0);
		entity.setSlotId(ContentSlotModelMother.UID_HEADER);
		entity.setPageId(ContentPageModelMother.UID_HOMEPAGE);

		final Map<String, String> params = new HashMap<>();
		params.put(PAGE_ID, ContentPageModelMother.UID_HOMEPAGE);
		params.put(SLOT_ID, ContentSlotModelMother.UID_FOOTER);
		params.put(COMPONENT_ID, ParagraphComponentModelMother.UID_HEADER);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(UPDATE_ENDPOINT, params)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(entity, MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(1));
		assertThat(errors.getErrors().get(0).getMessage(), is(COMPONENT_ALREADY_EXIST_SLOT));
	}

	protected void createParagraphWithinHeaderSlot_electronicsSite_appleCatalog_emptyFootSlot()
	{
		// Create catalog & catalog version
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		cmsSiteModelMother.createSiteWithTemplate(ELECTRONICS, catalogVersion);

		// Create homepage template
		final PageTemplateModel pageTemplate = pageTemplateModelMother.homepageTemplate(catalogVersion);

		//create home page
		contentPageModelMother.homePage(catalogVersion);

		// Create homepage page and content slot header with paragraph component
		contentSlotForPageModelMother.HeaderHomepage_ParagraphOnly(catalogVersion);
		contentSlotForPageModelMother.FooterHomepage_Empty(catalogVersion);

		// Create header content slot name with paragraph + link restrictions
		contentSlotNameModelMother.Header(pageTemplate);

		// Create footer slot
		contentSlotNameModelMother.Footer(pageTemplate);
	}

	protected void createParagraphAndLinkWithinHeaderSlot_electronicsSite_appleCatalog()
	{
		// Create catalog & catalog version
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		cmsSiteModelMother.createSiteWithTemplate(ELECTRONICS, catalogVersion);

		// Create homepage template
		final PageTemplateModel pageTemplate = pageTemplateModelMother.homepageTemplate(catalogVersion);

		//create home page
		contentPageModelMother.homePage(catalogVersion);

		// Create homepage page and content slot header with paragraph and link components
		contentSlotForPageModelMother.HeaderHomepage_ParagraphAndLink(catalogVersion);

		// Create header content slot name with paragraph + link restrictions
		contentSlotNameModelMother.Header(pageTemplate);

	}
}
