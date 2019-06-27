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
package de.hybris.platform.cmswebservices.sites.controller;

import static de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite.APPAREL;
import static de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite.ELECTRONICS;
import static de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite.POWER_TOOLS;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_ONLINE;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_READONLY;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_STAGED;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.SiteData;
import de.hybris.platform.cmswebservices.data.SiteListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.Collection;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class SiteControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String SITES_ENDPOINT = "/v1/sites";

	private static final String NAME = "name";
	private static final String UID = "uid";

	private static Matcher<Object> EXPECTED_APPAREL_SITE;
	private static Matcher<Object> EXPECTED_ELECTRONICS_SITE;

	@Resource
	private CMSSiteModelMother cmsSiteModelMother;

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;

	@Resource
	private ContentPageModelMother contentPageModelMother;

	@Before
	public void setUp()
	{
		EXPECTED_APPAREL_SITE = allOf( //
				hasProperty(NAME, equalTo(APPAREL.getNames())), //
				hasProperty(UID, equalTo(APPAREL.getUid())));

		EXPECTED_ELECTRONICS_SITE = allOf( //
				hasProperty(NAME, equalTo(ELECTRONICS.getNames())), //
				hasProperty(UID, equalTo(ELECTRONICS.getUid())));
	}

	@Test
	public void getAllSites_WillReturnAnEmptyListOfSites_WhenNothingIsAvailable() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(SITES_ENDPOINT).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final SiteListData entity = response.readEntity(SiteListData.class);
		assertThat(entity.getSites(), empty());
	}

	@Test
	public void getAllSites_ShouldReturnEmptyList_IfUserHasNoAccess() throws Exception
	{
		// Arrange
		// Site exists, but user has no permissions
		final CatalogVersionModel[] allowedCatalogVersionModels = new CatalogVersionModel[]
		{ catalogVersionModelMother.createOnlineCatalogVersionModelWithId(ID_ONLINE),
				catalogVersionModelMother.createStagedCatalogVersionModelWithId(ID_STAGED) };

		contentPageModelMother.homePage(allowedCatalogVersionModels[0]);
		cmsSiteModelMother.createSiteWithTemplate(APPAREL, allowedCatalogVersionModels);

		// Act
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(SITES_ENDPOINT).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		// Assert
		assertResponse(Status.OK, response);

		final SiteListData entity = response.readEntity(SiteListData.class);
		assertThat(entity.getSites(), empty());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAllSites_WillReturnAListOfSitesWithApparel_WhenApparelIsAvailable() throws Exception
	{
		// cmsmanager does not have any read or write permissions on the ID_ONLINE and ID_STAGED catalog versions
		final CatalogVersionModel[] notAllowedCatalogVersionModels = new CatalogVersionModel[]
		{ catalogVersionModelMother.createOnlineCatalogVersionModelWithId(ID_ONLINE),
				catalogVersionModelMother.createStagedCatalogVersionModelWithId(ID_STAGED) };

		// cmsmanager has all the permissions defined in resources/cmswebservices/test/impex/essentialTestDataAuth.impex
		final CatalogVersionModel[] allowedCatalogVersionModels = new CatalogVersionModel[]
		{ catalogVersionModelMother.createAppleOnlineCatalogVersionModel(),
				catalogVersionModelMother.createAppleStagedCatalogVersionModel() };

		// create a homepage for the thumbnail
		contentPageModelMother.homePage(allowedCatalogVersionModels[0]);
		cmsSiteModelMother.createSiteWithTemplate(APPAREL, allowedCatalogVersionModels);
		cmsSiteModelMother.createSiteWithTemplate(ELECTRONICS, allowedCatalogVersionModels);
		cmsSiteModelMother.createSiteWithTemplate(POWER_TOOLS, notAllowedCatalogVersionModels);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(SITES_ENDPOINT).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final SiteListData entity = response.readEntity(SiteListData.class);
		assertThat(entity.getSites(), hasSize(2));
		final Collection<SiteData> sites = entity.getSites();
		assertThat(sites, containsInAnyOrder(is(EXPECTED_APPAREL_SITE), is(EXPECTED_ELECTRONICS_SITE)));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAllSites_WillReturnOnlyThoseSitesThatHaveAtleastReadAccessToInactiveCatalogVersion() throws Exception
	{
		// cmsmanager has all the permissions defined in resources/cmswebservices/test/impex/essentialTestDataAuth.impex
		final CatalogVersionModel[] notAllowedCatalogVersionModels = new CatalogVersionModel[]
		{ catalogVersionModelMother.createOnlineCatalogVersionModelWithId(ID_ONLINE),
				catalogVersionModelMother.createStagedCatalogVersionModelWithId(ID_STAGED) };

		// cmsmanager has all the permissions defined in resources/cmswebservices/test/impex/essentialTestDataAuth.impex
		final CatalogVersionModel[] catalogVersionModelsWithReadAccessToStagedVersion = new CatalogVersionModel[]
		{ catalogVersionModelMother.createStagedCatalogVersionModelWithId(ID_READONLY),
				catalogVersionModelMother.createOnlineCatalogVersionModelWithId(ID_READONLY) };

		// create a homepage for the thumbnail
		contentPageModelMother.homePage(catalogVersionModelsWithReadAccessToStagedVersion[0]);
		cmsSiteModelMother.createSiteWithTemplate(APPAREL, catalogVersionModelsWithReadAccessToStagedVersion);
		cmsSiteModelMother.createSiteWithTemplate(ELECTRONICS, notAllowedCatalogVersionModels);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(SITES_ENDPOINT).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final SiteListData entity = response.readEntity(SiteListData.class);
		assertThat(entity.getSites(), hasSize(1));
	}

	@Test
	public void theApparelSiteItem_WillHaveUidAndBaseUrlAndSiteName() throws Exception
	{
		// cmsmanager has all the permissions defined in resources/cmswebservices/test/impex/essentialTestDataAuth.impex
		final CatalogVersionModel[] allowedCatalogVersionModels = new CatalogVersionModel[]
		{ catalogVersionModelMother.createAppleOnlineCatalogVersionModel(),
				catalogVersionModelMother.createAppleStagedCatalogVersionModel() };

		// create a homepage for the thumbnail
		contentPageModelMother.homePage(allowedCatalogVersionModels[0]);
		cmsSiteModelMother.createSiteWithTemplate(APPAREL, allowedCatalogVersionModels);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(SITES_ENDPOINT).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final SiteListData entity = response.readEntity(SiteListData.class);
		final SiteData siteData = entity.getSites().iterator().next();

		assertThat(siteData, is(EXPECTED_APPAREL_SITE));
	}

}
