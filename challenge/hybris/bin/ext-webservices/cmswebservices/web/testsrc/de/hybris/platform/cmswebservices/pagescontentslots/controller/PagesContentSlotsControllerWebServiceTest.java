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
package de.hybris.platform.cmswebservices.pagescontentslots.controller;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.data.SlotStatus;
import de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother;
import de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotForPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotForTemplateModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.PageContentSlotListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class PagesContentSlotsControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String PAGE_CONTENT_SLOT_ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagescontentslots";

	@Resource
	private ContentSlotForPageModelMother contentSlotForPageModelMother;
	@Resource
	private ContentSlotForTemplateModelMother contentSlotForTemplateModelMother;
	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private CMSSiteModelMother cmsSiteModelMother;

	private static final String INVALID_ID = "invalid-id";

	private CatalogVersionModel catalogVersion;

	@Before
	public void setup()
	{
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		cmsSiteModelMother.createSiteWithTemplate(TemplateSite.ELECTRONICS, catalogVersion);
	}

	@Test
	public void shouldGetContentSlotsInPage() throws Exception
	{
		contentSlotForPageModelMother.FooterHomepage_Empty(catalogVersion);
		contentSlotForPageModelMother.HeaderHomepage_FlashComponentOnly(catalogVersion);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_CONTENT_SLOT_ENDPOINT, new HashMap<>()))
				.queryParam(CmswebservicesConstants.URI_PAGE_ID, ContentPageModelMother.UID_HOMEPAGE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageContentSlotListData entity = response.readEntity(PageContentSlotListData.class);
		assertThat(entity.getPageContentSlotList().size(), is(2));
	}

	@Test
	public void shouldGetSingleSlotForPageWhileOverriding() throws Exception
	{
		contentSlotForPageModelMother.FooterHomepage_Empty(catalogVersion);
		contentSlotForTemplateModelMother.FooterHomepage(catalogVersion);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_CONTENT_SLOT_ENDPOINT, new HashMap<>()))
				.queryParam(CmswebservicesConstants.URI_PAGE_ID, ContentPageModelMother.UID_HOMEPAGE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageContentSlotListData entity = response.readEntity(PageContentSlotListData.class);
		assertThat(entity.getPageContentSlotList().size(), is(1));
		assertThat(entity.getPageContentSlotList().iterator().next().getSlotStatus(), is(SlotStatus.OVERRIDE.name()));
	}

	@Test
	public void shouldGetEmptyForInvalidPage() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_CONTENT_SLOT_ENDPOINT, new HashMap<>()))
				.queryParam(CmswebservicesConstants.URI_PAGE_ID, INVALID_ID).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageContentSlotListData entity = response.readEntity(PageContentSlotListData.class);
		assertThat(entity.getPageContentSlotList(), empty());
	}
}
