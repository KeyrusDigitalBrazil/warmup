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
import de.hybris.platform.adaptivesearch.model.AsPromotedItemModel;
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
public class AsPromotedItemModelTest extends ServicelayerTransactionalTest
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
	public void getNonExistingPromotedItem() throws Exception
	{
		// when
		final Optional<AsPromotedItemModel> promotedItemOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedItemModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(promotedItemOptional.isPresent());
	}

	@Test
	public void createPromotedItemWithoutUid() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(onlineCatalogVersion);
		promotedItem.setSearchConfiguration(searchConfiguration);
		promotedItem.setItem(onlineCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(promotedItem);

		// then
		assertNotNull(promotedItem.getUid());
		assertFalse(promotedItem.getUid().isEmpty());
	}

	@Test
	public void createPromotedItem() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(onlineCatalogVersion);
		promotedItem.setUid(UID1);
		promotedItem.setSearchConfiguration(searchConfiguration);
		promotedItem.setItem(onlineCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(promotedItem);

		final Optional<AsPromotedItemModel> createdPromotedItemOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedItemModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(createdPromotedItemOptional.isPresent());

		final AsPromotedItemModel createdPromotedItem = createdPromotedItemOptional.get();
		assertEquals(onlineCatalogVersion, createdPromotedItem.getCatalogVersion());
		assertEquals(UID1, createdPromotedItem.getUid());
		assertEquals(searchConfiguration, createdPromotedItem.getSearchConfiguration());
		assertEquals(onlineCatalogVersion, createdPromotedItem.getItem());
	}

	@Test
	public void failToCreatePromotedItemWithWrongCatalogVersion() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(stagedCatalogVersion);
		promotedItem.setSearchConfiguration(searchConfiguration);
		promotedItem.setItem(onlineCatalogVersion);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedItem);
	}

	@Test
	public void failToCreatePromotedItemWithoutSearchConfiguration() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(onlineCatalogVersion);
		promotedItem.setItem(onlineCatalogVersion);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedItem);
	}

	@Test
	public void failToCreatePromotedItemWithoutItem() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(onlineCatalogVersion);
		promotedItem.setSearchConfiguration(searchConfiguration);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedItem);
	}

	@Test
	public void createMultiplePromotedItems() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem1 = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem1.setCatalogVersion(onlineCatalogVersion);
		promotedItem1.setUid(UID1);
		promotedItem1.setSearchConfiguration(searchConfiguration);
		promotedItem1.setItem(stagedCatalogVersion);

		final AsPromotedItemModel promotedItem2 = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem2.setCatalogVersion(onlineCatalogVersion);
		promotedItem2.setUid(UID2);
		promotedItem2.setSearchConfiguration(searchConfiguration);
		promotedItem2.setItem(onlineCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(promotedItem1);
		asConfigurationService.saveConfiguration(promotedItem2);

		modelService.refresh(searchConfiguration);

		// then
		assertThat(searchConfiguration.getPromotedItems()).containsExactly(promotedItem1, promotedItem2);
	}


	@Test
	public void failToUpdatePromotedItemItem() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(onlineCatalogVersion);
		promotedItem.setUid(UID1);
		promotedItem.setSearchConfiguration(searchConfiguration);
		promotedItem.setItem(onlineCatalogVersion);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedItem);

		final Optional<AsPromotedItemModel> createdPromotedItemOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedItemModel.class, onlineCatalogVersion, UID1);

		final AsPromotedItemModel createdPromotedItem = createdPromotedItemOptional.get();
		createdPromotedItem.setItem(stagedCatalogVersion);
		asConfigurationService.saveConfiguration(createdPromotedItem);
	}

	@Test
	public void clonePromotedItem() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(onlineCatalogVersion);
		promotedItem.setUid(UID1);
		promotedItem.setSearchConfiguration(searchConfiguration);
		promotedItem.setItem(onlineCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(promotedItem);

		final AsPromotedItemModel clonedPromotedItem = asConfigurationService.cloneConfiguration(promotedItem);
		clonedPromotedItem.setItem(stagedCatalogVersion);
		asConfigurationService.saveConfiguration(clonedPromotedItem);

		// then
		assertNotSame(promotedItem, clonedPromotedItem);

		assertEquals(promotedItem.getCatalogVersion(), clonedPromotedItem.getCatalogVersion());
		assertNotEquals(promotedItem.getUid(), clonedPromotedItem.getUid());
		assertEquals(promotedItem.getSearchConfiguration(), clonedPromotedItem.getSearchConfiguration());
	}

	@Test
	public void removePromotedItem() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(onlineCatalogVersion);
		promotedItem.setUid(UID1);
		promotedItem.setSearchConfiguration(searchConfiguration);
		promotedItem.setItem(onlineCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(promotedItem);

		final Optional<AsPromotedItemModel> createdPromotedItemOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedItemModel.class, onlineCatalogVersion, UID1);

		final AsPromotedItemModel createdPromotedItem = createdPromotedItemOptional.get();
		asConfigurationService.removeConfiguration(createdPromotedItem);

		final Optional<AsPromotedItemModel> removedPromotedItemOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedItemModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(removedPromotedItemOptional.isPresent());
	}

	@Test
	public void promotedItemIsNotCorrupted() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(onlineCatalogVersion);
		promotedItem.setUid(UID1);
		promotedItem.setSearchConfiguration(searchConfiguration);
		promotedItem.setItem(onlineCatalogVersion);

		// when
		final boolean corrupted = promotedItem.isCorrupted();

		// then
		assertFalse(corrupted);
	}

	@Test
	public void promotedItemIsCorrupted() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(onlineCatalogVersion);
		promotedItem.setUid(UID1);
		promotedItem.setSearchConfiguration(searchConfiguration);
		promotedItem.setItem(onlineCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(promotedItem);
		promotedItem.setItem(null);

		final boolean corrupted = promotedItem.isCorrupted();

		// then
		assertTrue(corrupted);
	}

	@Test
	public void rankAfterPromotedItem() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem1 = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem1.setCatalogVersion(onlineCatalogVersion);
		promotedItem1.setUid(UID1);
		promotedItem1.setSearchConfiguration(searchConfiguration);
		promotedItem1.setItem(stagedCatalogVersion);

		final AsPromotedItemModel promotedItem2 = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem2.setCatalogVersion(onlineCatalogVersion);
		promotedItem2.setUid(UID2);
		promotedItem2.setSearchConfiguration(searchConfiguration);
		promotedItem2.setItem(onlineCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(promotedItem1);
		asConfigurationService.saveConfiguration(promotedItem2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, UID2, UID1);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID1, rankChange.getUid());
		assertEquals(Integer.valueOf(0), rankChange.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).containsExactly(promotedItem2, promotedItem1);
	}

	@Test
	public void rankBeforePromotedItem() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem1 = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem1.setCatalogVersion(onlineCatalogVersion);
		promotedItem1.setUid(UID1);
		promotedItem1.setSearchConfiguration(searchConfiguration);
		promotedItem1.setItem(stagedCatalogVersion);

		final AsPromotedItemModel promotedItem2 = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem2.setCatalogVersion(onlineCatalogVersion);
		promotedItem2.setUid(UID2);
		promotedItem2.setSearchConfiguration(searchConfiguration);
		promotedItem2.setItem(onlineCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(promotedItem1);
		asConfigurationService.saveConfiguration(promotedItem2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, UID1, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(0), rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).containsExactly(promotedItem2, promotedItem1);
	}
}
