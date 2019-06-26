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
package de.hybris.platform.cmswebservices.pages.controller;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cmsfacades.util.models.BaseStoreModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmsfacades.util.models.LanguageModelMother;
import de.hybris.platform.cmsfacades.util.models.ProductPageModelMother;
import de.hybris.platform.cmsfacades.util.models.SiteModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.UidListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class PageControllerGetVariationWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String BASE_PAGE_ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pages";
	private static final String FALLBACKS = "fallbacks";
	private static final String VARIATIONS = "variations";
	private static final String DEFAULT_PAGE = "defaultPage";
	private static final String TYPECODE = "typeCode";

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private ContentPageModelMother contentPageModelMother;
	@Resource
	private ProductPageModelMother productPageModelMother;
	@Resource
	private SiteModelMother siteModelMother;
	@Resource
	private BaseStoreModelMother baseStoreModelMother;
	@Resource
	private LanguageModelMother languageModelMother;
	@Resource
	private ModelService modelService;


	private CatalogVersionModel catalogVersion;

	@Before
	public void setUp()
	{
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		siteModelMother.createNorthAmericaElectronicsWithAppleStagedCatalog();
	}

	@Test
	public void shouldGetVariationContentPagesForSearchPage()
	{
		contentPageModelMother.primarySearchPageFromHomePageTemplate(catalogVersion);
		contentPageModelMother.searchPageFromHomePageTemplate(catalogVersion);
		contentPageModelMother.homePage(catalogVersion);
		contentPageModelMother.primaryHomePage(catalogVersion);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, Maps.newHashMap()))
				.path(ContentPageModelMother.UID_PRIMARY_SEARCHPAGE).path(VARIATIONS).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final UidListData entity = response.readEntity(UidListData.class);
		assertThat(entity.getUids(), hasSize(1));
		assertThat(entity.getUids().get(0), is(ContentPageModelMother.UID_SEARCHPAGE));
	}


	@Test
	public void shouldGetNoVariationContentPagesForDeletedSearchPage()
	{
		contentPageModelMother.primarySearchPageFromHomePageTemplate(catalogVersion);
		contentPageModelMother.searchPageFromHomePageTemplate(catalogVersion, CmsPageStatus.DELETED);

		contentPageModelMother.homePage(catalogVersion);
		contentPageModelMother.primaryHomePage(catalogVersion);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, Maps.newHashMap()))
				.path(ContentPageModelMother.UID_PRIMARY_SEARCHPAGE).path(VARIATIONS).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final UidListData entity = response.readEntity(UidListData.class);
		assertThat(entity.getUids(), empty());
	}

	@Test
	public void shouldGetFallbackContentPagesForSearchPage()
	{
		contentPageModelMother.searchPageFromHomePageTemplate(catalogVersion);
		contentPageModelMother.primarySearchPageFromHomePageTemplate(catalogVersion);
		contentPageModelMother.primaryHomePage(catalogVersion);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, Maps.newHashMap()))
				.path(ContentPageModelMother.UID_SEARCHPAGE).path(FALLBACKS).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final UidListData entity = response.readEntity(UidListData.class);

		assertThat(entity.getUids(), hasSize(1));
		assertThat(entity.getUids().get(0), is(ContentPageModelMother.UID_PRIMARY_SEARCHPAGE));
	}

}
