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
package de.hybris.platform.cmswebservices.pagesrestrictions.controller;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.util.models.CMSTimeRestrictionModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmsfacades.util.models.SiteModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.PageRestrictionData;
import de.hybris.platform.cmswebservices.data.PageRestrictionListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
public class PagesRestrictionsControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String INVALID_ID = "invalid-id";
	private static final String OTHER_PAGE = "other-page";
	private static final String OTHER_PAGE2 = "other-page2";

	private static final String PAGE_RESTRICTION_ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagesrestrictions";
	private static final String PAGE_RESTRICTION_UPDATE_ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagesrestrictions/pages/{pageId}";

	@Resource
	private CMSTimeRestrictionModelMother timeRestrictionModelMother;
	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private SiteModelMother siteModelMother;
	@Resource
	private ContentPageModelMother contentPageModelMother;

	private CatalogVersionModel catalogVersion;

	@Before
	public void setup()
	{
		siteModelMother.createNorthAmericaElectronicsWithAppleStagedCatalog();
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();

		final AbstractPageModel homePage = contentPageModelMother.homePage(catalogVersion);
		final AbstractPageModel searchPage = contentPageModelMother.searchPage(catalogVersion);
		contentPageModelMother.somePage(catalogVersion, OTHER_PAGE, "other-page-title", CmsPageStatus.ACTIVE);

		timeRestrictionModelMother.today(catalogVersion, homePage, searchPage);
		timeRestrictionModelMother.tomorrow(catalogVersion, homePage);
	}

	@Test
	public void shouldGetPageRestrictionsForSinglePage() throws Exception
	{
		final Response responseForSearch = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_RESTRICTION_ENDPOINT, new HashMap<>()))
				.queryParam(CmswebservicesConstants.URI_PAGE_ID, ContentPageModelMother.UID_SEARCHPAGE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, responseForSearch);

		final PageRestrictionListData searchPageRestrictionListEntity = responseForSearch.readEntity(PageRestrictionListData.class);
		assertThat(searchPageRestrictionListEntity.getPageRestrictionList().size(), is(1));
		assertThat(searchPageRestrictionListEntity.getPageRestrictionList().get(0).getPageId(),
				is(ContentPageModelMother.UID_SEARCHPAGE));
		assertThat(searchPageRestrictionListEntity.getPageRestrictionList().get(0).getRestrictionId(),
				is(CMSTimeRestrictionModelMother.UID_TODAY));
	}

	@Test
	public void shouldSuccessfullyPutPageRestrictions() throws Exception
	{
		// Given that the homepage has two restrictions
		final Response responseForGetOnHomepage = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_RESTRICTION_ENDPOINT, new HashMap<>()))
				.queryParam(CmswebservicesConstants.URI_PAGE_ID, ContentPageModelMother.UID_HOMEPAGE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, responseForGetOnHomepage);

		final PageRestrictionListData homepageRestrictionListEntity = responseForGetOnHomepage
				.readEntity(PageRestrictionListData.class);
		assertThat(homepageRestrictionListEntity.getPageRestrictionList().size(), is(2));

		// When we perform a PUT with one restriction
		final HashMap<String, String> collectionInstanceMap = new HashMap<>();
		collectionInstanceMap.put(CmswebservicesConstants.URI_PAGE_ID, ContentPageModelMother.UID_HOMEPAGE);
		final PageRestrictionListData entity = createAnEntityForRestrictionOnPage(ContentPageModelMother.UID_HOMEPAGE,
				CMSTimeRestrictionModelMother.UID_TOMORROW);
		final Response responseForPutOnHomepage = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_RESTRICTION_UPDATE_ENDPOINT, collectionInstanceMap)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(entity, MediaType.APPLICATION_JSON));


		// Then instead of two there's one restriction on the page
		assertResponse(Status.OK, responseForPutOnHomepage);

		final PageRestrictionListData resultEntity = responseForPutOnHomepage.readEntity(PageRestrictionListData.class);
		assertThat(resultEntity.getPageRestrictionList().size(), is(1));
		assertThat(resultEntity.getPageRestrictionList().get(0).getRestrictionId(), is(CMSTimeRestrictionModelMother.UID_TOMORROW));
	}

	@Test
	public void shouldFailPutPageRestrictions_AmbiguousPageId() throws Exception
	{
		// When we perform a PUT with ambiguous page id
		final HashMap<String, String> collectionInstanceMap = new HashMap<>();
		collectionInstanceMap.put(CmswebservicesConstants.URI_PAGE_ID, ContentPageModelMother.UID_HOMEPAGE);
		final PageRestrictionListData entity = createAnEntityForRestrictionOnPage(ContentPageModelMother.UID_SEARCHPAGE,
				CMSTimeRestrictionModelMother.UID_TOMORROW);
		final Response responseForPutOnHomepage = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_RESTRICTION_UPDATE_ENDPOINT, collectionInstanceMap)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(entity, MediaType.APPLICATION_JSON));

		// Then should return 404
		assertResponse(Status.NOT_FOUND, responseForPutOnHomepage);
	}

	protected PageRestrictionListData createAnEntityForRestrictionOnPage(final String pageUid, final String restrictionUid)
	{
		final PageRestrictionListData entity = new PageRestrictionListData();
		final List<PageRestrictionData> pageRestrictionList = new ArrayList<>();
		final PageRestrictionData pageRestriction = new PageRestrictionData();
		pageRestriction.setPageId(pageUid);
		pageRestriction.setRestrictionId(restrictionUid);
		pageRestrictionList.add(pageRestriction);
		entity.setPageRestrictionList(pageRestrictionList);
		return entity;
	}

	@Test
	public void shouldFailPutPageRestrictions_InvalidPageId()
	{
		final PageRestrictionListData entity = createAnEntityForRestrictionOnPage(ContentPageModelMother.UID_HOMEPAGE,
				CMSTimeRestrictionModelMother.UID_TOMORROW);
		final Map<String, String> params = new HashMap<>();
		params.put(CmswebservicesConstants.URI_PAGE_ID, OTHER_PAGE);
		final Response responseForPutOnHomepage = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_RESTRICTION_UPDATE_ENDPOINT, params)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(entity, MediaType.APPLICATION_JSON));

		assertResponse(Status.NOT_FOUND, responseForPutOnHomepage);

	}

	@Test
	public void shouldFailPutPageRestrictions_DeletedPage()
	{
		final AbstractPageModel someDeletedPage = contentPageModelMother.somePage(catalogVersion, "some-deleted-page",
				"Some Deleted Page", CmsPageStatus.DELETED);
		timeRestrictionModelMother.nextWeek(catalogVersion, someDeletedPage);

		final PageRestrictionListData entity = createAnEntityForRestrictionOnPage(ContentPageModelMother.UID_HOMEPAGE,
				CMSTimeRestrictionModelMother.UID_TOMORROW);
		final Map<String, String> params = new HashMap<>();
		params.put(CmswebservicesConstants.URI_PAGE_ID, OTHER_PAGE);

		final Response responseForPutOnHomepage = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_RESTRICTION_UPDATE_ENDPOINT, params)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(entity, MediaType.APPLICATION_JSON));

		assertResponse(Status.NOT_FOUND, responseForPutOnHomepage);
	}

	@Test
	public void shouldNotGetPageRestrictions_NoneFound()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_RESTRICTION_ENDPOINT, new HashMap<>()))
				.queryParam(CmswebservicesConstants.URI_PAGE_ID, OTHER_PAGE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageRestrictionListData entity = response.readEntity(PageRestrictionListData.class);
		assertThat(entity.getPageRestrictionList(), empty());
	}

	@Test
	public void shouldNotGetPageRestrictions_PageDoesNotExist()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_RESTRICTION_ENDPOINT, new HashMap<>()))
				.queryParam(CmswebservicesConstants.URI_PAGE_ID, INVALID_ID).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageRestrictionListData entity = response.readEntity(PageRestrictionListData.class);
		assertThat(entity.getPageRestrictionList(), empty());
	}

	@Test
	public void shouldGetAllPagesRestrictions()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_RESTRICTION_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageRestrictionListData entity = response.readEntity(PageRestrictionListData.class);
		assertThat(entity.getPageRestrictionList().size(), is(3));

		final Map<String, List<PageRestrictionData>> dataMap = entity.getPageRestrictionList().stream()
				.collect(Collectors.groupingBy(PageRestrictionData::getPageId));
		final List<String> homepageRestrictionIds = dataMap.get(ContentPageModelMother.UID_HOMEPAGE).stream()
				.map(page -> page.getRestrictionId()).collect(Collectors.toList());
		final List<String> searchpageRestrictionIds = dataMap.get(ContentPageModelMother.UID_SEARCHPAGE).stream()
				.map(page -> page.getRestrictionId()).collect(Collectors.toList());

		assertThat(homepageRestrictionIds,
				containsInAnyOrder(CMSTimeRestrictionModelMother.UID_TOMORROW, CMSTimeRestrictionModelMother.UID_TODAY));
		assertThat(searchpageRestrictionIds, containsInAnyOrder(CMSTimeRestrictionModelMother.UID_TODAY));
	}

	@Test
	public void shouldGetAllPagesRestrictionsWithoutDeletedPage()
	{
		final AbstractPageModel someDeletedPage = contentPageModelMother.somePage(catalogVersion, "some-deleted-page",
				"Some Deleted Page", CmsPageStatus.DELETED);
		timeRestrictionModelMother.nextWeek(catalogVersion, someDeletedPage);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_RESTRICTION_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageRestrictionListData entity = response.readEntity(PageRestrictionListData.class);
		assertThat(entity.getPageRestrictionList().size(), is(3));

		final Map<String, List<PageRestrictionData>> dataMap = entity.getPageRestrictionList().stream()
				.collect(Collectors.groupingBy(PageRestrictionData::getPageId));
		final List<String> homepageRestrictionIds = dataMap.get(ContentPageModelMother.UID_HOMEPAGE).stream()
				.map(page -> page.getRestrictionId()).collect(Collectors.toList());
		final List<String> searchpageRestrictionIds = dataMap.get(ContentPageModelMother.UID_SEARCHPAGE).stream()
				.map(page -> page.getRestrictionId()).collect(Collectors.toList());

		assertThat(homepageRestrictionIds,
				containsInAnyOrder(CMSTimeRestrictionModelMother.UID_TOMORROW, CMSTimeRestrictionModelMother.UID_TODAY));
		assertThat(searchpageRestrictionIds, containsInAnyOrder(CMSTimeRestrictionModelMother.UID_TODAY));
	}

	@Test
	public void shouldGetPagesRestrictionsForPageIds()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_RESTRICTION_ENDPOINT, new HashMap<>()))
				.queryParam(CmswebservicesConstants.URI_PAGE_IDS,
						ContentPageModelMother.UID_HOMEPAGE + "," + ContentPageModelMother.UID_SEARCHPAGE)
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageRestrictionListData entity = response.readEntity(PageRestrictionListData.class);
		assertThat(entity.getPageRestrictionList().size(), is(3));

		final Map<String, List<PageRestrictionData>> dataMap = entity.getPageRestrictionList().stream()
				.collect(Collectors.groupingBy(PageRestrictionData::getPageId));
		final List<String> homepageRestrictionIds = dataMap.get(ContentPageModelMother.UID_HOMEPAGE).stream()
				.map(page -> page.getRestrictionId()).collect(Collectors.toList());
		final List<String> searchpageRestrictionIds = dataMap.get(ContentPageModelMother.UID_SEARCHPAGE).stream()
				.map(page -> page.getRestrictionId()).collect(Collectors.toList());

		assertThat(homepageRestrictionIds,
				containsInAnyOrder(CMSTimeRestrictionModelMother.UID_TOMORROW, CMSTimeRestrictionModelMother.UID_TODAY));
		assertThat(searchpageRestrictionIds, containsInAnyOrder(CMSTimeRestrictionModelMother.UID_TODAY));
	}

	@Test
	public void shouldGetEmptyPagesRestrictionsForPageIds_PageIdNotFound()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_RESTRICTION_ENDPOINT, new HashMap<>()))
				.queryParam(CmswebservicesConstants.URI_PAGE_IDS, OTHER_PAGE + "," + OTHER_PAGE2).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageRestrictionListData entity = response.readEntity(PageRestrictionListData.class);
		assertThat(entity.getPageRestrictionList(), empty());
	}

	@Test
	public void shouldGetEmptyPagesRestrictionsForPageIds_OneInvalidPageId()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_RESTRICTION_ENDPOINT, new HashMap<>()))
				.queryParam(CmswebservicesConstants.URI_PAGE_IDS, INVALID_ID + "," + ContentPageModelMother.UID_SEARCHPAGE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageRestrictionListData entity = response.readEntity(PageRestrictionListData.class);
		assertThat(entity.getPageRestrictionList().size(), is(1));
	}
}

