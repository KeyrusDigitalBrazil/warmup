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


import static de.hybris.platform.cms2.model.contents.CMSItemModel.UID;
import static de.hybris.platform.cms2.model.pages.AbstractPageModel.TITLE;
import static de.hybris.platform.cms2.model.pages.AbstractPageModel.TYPECODE;
import static de.hybris.platform.cmsfacades.util.models.ContentPageModelMother.TITLE_HOMEPAGE;
import static de.hybris.platform.cmsfacades.util.models.ContentPageModelMother.TITLE_SEARCHPAGE;
import static de.hybris.platform.cmsfacades.util.models.ContentPageModelMother.UID_HOMEPAGE;
import static de.hybris.platform.cmsfacades.util.models.ContentPageModelMother.UID_SEARCHPAGE;
import static de.hybris.platform.cmsfacades.util.models.PageTemplateModelMother.UID_HOME_PAGE;
import static de.hybris.platform.cmsfacades.util.models.PageTemplateModelMother.UID_SEARCH_PAGE;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmsfacades.util.models.PageTemplateModelMother;
import de.hybris.platform.cmsfacades.util.models.SiteModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.AbstractPageData;
import de.hybris.platform.cmswebservices.data.PageListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;
import java.util.Locale;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class PageControllerWebServiceTest extends ApiBaseIntegrationTest
{

	private static final String BASE_PAGE_ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pages";

	private static final String EN_LOCALE = Locale.ENGLISH.toString();
	private static final String INEXISTENCE_PAGE = "inexistence_page";
	private static final String TEMPLATE = "template";
	private static final String UID_INVALID1 = "invalidUid1";
	private static final String UID_INVALID2 = "invalidUid2";
	private static final String UIDS_REQUEST_PARAMETER = "uids";

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private ContentPageModelMother contentPageModelMother;
	@Resource
	private SiteModelMother siteModelMother;
	@Resource
	private PageTemplateModelMother pageTemplateModelMother;

	private CatalogVersionModel catalogVersion;

	@Before
	public void setup()
	{
		siteModelMother.createNorthAmericaElectronicsWithAppleStagedCatalog();
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();

		contentPageModelMother.searchPage(catalogVersion);
		contentPageModelMother.homePage(catalogVersion);
	}

	@Test
	public void shouldGetOnePage()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>())) //
				.path(UID_HOMEPAGE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final AbstractPageData pageData = response.readEntity(AbstractPageData.class);

		assertEquals(pageData.getUid(), UID_HOMEPAGE);
	}

	@Test
	public void shouldNotGetPage_InexistenceUid()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>())).path(INEXISTENCE_PAGE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.NOT_FOUND, response);
	}

	@Test
	public void shouldGetAllValidSpecificContentPages()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>()))
				.queryParam(UIDS_REQUEST_PARAMETER, UID_HOMEPAGE + "," + UID_SEARCHPAGE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageListData entity = response.readEntity(PageListData.class);
		assertThat(entity.getPages(), containsInAnyOrder( //
				allOf( //
						hasProperty(UID, equalTo(UID_HOMEPAGE)), //
						hasProperty(TYPECODE, equalTo(ContentPageModel._TYPECODE)), //
						hasProperty(TEMPLATE, equalTo(UID_HOME_PAGE)), //
						hasProperty(TITLE, hasEntry(EN_LOCALE, TITLE_HOMEPAGE))), //
				allOf( //
						hasProperty(UID, equalTo(UID_SEARCHPAGE)), //
						hasProperty(TYPECODE, equalTo(ContentPageModel._TYPECODE)), //
						hasProperty(TEMPLATE, equalTo(UID_SEARCH_PAGE)), //
						hasProperty(TITLE, hasEntry(EN_LOCALE, TITLE_SEARCHPAGE))) //
		));
	}

	@Test
	public void shouldGetOnlyTheValidSpecificContentPages()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>()))
				.queryParam(UIDS_REQUEST_PARAMETER, UID_HOMEPAGE + "," + UID_INVALID1).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageListData entity = response.readEntity(PageListData.class);
		assertThat(entity.getPages(), hasItem( //
				allOf( //
						hasProperty(UID, equalTo(UID_HOMEPAGE)), //
						hasProperty(TYPECODE, equalTo(ContentPageModel._TYPECODE)), //
						hasProperty(TEMPLATE, equalTo(UID_HOME_PAGE)), //
						hasProperty(TITLE, hasEntry(EN_LOCALE, TITLE_HOMEPAGE))) //
		));
	}

	@Test
	public void shouldReturnEmpty_AllSpecifiedContentPagesInvalid()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>()))
				.queryParam(UIDS_REQUEST_PARAMETER, UID_INVALID1 + "," + UID_INVALID2).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageListData entity = response.readEntity(PageListData.class);
		assertThat(entity.getPages(), empty());

	}
}