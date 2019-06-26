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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.daos.AsSearchProfileActivationSetDao;
import de.hybris.platform.adaptivesearch.model.AsSearchProfileActivationSetModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class DefaultAsSearchProfileActivationSetDaoTest extends ServicelayerTransactionalTest
{
	private static final String INDEX_TYPE = "testIndex";
	private static final String INDEX_TYPE_NO_CATALOG_VERSION = "indexNoCatalogVersion";

	private static final String WRONG_INDEX_TYPE = "wrongIndexType";

	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private AsSearchProfileActivationSetDao asSearchProfileActivationSetDao;

	@Test
	public void findAllEmpty() throws Exception
	{
		// when
		final List<AsSearchProfileActivationSetModel> activationSets = asSearchProfileActivationSetDao
				.findAllSearchProfileActivationSets();

		// then
		assertNotNull(activationSets);
		assertEquals(0, activationSets.size());
	}

	@Test
	public void findAll() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchProfileActivationSetDaoTest.impex", CharEncoding.UTF_8);

		// when
		final List<AsSearchProfileActivationSetModel> activationSets = asSearchProfileActivationSetDao
				.findAllSearchProfileActivationSets();

		// then
		assertNotNull(activationSets);
		assertEquals(2, activationSets.size());
	}

	@Test
	public void findWithNullCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchProfileActivationSetDaoTest.impex", CharEncoding.UTF_8);

		// when
		final Optional<AsSearchProfileActivationSetModel> activationSetResult = asSearchProfileActivationSetDao
				.findSearchProfileActivationSetByIndexType(null, INDEX_TYPE_NO_CATALOG_VERSION);

		// then
		assertTrue(activationSetResult.isPresent());
		final AsSearchProfileActivationSetModel activationSet = activationSetResult.get();
		assertEquals(INDEX_TYPE_NO_CATALOG_VERSION, activationSet.getIndexType());
		assertNull(activationSet.getCatalogVersion());
		assertNotNull(activationSet.getSearchProfiles());
		assertEquals(2, activationSet.getSearchProfiles().size());
	}

	@Test
	public void findWithStagedCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchProfileActivationSetDaoTest.impex", CharEncoding.UTF_8);
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		// when
		final Optional<AsSearchProfileActivationSetModel> activationSetResult = asSearchProfileActivationSetDao
				.findSearchProfileActivationSetByIndexType(catalogVersion, INDEX_TYPE);

		// then
		assertTrue(activationSetResult.isPresent());
		final AsSearchProfileActivationSetModel activationSet = activationSetResult.get();
		assertEquals(INDEX_TYPE, activationSet.getIndexType());
		assertEquals(catalogVersion, activationSet.getCatalogVersion());
		assertNotNull(activationSet.getSearchProfiles());
		assertEquals(2, activationSet.getSearchProfiles().size());
	}

	@Test
	public void cannotFindWithWrongCatalogVersion() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchProfileActivationSetDaoTest.impex", CharEncoding.UTF_8);
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		// when
		final Optional<AsSearchProfileActivationSetModel> activationSetResult = asSearchProfileActivationSetDao
				.findSearchProfileActivationSetByIndexType(catalogVersion, INDEX_TYPE);

		// then
		assertFalse(activationSetResult.isPresent());
	}

	@Test
	public void cannotFindWithWrongIndexType() throws Exception
	{
		// given
		importCsv("/adaptivesearch/test/daos/defaultAsSearchProfileActivationSetDaoTest.impex", CharEncoding.UTF_8);
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		// when
		final Optional<AsSearchProfileActivationSetModel> activationSetResult = asSearchProfileActivationSetDao
				.findSearchProfileActivationSetByIndexType(catalogVersion, WRONG_INDEX_TYPE);

		// then
		assertFalse(activationSetResult.isPresent());
	}
}
