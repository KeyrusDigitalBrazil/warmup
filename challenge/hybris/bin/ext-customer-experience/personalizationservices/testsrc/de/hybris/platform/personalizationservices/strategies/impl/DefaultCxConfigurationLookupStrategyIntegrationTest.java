/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
/**
 *
 */
package de.hybris.platform.personalizationservices.strategies.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.personalizationservices.AbstractCxServiceTest;
import de.hybris.platform.personalizationservices.model.config.CxConfigModel;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.site.BaseSiteService;

import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCxConfigurationLookupStrategyIntegrationTest extends AbstractCxServiceTest
{
	private static final String BASE_SITE_ID = "testSite";
	private static final String CATALOG_WITH_MULTIPLE_BASE_SITE = "testCatalog";
	private static final String CATALOG_WITH_SINGLE_BASE_SITE = "testCatalog1";
	private static final String CATALOG_VERSION = "Online";

	@Resource(name = "defaultCxConfigurationLookupStrategy")
	private DefaultCxConfigurationLookupStrategy strategy;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private CatalogVersionService catalogVersionService;


	@Before
	public void setupTest() throws Exception
	{
		createCoreData();
		importData(new ClasspathImpExResource("/personalizationservices/test/testdata_cxconfig.impex", "UTF-8"));
	}

	@Test
	public void getConfigurationTest()
	{
		//given
		baseSiteService.setCurrentBaseSite(BASE_SITE_ID, false);

		//when
		final Optional<CxConfigModel> configuration = strategy.getConfiguration();

		//then
		assertNotNull(configuration);
		assertTrue(configuration.isPresent());
		assertTrue(isBaseSiteRelated(configuration.get(), BASE_SITE_ID));
	}


	@Test
	public void getConfigurationForBaseSiteTest()
	{
		//given
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(BASE_SITE_ID);

		//when
		final Optional<CxConfigModel> configuration = strategy.getConfiguration(baseSite);

		//then
		assertNotNull(configuration);
		assertTrue(configuration.isPresent());
		assertTrue(isBaseSiteRelated(configuration.get(), BASE_SITE_ID));
	}


	@Test(expected = IllegalArgumentException.class)
	public void getConfigurationWithNullBaseSiteTest()
	{
		//when
		strategy.getConfiguration(null);
	}


	@Test
	public void getConfigurationForCatalogVersionTest()
	{
		//given
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_WITH_SINGLE_BASE_SITE, CATALOG_VERSION);

		//when
		final Set<CxConfigModel> configuration = strategy.getConfigurations(catalogVersion);

		//then
		assertNotNull(configuration);
		assertTrue(configuration.size() == 1);
		assertTrue(isCatalogVersionRelated(configuration.iterator().next(), catalogVersion));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getConfigurationWithNullCatalogVersionTest()
	{
		//when
		strategy.getConfigurations(null);
	}

	@Test
	public void getConfigurationForCatalogVersionWhenMultipleSitesTest()
	{
		//given
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_WITH_MULTIPLE_BASE_SITE,
				CATALOG_VERSION);

		//when
		final Set<CxConfigModel> configuration = strategy.getConfigurations(catalogVersion);

		//then
		assertNotNull(configuration);
		assertTrue(configuration.size() == 3);
		assertTrue(isCatalogVersionRelated(configuration.iterator().next(), catalogVersion));
	}

	private boolean isCatalogVersionRelated(final CxConfigModel config, final CatalogVersionModel catalogVersion)
	{
		return config.getBaseSites().stream()//
				.map(site -> site.getStores())//
				.flatMap(stores -> stores.stream())//
				.map(store -> store.getCatalogs())//
				.flatMap(catalogs -> catalogs.stream())//
				.map(c -> c.getCatalogVersions())//
				.flatMap(catalogVersions -> catalogVersions.stream())//
				.anyMatch(cv -> cv.equals(catalogVersion));
	}


	private boolean isBaseSiteRelated(final CxConfigModel config, final String baseSiteId)
	{
		return config.getBaseSites().stream()//
				.filter(bs -> StringUtils.equals(bs.getUid(), baseSiteId)) //
				.findAny() //
				.isPresent();
	}

}
