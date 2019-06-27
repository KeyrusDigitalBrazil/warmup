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
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cmsfacades.pagescontentslotscomponents.validator.ComponentExistsInSlotValidator;
import de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotForPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotNameModelMother;
import de.hybris.platform.cmsfacades.util.models.LinkComponentModelMother;
import de.hybris.platform.cmsfacades.util.models.PageTemplateModelMother;
import de.hybris.platform.cmsfacades.util.models.ParagraphComponentModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.PageContentSlotComponentData;
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
public class PageContentSlotComponentControllerAddAndRemoveWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String INVALID = "INVALID";
	private static final String CONTENTSLOT_ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagescontentslotscomponents";
	private static final String REMOVE_ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagescontentslotscomponents/contentslots/{slotId}/components/{componentId}";
	private static final String SLOT_ID = "slotId";
	private static final String COMPONENT_ID = "componentId";
	private static final String POSITION = "position";

	@Resource
	private CMSSiteModelMother cmsSiteModelMother;
	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private ContentSlotModelMother contentSlotModelMother;
	@Resource
	private ContentSlotForPageModelMother contentSlotForPageModelMother;
	@Resource
	private PageTemplateModelMother pageTemplateModelMother;
	@Resource
	private ContentSlotNameModelMother contentSlotNameModelMother;
	@Resource
	private ParagraphComponentModelMother paragraphComponentModelMother;
	@Resource
	private LinkComponentModelMother linkComponentModelMother;

	private CatalogVersionModel catalogVersion;

	@Test
	public void shouldAddComponentToSlot_FirstPosition() throws Exception
	{
		createAppleCatalogWithHeaderParagraphOnly();
		final PageContentSlotComponentData entity = buildLinkContentSlotComponentItemDto(0, ContentSlotModelMother.UID_HEADER);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(CONTENTSLOT_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(entity, MediaType.APPLICATION_JSON));

		assertResponse(Status.CREATED, response);
	}

	@Test
	public void shouldAddComponentToSlot_LastPosition() throws Exception
	{
		createAppleCatalogWithHeaderParagraphOnly();
		final PageContentSlotComponentData entity = buildLinkContentSlotComponentItemDto(5, ContentSlotModelMother.UID_HEADER);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(CONTENTSLOT_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(entity, MediaType.APPLICATION_JSON));

		assertResponse(Status.CREATED, response);
	}

	@Test
	public void shouldAddComponentToSlot_FailedTypeRestriction() throws Exception
	{
		createAppleCatalogWithLinkWithRestrictions();
		final PageContentSlotComponentData entity = buildParagraphContentSlotComponentItemDto(0, ContentSlotModelMother.UID_HEADER);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(CONTENTSLOT_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(entity, MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(1));
	}

	@Test
	public void shouldAddComponentToSlot_AlreadyPresent() throws Exception
	{
		createAppleCatalogWithHeaderParagraphAndLink();
		final PageContentSlotComponentData entity = buildParagraphContentSlotComponentItemDto(0, ContentSlotModelMother.UID_HEADER);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(CONTENTSLOT_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(entity, MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(1));
	}

	@Test
	public void shouldAddComponentToSlot_SlotNotFound() throws Exception
	{
		createAppleCatalogWithHeaderParagraphOnly();
		final PageContentSlotComponentData entity = buildLinkContentSlotComponentItemDto(0, ContentSlotModelMother.UID_FOOTER);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(CONTENTSLOT_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(entity, MediaType.APPLICATION_JSON));

		assertResponse(Status.NOT_FOUND, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(1));
	}

	@Test
	public void shouldAddComponentToSlot_PageNotFound() throws Exception
	{
		createAppleCatalogWithHeaderParagraphOnly();
		final PageContentSlotComponentData entity = buildInvalidPageLinkComponentItemDto(0, ContentSlotModelMother.UID_HEADER);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(CONTENTSLOT_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(entity, MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(1));
	}

	@Test
	public void shouldAddComponentToSlot_ComponentNotFound() throws Exception
	{
		createAppleCatalogWithHeaderParagraphNoRestriction();
		final PageContentSlotComponentData entity = buildInvalidContentSlotComponentItemDto(0, ContentSlotModelMother.UID_HEADER);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(CONTENTSLOT_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(entity, MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(1));
		assertThat(errors.getErrors().get(0).getSubject(), is(ComponentExistsInSlotValidator.COMPONENT_ID));
	}

	@Test
	public void shouldAddComponentToSlot_InvalidIndex() throws Exception
	{
		createAppleCatalogWithHeaderParagraphOnly();
		final PageContentSlotComponentData entity = buildLinkContentSlotComponentItemDto(-3, ContentSlotModelMother.UID_HEADER);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(CONTENTSLOT_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(entity, MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(1));
		assertThat(errors.getErrors().get(0).getSubject(), is(POSITION));
	}

	@Test
	public void shouldRemoveComponentFromSlot() throws Exception
	{
		createAppleCatalogWithHeaderParagraphAndLink();

		final Map<String, String> params = new HashMap<>();
		params.put(SLOT_ID, ContentSlotModelMother.UID_HEADER);
		params.put(COMPONENT_ID, ParagraphComponentModelMother.UID_HEADER);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(REMOVE_ENDPOINT, params)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.delete();

		assertResponse(Status.NO_CONTENT, response);
	}

	@Test
	public void shouldNotRemoveComponentFromSlot_ComponentNotFound() throws Exception
	{
		createAppleCatalogWithHeaderParagraphAndLink();

		final Map<String, String> params = new HashMap<>();
		params.put(SLOT_ID, ContentSlotModelMother.UID_HEADER);
		params.put(COMPONENT_ID, INVALID);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(REMOVE_ENDPOINT, params)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.delete();

		assertResponse(Status.NOT_FOUND, response);
	}

	@Test
	public void shouldNotRemoveComponentFromSlot_SlotNotFound() throws Exception
	{
		createAppleCatalogWithHeaderParagraphAndLink();

		final Map<String, String> params = new HashMap<>();
		params.put(SLOT_ID, INVALID);
		params.put(COMPONENT_ID, ParagraphComponentModelMother.UID_HEADER);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(REMOVE_ENDPOINT, params)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.delete();

		assertResponse(Status.NOT_FOUND, response);
	}

	protected void createAppleCatalogWithHeaderParagraphAndLink()
	{
		// Create site & catalog & catalog version
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		cmsSiteModelMother.createSiteWithTemplate(ELECTRONICS, catalogVersion);
		// Create homepage template
		final PageTemplateModel pageTemplate = pageTemplateModelMother.homepageTemplate(catalogVersion);
		// Create homepage page and content slot header with paragraph and link component
		contentSlotForPageModelMother.HeaderHomepage_ParagraphAndLink(catalogVersion);
		// Create header content slot name with paragraph restrictions
		contentSlotNameModelMother.Header(pageTemplate);
	}

	protected void createAppleCatalogWithLinkWithRestrictions()
	{
		// Create site & catalog & catalog version
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		cmsSiteModelMother.createSiteWithTemplate(ELECTRONICS, catalogVersion);
		// Create homepage template
		final PageTemplateModel pageTemplate = pageTemplateModelMother.homepageTemplate(catalogVersion);
		// Create homepage page and content slot link
		contentSlotForPageModelMother.HeaderHomepage_LinkOnly(catalogVersion);
		// Create link content slot name with restrictions
		contentSlotNameModelMother.Link(pageTemplate);
		
		// Create paragraph not in slot
		paragraphComponentModelMother.createHeaderParagraphComponentModel(catalogVersion);
	}
	
	protected void createAppleCatalogWithHeaderParagraphOnly()
	{
		// Create site & catalog & catalog version
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		cmsSiteModelMother.createSiteWithTemplate(ELECTRONICS, catalogVersion);
		// Create homepage template
		final PageTemplateModel pageTemplate = pageTemplateModelMother.homepageTemplate(catalogVersion);
		// Create homepage page and content slot header with paragraph component
		contentSlotForPageModelMother.HeaderHomepage_ParagraphOnly(catalogVersion);
		// Create header content slot name with paragraph restrictions
		contentSlotNameModelMother.Header(pageTemplate);

		// Create link not in slot
		linkComponentModelMother.createExternalLinkComponentModel(catalogVersion);
	}

	protected void createAppleCatalogWithHeaderParagraphNoRestriction()
	{
		// Create site & catalog & catalog version
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		cmsSiteModelMother.createSiteWithTemplate(ELECTRONICS, catalogVersion);
		// Create homepage template
		final PageTemplateModel pageTemplate = pageTemplateModelMother.homepageTemplate(catalogVersion);
		// Create homepage page and content slot header with paragraph component
		contentSlotForPageModelMother.HeaderHomepage_ParagraphOnly(catalogVersion);
		// Create header content slot name with paragraph restrictions
		contentSlotNameModelMother.Header_without_restriction(pageTemplate);

		// Create link not in slot
		linkComponentModelMother.createExternalLinkComponentModel(catalogVersion);
	}

	protected PageContentSlotComponentData buildParagraphContentSlotComponentItemDto(final int index, final String slotId)
	{
		final PageContentSlotComponentData dto = new PageContentSlotComponentData();
		dto.setComponentId(ParagraphComponentModelMother.UID_HEADER);
		dto.setPosition(index);
		dto.setSlotId(slotId);
		dto.setPageId(ContentPageModelMother.UID_HOMEPAGE);
		return dto;
	}

	protected PageContentSlotComponentData buildLinkContentSlotComponentItemDto(final int index, final String slotId)
	{
		final PageContentSlotComponentData dto = new PageContentSlotComponentData();
		dto.setComponentId(LinkComponentModelMother.UID_EXTERNAL_LINK);
		dto.setPosition(index);
		dto.setSlotId(slotId);
		dto.setPageId(ContentPageModelMother.UID_HOMEPAGE);
		return dto;
	}

	protected PageContentSlotComponentData buildInvalidContentSlotComponentItemDto(final int index, final String slotId)
	{
		final PageContentSlotComponentData dto = new PageContentSlotComponentData();
		dto.setComponentId(INVALID);
		dto.setPosition(index);
		dto.setSlotId(slotId);
		dto.setPageId(ContentPageModelMother.UID_HOMEPAGE);
		return dto;
	}

	protected PageContentSlotComponentData buildInvalidPageLinkComponentItemDto(final int index, final String slotId)
	{
		final PageContentSlotComponentData dto = new PageContentSlotComponentData();
		dto.setComponentId(LinkComponentModelMother.UID_EXTERNAL_LINK);
		dto.setPosition(index);
		dto.setSlotId(slotId);
		dto.setPageId(INVALID);
		return dto;
	}
}
