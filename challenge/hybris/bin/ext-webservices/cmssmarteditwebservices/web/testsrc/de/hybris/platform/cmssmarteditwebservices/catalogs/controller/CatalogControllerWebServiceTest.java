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
package de.hybris.platform.cmssmarteditwebservices.catalogs.controller;

import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.ONLINE;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_APPLE;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_PHONES;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cmsfacades.util.models.SiteModelMother;
import de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants;
import de.hybris.platform.cmssmarteditwebservices.dto.CatalogListWsDTO;
import de.hybris.platform.cmssmarteditwebservices.dto.CatalogWsDTO;
import de.hybris.platform.cmssmarteditwebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hamcrest.Matcher;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmssmarteditwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class CatalogControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String CONTENT_CATALOG_ENDPOINT = "/v1/sites/{siteId}/contentcatalogs";
	private static final String PRODUCT_CATALOG_ENDPOINT = "/v1/sites/{siteId}/productcatalogs";

	private static final String ACTIVE = "active";
	private static final String CATALOG_ID = "catalogId";
	private static final String NAME = "name";
	private static final String PAGE_DISPLAY_CONDITIONS = "pageDisplayConditions";
	private static final String VERSION = "version";
	private static final String VERSIONS = "versions";

	@Resource
	private SiteModelMother siteModelMother;

	@Test
	public void shouldGetAllContentCatalogs()
	{
		siteModelMother.createElectronicsWithAppleStagedAndOnlineCatalog();

		final Response response = getWsSecuredRequestBuilder()
				.path(replaceUriVariablesWithDefaults(CONTENT_CATALOG_ENDPOINT, new HashMap<String, String>())).build()
				.accept(MediaType.APPLICATION_JSON).get();

		assertResponse(Status.OK, response);

		final CatalogListWsDTO catalogs = response.readEntity(CatalogListWsDTO.class);

		assertThat(catalogs.getCatalogs(), hasSize(greaterThanOrEqualTo(1)));

		final Matcher<CatalogWsDTO> EXPECTED_APPLE_CONTENT_CATALOG = //
				allOf(hasProperty(CATALOG_ID, equalTo(ID_APPLE.name())), //
						hasProperty(NAME, equalTo(ID_APPLE.getHumanName())), //
						hasProperty(VERSIONS, hasSize(greaterThanOrEqualTo(2))), //
						hasProperty(VERSIONS,
								hasItem( //
										allOf(hasProperty(ACTIVE, equalTo(true)), //
												hasProperty(VERSION, equalTo(ONLINE.getVersion())), //
												hasProperty(PAGE_DISPLAY_CONDITIONS, hasSize(greaterThanOrEqualTo(3)))))));
		assertThat(catalogs.getCatalogs(), hasItem(EXPECTED_APPLE_CONTENT_CATALOG));
	}

	@Test
	public void shouldGetAllProductCatalogs()
	{
		siteModelMother.createNorthAmericaElectronicsWithAppleStagedCatalog();

		final Response response = getWsSecuredRequestBuilder()
				.path(replaceUriVariablesWithDefaults(PRODUCT_CATALOG_ENDPOINT, new HashMap<String, String>())).build()
				.accept(MediaType.APPLICATION_JSON).get();

		assertResponse(Status.OK, response);

		final CatalogListWsDTO catalogs = response.readEntity(CatalogListWsDTO.class);

		assertThat(catalogs.getCatalogs(), hasSize(greaterThanOrEqualTo(1)));

		final Matcher<CatalogWsDTO> EXPECTED_PHONE_PRODUCT_CATALOG = //
				allOf(hasProperty(CATALOG_ID, equalTo(ID_PHONES.name())), //
						hasProperty(NAME, equalTo(ID_PHONES.getHumanName())), //
						hasProperty(VERSIONS, hasSize(greaterThanOrEqualTo(3))), //
						hasProperty(VERSIONS,
								hasItem( //
										allOf(hasProperty(ACTIVE, equalTo(true)), //
												hasProperty(VERSION, equalTo(ONLINE.getVersion()))))));
		assertThat(catalogs.getCatalogs(), hasItem(EXPECTED_PHONE_PRODUCT_CATALOG));
	}

}
