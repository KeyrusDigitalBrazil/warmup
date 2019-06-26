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
package de.hybris.platform.personalizationcms.service;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.personalizationservices.AbstractCxServiceTest;
import de.hybris.platform.personalizationservices.constants.PersonalizationservicesConstants;
import de.hybris.platform.personalizationservices.enums.CxCatalogLookupType;
import de.hybris.platform.personalizationservices.service.impl.DefaultCxCatalogService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


@IntegrationTest
public class DefaultCxCatalogServiceIntegrationTest extends AbstractCxServiceTest
{
	private static final String TEST_CATALOG = "testCatalog";
	private static final String TEST_CATALOG2 = "testCatalog2";
	private static final String MULTI_CATALOG1 = "multiCatalog1";
	private static final String MULTI_CATALOG2 = "multiCatalog2";
	private static final String MULTI_CATALOG3A = "multiCatalog3a";
	private static final String MULTI_CATALOG3B = "multiCatalog3b";

	private static final String VERSION = "Online";

	@Resource(name = "cxCatalogService")
	DefaultCxCatalogService defaultCxCatalogService;

	@Resource
	ConfigurationService configurationService;

	@Resource
	CatalogVersionService catalogVersionService;

	@Resource
	ModelService modelService;


	@Before
	public void setup() throws ImpExException
	{
		importData(new ClasspathImpExResource("/personalizationcms/test/testdata_personalizationcms_multiplecatalogs.impex", "UTF-8"));
	}

	private void setCatalogsInSession(final String... ids)
	{
		catalogVersionService.setSessionCatalogVersions(Collections.emptyList());
		for (final String id : ids)
		{
			addCatalogToSession(id);
		}
	}

	private void addCatalogToSession(final String name)
	{
		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion(name, VERSION);
		catalogVersionService.addSessionCatalogVersion(cv);
	}

	@Test
	public void testAllCatalogs()
	{
		//given
		configurationService.getConfiguration().setProperty(PersonalizationservicesConstants.CATALOG_LOOKUP_TYPE,
				CxCatalogLookupType.ALL_CATALOGS.getCode());
		setCatalogsInSession(TEST_CATALOG, TEST_CATALOG2, MULTI_CATALOG2);

		//when
		final List<CatalogVersionModel> catalogVersionsForCalculation = defaultCxCatalogService.getConfiguredCatalogVersions();

		//then
		Assert.assertNotNull(catalogVersionsForCalculation);
		Assert.assertEquals(2, catalogVersionsForCalculation.size());


		assertContains(catalogVersionsForCalculation, TEST_CATALOG, TEST_CATALOG2);
	}

	@Test
	public void testAllCatalogsWithMulticountry()
	{
		//given
		configurationService.getConfiguration().setProperty(PersonalizationservicesConstants.CATALOG_LOOKUP_TYPE,
				CxCatalogLookupType.ALL_CATALOGS.getCode());
		setCatalogsInSession(TEST_CATALOG, MULTI_CATALOG3B, MULTI_CATALOG1, MULTI_CATALOG2, MULTI_CATALOG3A);

		//when
		final List<CatalogVersionModel> catalogVersionsForCalculation = defaultCxCatalogService.getConfiguredCatalogVersions();

		//then
		Assert.assertNotNull(catalogVersionsForCalculation);
		Assert.assertEquals(3, catalogVersionsForCalculation.size());
		//multicatalog1 needs to be last!
		Assert.assertEquals(MULTI_CATALOG1, catalogVersionsForCalculation.get(2).getCatalog().getId());
		assertContains(catalogVersionsForCalculation, TEST_CATALOG, MULTI_CATALOG3A);
	}

	@Test
	public void testLeafCatalogs()
	{
		//given
		configurationService.getConfiguration().setProperty(PersonalizationservicesConstants.CATALOG_LOOKUP_TYPE,
				CxCatalogLookupType.LEAF_CATALOGS.getCode());
		setCatalogsInSession(TEST_CATALOG, MULTI_CATALOG1, MULTI_CATALOG2, MULTI_CATALOG3A, MULTI_CATALOG3B);


		//when
		final List<CatalogVersionModel> catalogVersionsForCalculation = defaultCxCatalogService.getConfiguredCatalogVersions();

		//then
		Assert.assertNotNull(catalogVersionsForCalculation);
		Assert.assertEquals(2, catalogVersionsForCalculation.size());
		assertContains(catalogVersionsForCalculation, TEST_CATALOG, MULTI_CATALOG3A);
	}

	@Test
	public void testLeafOrParentCatalogs()
	{
		//given
		configurationService.getConfiguration().setProperty(PersonalizationservicesConstants.CATALOG_LOOKUP_TYPE,
				CxCatalogLookupType.LEAF_CLOSEST_ANCESTOR_CATALOGS.getCode());
		setCatalogsInSession(MULTI_CATALOG1, MULTI_CATALOG2, MULTI_CATALOG3B);


		//when
		final List<CatalogVersionModel> catalogVersionsForCalculation = defaultCxCatalogService.getConfiguredCatalogVersions();

		//then
		Assert.assertNotNull(catalogVersionsForCalculation);
		Assert.assertEquals(1, catalogVersionsForCalculation.size());
		assertEqual(catalogVersionsForCalculation, MULTI_CATALOG1);
	}

	private void assertContains(final Collection<CatalogVersionModel> collection, final String... ids)
	{
		final Set<String> actual = collection.stream().map(CatalogVersionModel::getCatalog).map(CatalogModel::getId)
				.collect(Collectors.toSet());
		final Set<String> expected = Sets.newHashSet(ids);
		Assert.assertTrue("Expected " + actual + " to contain " + expected, actual.containsAll(expected));
	}

	private void assertEqual(final Collection<CatalogVersionModel> collection, final String... ids)
	{
		final List<String> actual = collection.stream().map(CatalogVersionModel::getCatalog).map(CatalogModel::getId)
				.collect(Collectors.toList());
		final List<String> expected = Lists.newArrayList(ids);
		Assert.assertEquals(expected, actual);
	}
}
