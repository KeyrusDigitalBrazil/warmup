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
package de.hybris.platform.adaptivesearch.daos.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.daos.AsSearchConfigurationDao;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AsCategoryAwareSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class DefaultAsSearchConfigurationDaoTest extends ServicelayerTransactionalTest
{
	private static final String NOT_FOUND = "NotFound";

	private static final String SIMPLE_SEARCH_PROF_CODE = "simpleProfile";
	private static final String CAT_AWARE_SEARCH_PROF_CODE = "categoryAwareProfile";

	private static final String SIMPLE_SEARCH_CONF_UID = "simpleConfiguration";
	private static final String NULL_SIMPLE_SEARCH_CONF_UID = "nullSimpleConfiguration";
	private static final String CAT_AWARE_SEARCH_CONF_UID = "categoryAwareConfiguration";
	private static final String NULL_CAT_AWARE_SEARCH_CONF_UID = "nullCategoryAwareConfiguration";

	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";
	private final static String VERSION_TEST = "Test";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private AsSearchProfileService asSearchProfileService;

	@Resource
	private AsSearchConfigurationDao asSearchConfigurationDao;

	@Test
	public void findAllEmpty() throws Exception
	{
		// when
		final List<AbstractAsSearchConfigurationModel> searchConfigurations = asSearchConfigurationDao
				.findAllSearchConfigurations();

		// then
		assertNotNull(searchConfigurations);
		assertEquals(0, searchConfigurations.size());
	}

	@Test
	public void findAll() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchConfigurationDaoTest.impex", CharEncoding.UTF_8);

		// when
		final List<AbstractAsSearchConfigurationModel> searchConfigurations = asSearchConfigurationDao
				.findAllSearchConfigurations();

		// then
		assertNotNull(searchConfigurations);
		assertEquals(6, searchConfigurations.size());
	}

	@Test
	public void findByNullCatalogVersionEmpty() throws Exception
	{
		// when
		final List<AbstractAsSearchConfigurationModel> searchConfigurations = asSearchConfigurationDao
				.findSearchConfigurationsByCatalogVersion(null);

		// then
		assertNotNull(searchConfigurations);
		assertEquals(0, searchConfigurations.size());
	}

	@Test
	public void findByNullCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchConfigurationDaoTest.impex", CharEncoding.UTF_8);

		// when
		final List<AbstractAsSearchConfigurationModel> searchConfigurations = asSearchConfigurationDao
				.findSearchConfigurationsByCatalogVersion(null);

		// then
		assertNotNull(searchConfigurations);
		assertEquals(2, searchConfigurations.size());
	}

	@Test
	public void findByStagedCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchConfigurationDaoTest.impex", CharEncoding.UTF_8);
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		// when
		final List<AbstractAsSearchConfigurationModel> searchConfigurations = asSearchConfigurationDao
				.findSearchConfigurationsByCatalogVersion(catalogVersion);

		// then
		assertNotNull(searchConfigurations);
		assertEquals(2, searchConfigurations.size());
	}

	@Test
	public void findByOnlineCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchConfigurationDaoTest.impex", CharEncoding.UTF_8);
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		// when
		final List<AbstractAsSearchConfigurationModel> searchConfigurations = asSearchConfigurationDao
				.findSearchConfigurationsByCatalogVersion(catalogVersion);

		// then
		assertNotNull(searchConfigurations);
		assertEquals(2, searchConfigurations.size());
	}

	@Test
	public void cannotFindByTestCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchConfigurationDaoTest.impex", CharEncoding.UTF_8);
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_TEST);

		// when
		final List<AbstractAsSearchConfigurationModel> searchConfigurations = asSearchConfigurationDao
				.findSearchConfigurationsByCatalogVersion(catalogVersion);

		// then
		assertNotNull(searchConfigurations);
		assertEquals(0, searchConfigurations.size());
	}

	@Test
	public void findSimpleSearchConfigurationWithNullCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchConfigurationDaoTest.impex", CharEncoding.UTF_8);

		// when
		final Optional<AbstractAsSearchConfigurationModel> searchConfigurationResult = asSearchConfigurationDao
				.findSearchConfigurationByUid(null, NULL_SIMPLE_SEARCH_CONF_UID);

		// then
		assertTrue(searchConfigurationResult.isPresent());
		final AbstractAsSearchConfigurationModel searchConfiguration = searchConfigurationResult.get();
		assertTrue(searchConfiguration instanceof AsSimpleSearchConfigurationModel);
		assertEquals(NULL_SIMPLE_SEARCH_CONF_UID, searchConfiguration.getUid());
	}

	@Test
	public void findSimpleSearchConfigurationWithStagedCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchConfigurationDaoTest.impex", CharEncoding.UTF_8);
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		// when
		final Optional<AbstractAsSearchConfigurationModel> searchConfigurationResult = asSearchConfigurationDao
				.findSearchConfigurationByUid(catalogVersion, SIMPLE_SEARCH_CONF_UID);

		// then
		assertTrue(searchConfigurationResult.isPresent());
		final AbstractAsSearchConfigurationModel searchConfiguration = searchConfigurationResult.get();
		assertTrue(searchConfiguration instanceof AsSimpleSearchConfigurationModel);
		assertEquals(SIMPLE_SEARCH_CONF_UID, searchConfiguration.getUid());
	}

	@Test
	public void findSimpleSearchConfigurationWithFilters() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchConfigurationDaoTest.impex", CharEncoding.UTF_8);
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		final Optional<AbstractAsSearchProfileModel> searchProfileResult = asSearchProfileService
				.getSearchProfileForCode(catalogVersion, SIMPLE_SEARCH_PROF_CODE);

		final Map<String, Object> filters = new HashMap<>();
		filters.put(AsSimpleSearchConfigurationModel.SEARCHPROFILE, searchProfileResult.get());

		// when
		final List<AsSimpleSearchConfigurationModel> searchConfigurations = asSearchConfigurationDao
				.findSearchConfigurations(AsSimpleSearchConfigurationModel.class, filters);

		// then
		assertNotNull(searchConfigurations);
		assertEquals(1, searchConfigurations.size());
		assertEquals(SIMPLE_SEARCH_CONF_UID, searchConfigurations.get(0).getUid());
	}

	@Test
	public void findCategoryAwareSearchConfigurationWithNullCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchConfigurationDaoTest.impex", CharEncoding.UTF_8);

		// when
		final Optional<AbstractAsSearchConfigurationModel> searchConfigurationResult = asSearchConfigurationDao
				.findSearchConfigurationByUid(null, NULL_CAT_AWARE_SEARCH_CONF_UID);

		// then
		assertTrue(searchConfigurationResult.isPresent());
		final AbstractAsSearchConfigurationModel searchConfiguration = searchConfigurationResult.get();
		assertTrue(searchConfiguration instanceof AsCategoryAwareSearchConfigurationModel);
		assertEquals(NULL_CAT_AWARE_SEARCH_CONF_UID, searchConfiguration.getUid());
	}

	@Test
	public void findCategoryAwareSearchConfigurationWithStagedCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchConfigurationDaoTest.impex", CharEncoding.UTF_8);
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		// when
		final Optional<AbstractAsSearchConfigurationModel> searchConfigurationResult = asSearchConfigurationDao
				.findSearchConfigurationByUid(catalogVersion, CAT_AWARE_SEARCH_CONF_UID);

		// then
		assertTrue(searchConfigurationResult.isPresent());
		final AbstractAsSearchConfigurationModel searchConfiguration = searchConfigurationResult.get();
		assertTrue(searchConfiguration instanceof AsCategoryAwareSearchConfigurationModel);
		assertEquals(CAT_AWARE_SEARCH_CONF_UID, searchConfiguration.getUid());
	}

	@Test
	public void findCategoryAwareSearchConfigurationWithFilters() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchConfigurationDaoTest.impex", CharEncoding.UTF_8);
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		final Optional<AbstractAsSearchProfileModel> searchProfileResult = asSearchProfileService
				.getSearchProfileForCode(catalogVersion, CAT_AWARE_SEARCH_PROF_CODE);

		final Map<String, Object> filters = new HashMap<>();
		filters.put(AsCategoryAwareSearchConfigurationModel.SEARCHPROFILE, searchProfileResult.get());
		filters.put(AsCategoryAwareSearchConfigurationModel.CATEGORY, null);

		// when
		final List<AsCategoryAwareSearchConfigurationModel> searchConfigurations = asSearchConfigurationDao
				.findSearchConfigurations(AsCategoryAwareSearchConfigurationModel.class, filters);

		// then
		assertNotNull(searchConfigurations);
		assertEquals(1, searchConfigurations.size());
		assertEquals(CAT_AWARE_SEARCH_CONF_UID, searchConfigurations.get(0).getUid());
	}

	@Test
	public void cannotFindSearchConfiguration() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchConfigurationDaoTest.impex", CharEncoding.UTF_8);
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		// when
		final Optional<AbstractAsSearchConfigurationModel> searchConfigurationResult = asSearchConfigurationDao
				.findSearchConfigurationByUid(catalogVersion, NOT_FOUND);

		// then
		assertFalse(searchConfigurationResult.isPresent());
	}
}
