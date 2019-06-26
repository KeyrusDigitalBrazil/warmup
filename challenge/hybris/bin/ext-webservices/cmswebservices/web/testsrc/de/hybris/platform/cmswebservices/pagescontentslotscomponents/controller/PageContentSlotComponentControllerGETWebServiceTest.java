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

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_PAGE_ID;
import static de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite.ELECTRONICS;
import static de.hybris.platform.cmsfacades.util.models.ContentPageModelMother.UID_HOMEPAGE;
import static de.hybris.platform.cmsfacades.util.models.ContentSlotModelMother.UID_HEADER;
import static de.hybris.platform.cmsfacades.util.models.LinkComponentModelMother.UID_EXTERNAL_LINK;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.PageContentSlotComponentData;
import de.hybris.platform.cmswebservices.data.PageContentSlotComponentListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotForPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotNameModelMother;
import de.hybris.platform.cmsfacades.util.models.PageTemplateModelMother;
import de.hybris.platform.cmsfacades.util.models.ParagraphComponentModelMother;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hamcrest.Matchers;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class PageContentSlotComponentControllerGETWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String PAGE_CONTENT_SLOT_COMPONENTS_ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagescontentslotscomponents";
	private static final String INVALID_PAGE_ID = "INVALID_PAGE_ID";

	@Resource
	private CMSSiteModelMother cmsSiteModelMother;
	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private ContentSlotForPageModelMother contentSlotForPageModelMother;
	@Resource
	private ContentSlotNameModelMother contentSlotNameModelMother;
	@Resource
	private PageTemplateModelMother pageTemplateModelMother;

	private CatalogVersionModel catalogVersion;

	@Test
	public void shouldGetComponentsByPage() throws Exception
	{
		setupTestData();

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_CONTENT_SLOT_COMPONENTS_ENDPOINT,
						new HashMap<>()))
				.queryParam(URI_PAGE_ID, UID_HOMEPAGE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageContentSlotComponentListData entity = response.readEntity(PageContentSlotComponentListData.class);


		final PageContentSlotComponentData paragraphHeaderData = entity.getPageContentSlotComponentList().stream() //
				.filter(item -> item.getSlotId().equals(UID_HEADER)) //
				.filter(item -> item.getComponentId().equals(ParagraphComponentModelMother.UID_HEADER)) //
				.findFirst().get();
		assertThat(paragraphHeaderData.getPageId(), Matchers.is(UID_HOMEPAGE));
		assertThat(paragraphHeaderData.getSlotId(), Matchers.is(UID_HEADER));
		assertThat(paragraphHeaderData.getComponentId(), Matchers.is(ParagraphComponentModelMother.UID_HEADER));
		assertThat(paragraphHeaderData.getPosition(), Matchers.is(0));

		final PageContentSlotComponentData linkHeaderData = entity.getPageContentSlotComponentList().stream() //
				.filter(item -> item.getSlotId().equals(UID_HEADER)) //
				.filter(item -> item.getComponentId().equals(UID_EXTERNAL_LINK)) //
				.findFirst().get();
		assertThat(linkHeaderData.getPageId(), Matchers.is(UID_HOMEPAGE));
		assertThat(linkHeaderData.getSlotId(), Matchers.is(UID_HEADER));
		assertThat(linkHeaderData.getComponentId(), Matchers.is(UID_EXTERNAL_LINK));
		assertThat(linkHeaderData.getPosition(), Matchers.is(1));
	}

	@Test
	public void shouldReturnEmptyObject_OnException() throws Exception
	{
		createSiteCatalogVersion();

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_CONTENT_SLOT_COMPONENTS_ENDPOINT,
						new HashMap<>()))
				.queryParam(URI_PAGE_ID, INVALID_PAGE_ID).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageContentSlotComponentListData entity = response.readEntity(PageContentSlotComponentListData.class);

		assertThat(entity.getPageContentSlotComponentList(), empty());
	}

	protected void createSiteCatalogVersion()
	{
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		cmsSiteModelMother.createSiteWithTemplate(ELECTRONICS, catalogVersion);
	}

	protected void setupTestData() {
		createSiteCatalogVersion();

		final PageTemplateModel pageTemplate = pageTemplateModelMother.homepageTemplate(catalogVersion);
		contentSlotForPageModelMother.HeaderHomepage_ParagraphAndLink(catalogVersion);

		contentSlotNameModelMother.Header(pageTemplate);
		contentSlotNameModelMother.Link(pageTemplate);
	}

}
