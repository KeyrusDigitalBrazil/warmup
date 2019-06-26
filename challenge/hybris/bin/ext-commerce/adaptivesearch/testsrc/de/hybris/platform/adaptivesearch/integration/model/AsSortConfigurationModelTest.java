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
package de.hybris.platform.adaptivesearch.integration.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.model.AbstractAsConfigurableSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsExcludedSortModel;
import de.hybris.platform.adaptivesearch.model.AsPromotedSortModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsSortModel;
import de.hybris.platform.adaptivesearch.services.AsConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchConfigurationService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class AsSortConfigurationModelTest extends ServicelayerTransactionalTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private static final String SIMPLE_SEARCH_CONF_UID = "simpleConfiguration";

	private static final String UID1 = "d3299865-5a12-4985-bcde-0726f302b6f1";
	private static final String UID2 = "381c1991-65d5-4c60-bff5-c0761842d60d";

	private static final String CODE1 = "code1";
	private static final String CODE2 = "code2";
	private static final String CODE3 = "code3";

	private static final String NAME1 = "name1";
	private static final String NAME2 = "name2";
	private static final String NAME3 = "name3";

	private static final String INDEX_PROPERTY1 = "property1";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private AsSearchConfigurationService asSearchConfigurationService;

	@Resource
	private AsConfigurationService asConfigurationService;

	private CatalogVersionModel onlineCatalogVersion;
	private AsSimpleSearchConfigurationModel searchConfiguration;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/model/asSortConfigurationModelTest.impex", CharEncoding.UTF_8);

		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final Optional<AsSimpleSearchConfigurationModel> searchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, SIMPLE_SEARCH_CONF_UID);
		searchConfiguration = searchConfigurationOptional.get();
	}

	@Test
	public void createMultipleSortConfigurations() throws Exception
	{
		// given
		final AsPromotedSortModel promotedSort = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort.setCatalogVersion(onlineCatalogVersion);
		promotedSort.setSearchConfiguration(searchConfiguration);
		promotedSort.setCode(CODE1);
		promotedSort.setName(NAME1);

		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(onlineCatalogVersion);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(CODE2);
		sort.setName(NAME2);

		final AsExcludedSortModel excludedSort = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort.setCatalogVersion(onlineCatalogVersion);
		excludedSort.setSearchConfiguration(searchConfiguration);
		excludedSort.setCode(CODE3);
		excludedSort.setName(NAME3);

		// when
		asConfigurationService.saveConfiguration(promotedSort);
		asConfigurationService.saveConfiguration(sort);
		asConfigurationService.saveConfiguration(excludedSort);

		// then
		assertEquals(onlineCatalogVersion, promotedSort.getCatalogVersion());
		assertNotNull(promotedSort.getUid());
		assertFalse(promotedSort.getUid().isEmpty());

		assertEquals(onlineCatalogVersion, sort.getCatalogVersion());
		assertNotNull(sort.getUid());
		assertFalse(sort.getUid().isEmpty());

		assertEquals(onlineCatalogVersion, excludedSort.getCatalogVersion());
		assertNotNull(excludedSort.getUid());
		assertFalse(excludedSort.getUid().isEmpty());
	}

	@Test
	public void failToCreateMultipleSortConfigurationsWithSameUid1() throws Exception
	{
		// given
		final AsPromotedSortModel promotedSort = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort.setCatalogVersion(onlineCatalogVersion);
		promotedSort.setUid(UID1);
		promotedSort.setSearchConfiguration(searchConfiguration);
		promotedSort.setCode(CODE1);
		promotedSort.setName(NAME1);

		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(onlineCatalogVersion);
		sort.setUid(UID1);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(CODE2);
		sort.setName(NAME2);


		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedSort);
		asConfigurationService.saveConfiguration(sort);
	}

	@Test
	public void failToCreateMultipleSortConfigurationsWithSameUid2() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(onlineCatalogVersion);
		sort.setUid(UID1);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(CODE1);
		sort.setName(NAME1);

		final AsExcludedSortModel excludedSort = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort.setCatalogVersion(onlineCatalogVersion);
		excludedSort.setUid(UID1);
		excludedSort.setSearchConfiguration(searchConfiguration);
		excludedSort.setCode(CODE2);
		excludedSort.setName(NAME2);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(sort);
		asConfigurationService.saveConfiguration(excludedSort);
	}

	@Test
	public void failToCreateMultipleSortConfigurationsWithSameCode1() throws Exception
	{
		// given
		final AsPromotedSortModel promotedSort = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort.setCatalogVersion(onlineCatalogVersion);
		promotedSort.setUid(UID1);
		promotedSort.setSearchConfiguration(searchConfiguration);
		promotedSort.setCode(CODE1);
		promotedSort.setName(NAME1);

		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(onlineCatalogVersion);
		sort.setUid(UID2);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(CODE1);
		sort.setName(NAME2);


		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedSort);
		asConfigurationService.saveConfiguration(sort);
	}

	@Test
	public void failToCreateMultipleSortConfigurationsWithSameCode2() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(onlineCatalogVersion);
		sort.setUid(UID1);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(CODE1);
		sort.setName(NAME1);

		final AsExcludedSortModel excludedSort = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort.setCatalogVersion(onlineCatalogVersion);
		excludedSort.setUid(UID2);
		excludedSort.setSearchConfiguration(searchConfiguration);
		excludedSort.setCode(CODE1);
		excludedSort.setName(NAME3);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(sort);
		asConfigurationService.saveConfiguration(excludedSort);
	}

	@Test
	public void moveSortConfiguration1() throws Exception
	{
		// given
		final AsPromotedSortModel promotedSort = asConfigurationService.createConfiguration(AsPromotedSortModel.class);
		promotedSort.setCatalogVersion(onlineCatalogVersion);
		promotedSort.setUid(UID1);
		promotedSort.setSearchConfiguration(searchConfiguration);
		promotedSort.setCode(CODE1);
		promotedSort.setName(NAME1);

		// when
		asConfigurationService.saveConfiguration(promotedSort);

		modelService.refresh(searchConfiguration);

		final boolean result = asConfigurationService.moveConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDSORTS, AbstractAsConfigurableSearchConfigurationModel.SORTS,
				UID1);

		final Optional<AsPromotedSortModel> promotedSortOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedSortModel.class, onlineCatalogVersion, UID1);
		final Optional<AsSortModel> sortOptional = asConfigurationService.getConfigurationForUid(AsSortModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertTrue(result);
		assertFalse(promotedSortOptional.isPresent());
		assertTrue(sortOptional.isPresent());

		final AsSortModel sort = sortOptional.get();
		assertEquals(onlineCatalogVersion, sort.getCatalogVersion());
		assertEquals(UID1, sort.getUid());
		assertEquals(searchConfiguration, sort.getSearchConfiguration());
		assertEquals(CODE1, sort.getCode());
		assertEquals(NAME1, sort.getName());
	}

	@Test
	public void moveSortConfiguration2() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(onlineCatalogVersion);
		sort.setUid(UID1);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(CODE1);
		sort.setName(NAME1);

		// when
		asConfigurationService.saveConfiguration(sort);

		modelService.refresh(searchConfiguration);

		final boolean result = asConfigurationService.moveConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.SORTS, AbstractAsConfigurableSearchConfigurationModel.EXCLUDEDSORTS,
				UID1);

		final Optional<AsSortModel> sortOptional = asConfigurationService.getConfigurationForUid(AsSortModel.class,
				onlineCatalogVersion, UID1);
		final Optional<AsExcludedSortModel> excludedSortOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedSortModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(result);
		assertFalse(sortOptional.isPresent());
		assertTrue(excludedSortOptional.isPresent());

		final AsExcludedSortModel excludedSort = excludedSortOptional.get();
		assertEquals(onlineCatalogVersion, excludedSort.getCatalogVersion());
		assertEquals(UID1, excludedSort.getUid());
		assertEquals(searchConfiguration, excludedSort.getSearchConfiguration());
		assertEquals(CODE1, excludedSort.getCode());
		assertEquals(NAME1, excludedSort.getName());
	}

	@Test
	public void moveSortConfiguration3() throws Exception
	{
		// given
		final AsExcludedSortModel excludedSort = asConfigurationService.createConfiguration(AsExcludedSortModel.class);
		excludedSort.setCatalogVersion(onlineCatalogVersion);
		excludedSort.setUid(UID1);
		excludedSort.setSearchConfiguration(searchConfiguration);
		excludedSort.setCode(CODE1);
		excludedSort.setName(NAME1);

		// when
		asConfigurationService.saveConfiguration(excludedSort);

		modelService.refresh(searchConfiguration);

		final boolean result = asConfigurationService.moveConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.EXCLUDEDSORTS,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDSORTS, UID1);

		final Optional<AsPromotedSortModel> promotedSortOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedSortModel.class, onlineCatalogVersion, UID1);
		final Optional<AsExcludedSortModel> excludedSortOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedSortModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(result);
		assertTrue(promotedSortOptional.isPresent());
		assertFalse(excludedSortOptional.isPresent());

		final AsPromotedSortModel promotedSort = promotedSortOptional.get();
		assertEquals(onlineCatalogVersion, promotedSort.getCatalogVersion());
		assertEquals(UID1, promotedSort.getUid());
		assertEquals(searchConfiguration, promotedSort.getSearchConfiguration());
		assertEquals(CODE1, promotedSort.getCode());
		assertEquals(NAME1, promotedSort.getName());
	}
}
