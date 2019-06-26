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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.data.AsRankChange;
import de.hybris.platform.adaptivesearch.data.AsRankChangeType;
import de.hybris.platform.adaptivesearch.model.AbstractAsConfigurableSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsExcludedItemModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.services.AsConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchConfigurationService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class AsExcludedItemModelTest extends ServicelayerTransactionalTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private static final String SIMPLE_SEARCH_CONF_UID = "simpleConfiguration";

	private static final String UID1 = "e81de964-b6b8-4031-bf1a-2eeb99b606ac";
	private static final String UID2 = "e3780f3f-5e60-4174-b85d-52c84b34ee38";

	@Resource
	private ModelService modelService;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private AsSearchConfigurationService asSearchConfigurationService;

	@Resource
	private AsConfigurationService asConfigurationService;

	private CatalogVersionModel onlineCatalogVersion;
	private CatalogVersionModel stagedCatalogVersion;
	private AsSimpleSearchConfigurationModel searchConfiguration;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/model/asBoostItemConfigurationModelTest.impex", CharEncoding.UTF_8);

		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		final Optional<AsSimpleSearchConfigurationModel> searchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, SIMPLE_SEARCH_CONF_UID);
		searchConfiguration = searchConfigurationOptional.get();
	}

	@Test
	public void getNonExistingExcludedItem() throws Exception
	{
		// when
		final Optional<AsExcludedItemModel> excludedItemOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedItemModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(excludedItemOptional.isPresent());
	}

	@Test
	public void createExcludedItemWithoutUid() throws Exception
	{
		// given
		final AsExcludedItemModel excludedItem = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem.setCatalogVersion(onlineCatalogVersion);
		excludedItem.setSearchConfiguration(searchConfiguration);
		excludedItem.setItem(onlineCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(excludedItem);

		// then
		assertNotNull(excludedItem.getUid());
		assertFalse(excludedItem.getUid().isEmpty());
	}

	@Test
	public void createExcludedItem() throws Exception
	{
		// given
		final AsExcludedItemModel excludedItem = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem.setCatalogVersion(onlineCatalogVersion);
		excludedItem.setUid(UID1);
		excludedItem.setSearchConfiguration(searchConfiguration);
		excludedItem.setItem(onlineCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(excludedItem);

		final Optional<AsExcludedItemModel> createdExcludedItemOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedItemModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(createdExcludedItemOptional.isPresent());

		final AsExcludedItemModel createdExcludedItem = createdExcludedItemOptional.get();
		assertEquals(onlineCatalogVersion, createdExcludedItem.getCatalogVersion());
		assertEquals(UID1, createdExcludedItem.getUid());
		assertEquals(searchConfiguration, createdExcludedItem.getSearchConfiguration());
		assertEquals(onlineCatalogVersion, createdExcludedItem.getItem());
	}

	@Test
	public void failToCreateExcludedItemWithWrongCatalogVersion() throws Exception
	{
		// given
		final AsExcludedItemModel excludedItem = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem.setCatalogVersion(stagedCatalogVersion);
		excludedItem.setSearchConfiguration(searchConfiguration);
		excludedItem.setItem(onlineCatalogVersion);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedItem);
	}

	@Test
	public void failToCreateExcludedItemWithoutSearchConfiguration() throws Exception
	{
		// given
		final AsExcludedItemModel excludedItem = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem.setCatalogVersion(onlineCatalogVersion);
		excludedItem.setItem(onlineCatalogVersion);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedItem);
	}

	@Test
	public void failToCreateExcludedItemWithoutItem() throws Exception
	{
		// given
		final AsExcludedItemModel excludedItem = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem.setCatalogVersion(onlineCatalogVersion);
		excludedItem.setSearchConfiguration(searchConfiguration);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedItem);
	}

	@Test
	public void createMultipleExcludedItems() throws Exception
	{
		// given
		final AsExcludedItemModel excludedItem1 = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem1.setCatalogVersion(onlineCatalogVersion);
		excludedItem1.setUid(UID1);
		excludedItem1.setSearchConfiguration(searchConfiguration);
		excludedItem1.setItem(stagedCatalogVersion);

		final AsExcludedItemModel excludedItem2 = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem2.setCatalogVersion(onlineCatalogVersion);
		excludedItem2.setUid(UID2);
		excludedItem2.setSearchConfiguration(searchConfiguration);
		excludedItem2.setItem(onlineCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(excludedItem1);
		asConfigurationService.saveConfiguration(excludedItem2);

		modelService.refresh(searchConfiguration);

		// then
		assertThat(searchConfiguration.getExcludedItems()).containsExactly(excludedItem1, excludedItem2);
	}

	@Test
	public void failToUpdateExcludedItemItem() throws Exception
	{
		// given
		final AsExcludedItemModel excludedItem = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem.setCatalogVersion(onlineCatalogVersion);
		excludedItem.setUid(UID1);
		excludedItem.setSearchConfiguration(searchConfiguration);
		excludedItem.setItem(onlineCatalogVersion);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedItem);

		final Optional<AsExcludedItemModel> createdExcludedItemOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedItemModel.class, onlineCatalogVersion, UID1);

		final AsExcludedItemModel createdExcludedItem = createdExcludedItemOptional.get();
		createdExcludedItem.setItem(stagedCatalogVersion);
		asConfigurationService.saveConfiguration(createdExcludedItem);
	}

	@Test
	public void cloneExcludedItem() throws Exception
	{
		// given
		final AsExcludedItemModel excludedItem = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem.setCatalogVersion(onlineCatalogVersion);
		excludedItem.setUid(UID1);
		excludedItem.setSearchConfiguration(searchConfiguration);
		excludedItem.setItem(onlineCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(excludedItem);

		final AsExcludedItemModel clonedExcludedItem = asConfigurationService.cloneConfiguration(excludedItem);
		clonedExcludedItem.setItem(stagedCatalogVersion);
		asConfigurationService.saveConfiguration(clonedExcludedItem);

		// then
		assertNotSame(excludedItem, clonedExcludedItem);

		assertEquals(excludedItem.getCatalogVersion(), clonedExcludedItem.getCatalogVersion());
		assertNotEquals(excludedItem.getUid(), clonedExcludedItem.getUid());
		assertEquals(excludedItem.getSearchConfiguration(), clonedExcludedItem.getSearchConfiguration());
	}

	@Test
	public void removeExcludedItem() throws Exception
	{
		// given
		final AsExcludedItemModel excludedItem = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem.setCatalogVersion(onlineCatalogVersion);
		excludedItem.setUid(UID1);
		excludedItem.setSearchConfiguration(searchConfiguration);
		excludedItem.setItem(onlineCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(excludedItem);

		final Optional<AsExcludedItemModel> createdExcludedItemOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedItemModel.class, onlineCatalogVersion, UID1);

		final AsExcludedItemModel createdExcludedItem = createdExcludedItemOptional.get();
		asConfigurationService.removeConfiguration(createdExcludedItem);

		final Optional<AsExcludedItemModel> removedExcludedItemOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedItemModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(removedExcludedItemOptional.isPresent());
	}

	@Test
	public void excludedItemIsNotCorrupted() throws Exception
	{
		// given
		final AsExcludedItemModel excludedItem = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem.setCatalogVersion(onlineCatalogVersion);
		excludedItem.setUid(UID1);
		excludedItem.setSearchConfiguration(searchConfiguration);
		excludedItem.setItem(onlineCatalogVersion);

		// when
		final boolean corrupted = excludedItem.isCorrupted();

		// then
		assertFalse(corrupted);
	}

	@Test
	public void excludedItemIsCorrupted() throws Exception
	{
		// given
		final AsExcludedItemModel excludedItem = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem.setCatalogVersion(onlineCatalogVersion);
		excludedItem.setUid(UID1);
		excludedItem.setSearchConfiguration(searchConfiguration);
		excludedItem.setItem(onlineCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(excludedItem);
		excludedItem.setItem(null);

		final boolean corrupted = excludedItem.isCorrupted();

		// then
		assertTrue(corrupted);
	}

	@Test
	public void rankAfterExcludedItem() throws Exception
	{
		// given
		final AsExcludedItemModel excludedItem1 = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem1.setCatalogVersion(onlineCatalogVersion);
		excludedItem1.setUid(UID1);
		excludedItem1.setSearchConfiguration(searchConfiguration);
		excludedItem1.setItem(stagedCatalogVersion);

		final AsExcludedItemModel excludedItem2 = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem2.setCatalogVersion(onlineCatalogVersion);
		excludedItem2.setUid(UID2);
		excludedItem2.setSearchConfiguration(searchConfiguration);
		excludedItem2.setItem(onlineCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(excludedItem1);
		asConfigurationService.saveConfiguration(excludedItem2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.EXCLUDEDITEMS, UID2, UID1);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID1, rankChange.getUid());
		assertEquals(Integer.valueOf(0), rankChange.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange.getNewRank());

		assertThat(searchConfiguration.getExcludedItems()).containsExactly(excludedItem2, excludedItem1);
	}

	@Test
	public void rankBeforeExcludedItem() throws Exception
	{
		// given
		final AsExcludedItemModel excludedItem1 = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem1.setCatalogVersion(onlineCatalogVersion);
		excludedItem1.setUid(UID1);
		excludedItem1.setSearchConfiguration(searchConfiguration);
		excludedItem1.setItem(stagedCatalogVersion);

		final AsExcludedItemModel excludedItem2 = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem2.setCatalogVersion(onlineCatalogVersion);
		excludedItem2.setUid(UID2);
		excludedItem2.setSearchConfiguration(searchConfiguration);
		excludedItem2.setItem(onlineCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(excludedItem1);
		asConfigurationService.saveConfiguration(excludedItem2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.EXCLUDEDITEMS, UID1, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(0), rankChange.getNewRank());

		assertThat(searchConfiguration.getExcludedItems()).containsExactly(excludedItem2, excludedItem1);
	}
}
