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
import de.hybris.platform.adaptivesearch.daos.AsSearchProfileDao;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AsCategoryAwareSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchProfileModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class DefaultAsSearchProfileDaoTest extends ServicelayerTransactionalTest
{
	private static final String NOT_FOUND = "NotFound";

	private static final String INDEX_TYPE_1 = "testIndex1";
	private static final String INDEX_TYPE_2 = "testIndex2";

	private static final String SIMPLE_SEARCH_PROFILE_CODE = "simpleProfile";
	private static final String SIMPLE_SEARCH_PROFILE_NAME = "Simple search profile";

	private static final String NULL_SIMPLE_SEARCH_PROFILE_CODE = "nullSimpleProfile";
	private static final String NULL_SIMPLE_SEARCH_PROFILE_NAME = "Null simple search profile";

	private static final String CAT_AWARE_SEARCH_PROFILE_CODE = "categoryAwareProfile";
	private static final String CAT_AWARE_SEARCH_PROFILE_NAME = "Category aware search profile";

	private static final String NULL_CAT_AWARE_SEARCH_PROFILE_CODE = "nullCategoryAwareProfile";
	private static final String NULL_CAT_AWARE_SEARCH_PROFILE_NAME = "Null category aware search profile";

	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";
	private final static String VERSION_TEST = "Test";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private AsSearchProfileDao asSearchProfileDao;

	@Test
	public void findAllEmpty() throws Exception
	{
		// when
		final List<AbstractAsSearchProfileModel> searchProfiles = asSearchProfileDao.findAllSearchProfiles();

		// then
		assertNotNull(searchProfiles);
		assertEquals(0, searchProfiles.size());
	}

	@Test
	public void findAll() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchProfileDaoTest.impex", CharEncoding.UTF_8);

		// when
		final List<AbstractAsSearchProfileModel> searchProfiles = asSearchProfileDao.findAllSearchProfiles();

		// then
		assertNotNull(searchProfiles);
		assertEquals(6, searchProfiles.size());
	}

	@Test
	public void findByEmptyIndexTypeAndCatalogVersion() throws Exception
	{
		// when
		final List<AbstractAsSearchProfileModel> searchProfiles = asSearchProfileDao
				.findSearchProfilesByIndexTypesAndCatalogVersions(null, null);

		// then
		assertNotNull(searchProfiles);
		assertEquals(0, searchProfiles.size());
	}

	@Test
	public void findByNullIndexTypeAndCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchProfileDaoTest.impex", CharEncoding.UTF_8);

		// when
		final List<AbstractAsSearchProfileModel> searchProfiles = asSearchProfileDao
				.findSearchProfilesByIndexTypesAndCatalogVersions(null, null);

		// then
		assertNotNull(searchProfiles);
		assertEquals(6, searchProfiles.size());
	}

	@Test
	public void findByIndexTypeAndCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchProfileDaoTest.impex", CharEncoding.UTF_8);
		final CatalogVersionModel catalogVersionStaged = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		// when
		final List<AbstractAsSearchProfileModel> searchProfiles = asSearchProfileDao
				.findSearchProfilesByIndexTypesAndCatalogVersions(Collections.singletonList(INDEX_TYPE_1),
						Collections.singletonList(catalogVersionStaged));

		// then
		assertNotNull(searchProfiles);
		assertEquals(2, searchProfiles.size());
	}

	@Test
	public void findByIndexTypesAndCatalogVersions() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchProfileDaoTest.impex", CharEncoding.UTF_8);
		final CatalogVersionModel catalogVersionStaged = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		final CatalogVersionModel catalogVersionOnline = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		// when
		final List<AbstractAsSearchProfileModel> searchProfiles = asSearchProfileDao
				.findSearchProfilesByIndexTypesAndCatalogVersions(Arrays.asList(INDEX_TYPE_1, INDEX_TYPE_2),
						Arrays.asList(catalogVersionStaged, catalogVersionOnline));

		// then
		assertNotNull(searchProfiles);
		assertEquals(4, searchProfiles.size());
	}

	@Test
	public void findByNullCatalogVersionEmpty() throws Exception
	{
		// when
		final List<AbstractAsSearchProfileModel> searchProfiles = asSearchProfileDao.findSearchProfilesByCatalogVersion(null);

		// then
		assertNotNull(searchProfiles);
		assertEquals(0, searchProfiles.size());
	}

	@Test
	public void findByNullCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchProfileDaoTest.impex", CharEncoding.UTF_8);

		// when
		final List<AbstractAsSearchProfileModel> searchProfiles = asSearchProfileDao.findSearchProfilesByCatalogVersion(null);

		// then
		assertNotNull(searchProfiles);
		assertEquals(2, searchProfiles.size());
	}

	@Test
	public void findByStagedCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchProfileDaoTest.impex", CharEncoding.UTF_8);
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		// when
		final List<AbstractAsSearchProfileModel> searchProfiles = asSearchProfileDao
				.findSearchProfilesByCatalogVersion(catalogVersion);

		// then
		assertNotNull(searchProfiles);
		assertEquals(2, searchProfiles.size());
	}

	@Test
	public void findByOnlineCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchProfileDaoTest.impex", CharEncoding.UTF_8);
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		// when
		final List<AbstractAsSearchProfileModel> searchProfiles = asSearchProfileDao
				.findSearchProfilesByCatalogVersion(catalogVersion);

		// then
		assertNotNull(searchProfiles);
		assertEquals(2, searchProfiles.size());
	}

	@Test
	public void cannotFindByTestCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchProfileDaoTest.impex", CharEncoding.UTF_8);
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_TEST);

		// when
		final List<AbstractAsSearchProfileModel> searchProfiles = asSearchProfileDao
				.findSearchProfilesByCatalogVersion(catalogVersion);

		// then
		assertNotNull(searchProfiles);
		assertEquals(0, searchProfiles.size());
	}

	@Test
	public void findSimpleSearchProfileWithNullCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchProfileDaoTest.impex", CharEncoding.UTF_8);

		// when
		final Optional<AbstractAsSearchProfileModel> searchProfileResult = asSearchProfileDao.findSearchProfileByCode(null,
				NULL_SIMPLE_SEARCH_PROFILE_CODE);

		// then
		assertTrue(searchProfileResult.isPresent());
		final AbstractAsSearchProfileModel searchProfile = searchProfileResult.get();
		assertTrue(searchProfile instanceof AsSimpleSearchProfileModel);
		assertEquals(NULL_SIMPLE_SEARCH_PROFILE_NAME, searchProfile.getName());
		assertEquals(INDEX_TYPE_2, searchProfile.getIndexType());
	}

	@Test
	public void findSimpleSearchProfileWithStagedCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchProfileDaoTest.impex", CharEncoding.UTF_8);
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		// when
		final Optional<AbstractAsSearchProfileModel> searchProfileResult = asSearchProfileDao
				.findSearchProfileByCode(catalogVersion, SIMPLE_SEARCH_PROFILE_CODE);

		// then
		assertTrue(searchProfileResult.isPresent());
		final AbstractAsSearchProfileModel searchProfile = searchProfileResult.get();
		assertTrue(searchProfile instanceof AsSimpleSearchProfileModel);
		assertEquals(SIMPLE_SEARCH_PROFILE_NAME, searchProfile.getName());
		assertEquals(INDEX_TYPE_1, searchProfile.getIndexType());
	}

	@Test
	public void findCategoryAwareSearchProfileWithNullCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchProfileDaoTest.impex", CharEncoding.UTF_8);

		// when
		final Optional<AbstractAsSearchProfileModel> searchProfileResult = asSearchProfileDao.findSearchProfileByCode(null,
				NULL_CAT_AWARE_SEARCH_PROFILE_CODE);

		// then
		assertTrue(searchProfileResult.isPresent());
		final AbstractAsSearchProfileModel searchProfile = searchProfileResult.get();
		assertTrue(searchProfile instanceof AsCategoryAwareSearchProfileModel);
		assertEquals(NULL_CAT_AWARE_SEARCH_PROFILE_NAME, searchProfile.getName());
		assertEquals(INDEX_TYPE_2, searchProfile.getIndexType());
	}

	@Test
	public void findCategoryAwareSearchProfileWithStagedCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchProfileDaoTest.impex", CharEncoding.UTF_8);
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		// when
		final Optional<AbstractAsSearchProfileModel> searchProfileResult = asSearchProfileDao
				.findSearchProfileByCode(catalogVersion, CAT_AWARE_SEARCH_PROFILE_CODE);

		// then
		assertTrue(searchProfileResult.isPresent());
		final AbstractAsSearchProfileModel searchProfile = searchProfileResult.get();
		assertTrue(searchProfile instanceof AsCategoryAwareSearchProfileModel);
		assertEquals(CAT_AWARE_SEARCH_PROFILE_NAME, searchProfile.getName());
		assertEquals(INDEX_TYPE_1, searchProfile.getIndexType());
	}

	@Test
	public void cannotFindSearchProfile() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchProfileDaoTest.impex", CharEncoding.UTF_8);
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		// when
		final Optional<AbstractAsSearchProfileModel> searchProfileResult = asSearchProfileDao
				.findSearchProfileByCode(catalogVersion, NOT_FOUND);

		// then
		assertFalse(searchProfileResult.isPresent());
	}
}