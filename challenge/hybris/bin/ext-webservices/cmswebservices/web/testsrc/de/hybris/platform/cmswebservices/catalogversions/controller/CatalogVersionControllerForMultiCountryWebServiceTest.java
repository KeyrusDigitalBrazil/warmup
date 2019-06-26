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
package de.hybris.platform.cmswebservices.catalogversions.controller;

import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.ONLINE;
import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.STAGED;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.MULTI_COUNTRY_ID_CARS;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.MULTI_COUNTRY_ID_EUROPE_CARS;
import static de.hybris.platform.cmsfacades.util.models.SiteModelMother.MULTI_COUNTRY_CAR_SITE;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.MODE_CLONEABLE_TO;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.QUERY_PARAM_MODE;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_CATALOG_ID;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_SITE_ID;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_VERSION_ID;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cmsfacades.data.CatalogVersionData;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.CatalogVersionListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class CatalogVersionControllerForMultiCountryWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String ENDPOINT_TARGETS = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/targets";

	@Before
	public void start() throws ImpExException
	{
		importCsv("/cmswebservices/test/impex/essentialMultiCountryTestDataAuth.impex", "utf-8");
	}

	@Test
	public void shouldReturn2WritableContentCatalogVersionsForEuropeOnline()
	{
		// GIVEN
		final Map<String, String> params = new HashMap<>();
		params.put(URI_CATALOG_ID, MULTI_COUNTRY_ID_EUROPE_CARS.name());
		params.put(URI_VERSION_ID, ONLINE.getVersion());
		params.put(URI_SITE_ID, MULTI_COUNTRY_CAR_SITE);

		// WHEN
		final Response response = getMultiCountryCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT_TARGETS, params))
				.queryParam(QUERY_PARAM_MODE, MODE_CLONEABLE_TO).build()
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		// THEN
		assertResponse(Response.Status.OK, response);
		final CatalogVersionListData entity = response.readEntity(CatalogVersionListData.class);
		final List<CatalogVersionData> versions = entity.getVersions();

		assertThat("CatalogVersionListData targets should return 2 subcatalogs for EuropeCars/Online", entity.getVersions().size(),
				equalTo(2));
		assertTrue("CatalogVersionListData targets should contain only writable catalogs",
				versions.stream().noneMatch(CatalogVersionData::getActive));
	}

	@Test
	public void shouldReturnCurrentLevelWritableCatalogVersionsForEuropeStaged()
	{
		// GIVEN
		final Map<String, String> params = new HashMap<>();
		params.put(URI_CATALOG_ID, MULTI_COUNTRY_ID_EUROPE_CARS.name());
		params.put(URI_VERSION_ID, STAGED.getVersion());
		params.put(URI_SITE_ID, MULTI_COUNTRY_CAR_SITE);

		// WHEN
		final Response response = getMultiCountryCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT_TARGETS, params))
				.queryParam(QUERY_PARAM_MODE, MODE_CLONEABLE_TO).build()
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		// THEN
		assertResponse(Response.Status.OK, response);
		final CatalogVersionListData entity = response.readEntity(CatalogVersionListData.class);
		final List<CatalogVersionData> versions = entity.getVersions();
		assertThat("CatalogVersionListData targets should return 1 subcatalog for EuropeCars/Staged", entity.getVersions().size(),
				equalTo(1));
		assertTrue("CatalogVersionListData targets should contain only writable catalogs",
				versions.stream().noneMatch(CatalogVersionData::getActive));
	}

	@Test
	public void shouldReturn3WritableContentCatalogVersionsForGlobalOnline()
	{
		// GIVEN
		final Map<String, String> params = new HashMap<>();
		params.put(URI_CATALOG_ID, MULTI_COUNTRY_ID_CARS.name());
		params.put(URI_VERSION_ID, ONLINE.getVersion());
		params.put(URI_SITE_ID, MULTI_COUNTRY_CAR_SITE);

		// WHEN
		final Response response = getMultiCountryCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT_TARGETS, params))
				.queryParam(QUERY_PARAM_MODE, MODE_CLONEABLE_TO).build()
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		// THEN
		assertResponse(Response.Status.OK, response);
		final CatalogVersionListData entity = response.readEntity(CatalogVersionListData.class);
		final List<CatalogVersionData> versions = entity.getVersions();
		assertThat("CatalogVersionListData targets should return 3 subcatalog for EuropeCars/Staged", entity.getVersions().size(),
				equalTo(3));
		assertTrue("CatalogVersionListData targets should contain only writable catalogs",
				versions.stream().noneMatch(CatalogVersionData::getActive));
	}
}
