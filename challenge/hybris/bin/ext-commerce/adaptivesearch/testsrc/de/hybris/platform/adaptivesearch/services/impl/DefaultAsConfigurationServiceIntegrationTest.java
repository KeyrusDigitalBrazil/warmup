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
package de.hybris.platform.adaptivesearch.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.data.AsRankChange;
import de.hybris.platform.adaptivesearch.data.AsRankChangeType;
import de.hybris.platform.adaptivesearch.model.AbstractAsConfigurableSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsExcludedItemModel;
import de.hybris.platform.adaptivesearch.model.AsPromotedItemModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.services.AsConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchConfigurationService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
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
public class DefaultAsConfigurationServiceIntegrationTest extends ServicelayerTransactionalTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private static final String SIMPLE_SEARCH_CONF_UID = "simpleConfiguration";

	private static final String UID1 = "6e8b9a1b-38ee-4fb1-b34a-b5e7dba5c3ab";
	private static final String UID2 = "478cad9e-5c04-45ce-80b7-73fdf93b5ab0";
	private static final String UID3 = "ffb42acb-5a06-4954-808a-9bf669c42d26";

	private static final String NON_EXISTING_ATTRIBUTE = "abc";
	private static final String NON_EXISTING_UID = "123";

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
		importCsv("/adaptivesearch/test/services/defaultAsConfigurationServiceTest.impex", CharEncoding.UTF_8);

		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		final Optional<AsSimpleSearchConfigurationModel> searchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, SIMPLE_SEARCH_CONF_UID);
		searchConfiguration = searchConfigurationOptional.get();
	}

	@Test
	public void moveConfigurationShouldAddToLastPosition() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(onlineCatalogVersion);
		promotedItem.setUid(UID1);
		promotedItem.setSearchConfiguration(searchConfiguration);
		promotedItem.setItem(onlineCatalogVersion);

		final AsExcludedItemModel excludedItem = asConfigurationService.createConfiguration(AsExcludedItemModel.class);
		excludedItem.setCatalogVersion(onlineCatalogVersion);
		excludedItem.setUid(UID2);
		excludedItem.setSearchConfiguration(searchConfiguration);
		excludedItem.setItem(stagedCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(promotedItem);
		asConfigurationService.saveConfiguration(excludedItem);

		modelService.refresh(searchConfiguration);

		final boolean result = asConfigurationService.moveConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS,
				AbstractAsConfigurableSearchConfigurationModel.EXCLUDEDITEMS, UID1);

		// then
		assertTrue(result);
		assertThat(searchConfiguration.getPromotedItems()).isEmpty();
		assertThat(searchConfiguration.getExcludedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID2, UID1);
	}

	@Test
	public void failToMoveConfigurationForNonExistingUid() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(onlineCatalogVersion);
		promotedItem.setUid(UID1);
		promotedItem.setSearchConfiguration(searchConfiguration);
		promotedItem.setItem(onlineCatalogVersion);

		// when
		asConfigurationService.saveConfiguration(promotedItem);

		modelService.refresh(searchConfiguration);

		final boolean result = asConfigurationService.moveConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS,
				AbstractAsConfigurableSearchConfigurationModel.EXCLUDEDITEMS, NON_EXISTING_UID);

		// then
		assertFalse(result);
		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID1);
		assertThat(searchConfiguration.getExcludedItems()).isEmpty();
	}

	@Test
	public void failToMoveConfigurationForNonExistingSourceAttribute() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(onlineCatalogVersion);
		promotedItem.setUid(UID1);
		promotedItem.setSearchConfiguration(searchConfiguration);
		promotedItem.setItem(onlineCatalogVersion);

		// expect
		expectedException.expect(AttributeNotSupportedException.class);

		// when
		asConfigurationService.saveConfiguration(promotedItem);

		modelService.refresh(searchConfiguration);

		asConfigurationService.moveConfiguration(searchConfiguration, NON_EXISTING_ATTRIBUTE,
				AbstractAsConfigurableSearchConfigurationModel.EXCLUDEDITEMS, UID1);
	}

	@Test
	public void failToMoveConfigurationForNonExistingTargetAttribute() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(onlineCatalogVersion);
		promotedItem.setUid(UID1);
		promotedItem.setSearchConfiguration(searchConfiguration);
		promotedItem.setItem(onlineCatalogVersion);

		// expect
		expectedException.expect(AttributeNotSupportedException.class);

		// when
		asConfigurationService.saveConfiguration(promotedItem);

		modelService.refresh(searchConfiguration);

		asConfigurationService.moveConfiguration(searchConfiguration, AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS,
				NON_EXISTING_ATTRIBUTE, UID1);
	}

	@Test
	public void failToMoveConfigurationForNonCompatibleSourceAttribute() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(onlineCatalogVersion);
		promotedItem.setUid(UID1);
		promotedItem.setSearchConfiguration(searchConfiguration);
		promotedItem.setItem(onlineCatalogVersion);

		// expect
		expectedException.expect(AttributeNotSupportedException.class);

		// when
		asConfigurationService.saveConfiguration(promotedItem);

		modelService.refresh(searchConfiguration);

		asConfigurationService.moveConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.BOOSTITEMSMERGEMODE,
				AbstractAsConfigurableSearchConfigurationModel.EXCLUDEDITEMS, UID1);
	}

	@Test
	public void failToMoveConfigurationForNonCompatibleTargetAttribute() throws Exception
	{
		// given
		final AsPromotedItemModel promotedItem = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem.setCatalogVersion(onlineCatalogVersion);
		promotedItem.setUid(UID1);
		promotedItem.setSearchConfiguration(searchConfiguration);
		promotedItem.setItem(onlineCatalogVersion);

		// expect
		expectedException.expect(AttributeNotSupportedException.class);

		// when
		asConfigurationService.saveConfiguration(promotedItem);

		modelService.refresh(searchConfiguration);

		asConfigurationService.moveConfiguration(searchConfiguration, AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS,
				AbstractAsConfigurableSearchConfigurationModel.BOOSTITEMSMERGEMODE, UID1);
	}

	@Test
	public void rankBeforeConfigurationToMidlePosition()
	{
		// given
		createConfigurations();

		// when
		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, UID2, UID3);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID3, rankChange.getUid());
		assertEquals(Integer.valueOf(2), rankChange.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID1, UID3,
				UID2);
	}

	@Test
	public void rankBeforeConfigurationToSamePosition()
	{
		// given
		createConfigurations();

		// when
		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, UID2, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID1, UID2,
				UID3);
	}

	@Test
	public void rankBeforeConfigurationToFirstPosition()
	{
		// given
		createConfigurations();

		// when
		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, UID1, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(0), rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID2, UID1,
				UID3);
	}

	@Test
	public void rankBeforeConfigurationToLastPosition()
	{
		// given
		createConfigurations();

		// when
		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, null, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(2), rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID1, UID3,
				UID2);
	}

	@Test
	public void rankBeforeConfigurationWithEmptyUids()
	{
		// given
		createConfigurations();

		// when
		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, UID2, new String[0]);

		// then
		assertThat(rankChanges).isEmpty();

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID1, UID2,
				UID3);
	}

	@Test
	public void rankBeforeConfigurationWithMultipleUids()
	{
		// given
		createConfigurations();

		// when
		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, UID2, UID3, UID1);

		// then
		assertThat(rankChanges).hasSize(2);

		final AsRankChange rankChange1 = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange1.getType());
		assertEquals(UID3, rankChange1.getUid());
		assertEquals(Integer.valueOf(2), rankChange1.getOldRank());
		assertEquals(Integer.valueOf(0), rankChange1.getNewRank());

		final AsRankChange rankChange2 = rankChanges.get(1);
		assertEquals(AsRankChangeType.MOVE, rankChange2.getType());
		assertEquals(UID1, rankChange2.getUid());
		assertEquals(Integer.valueOf(0), rankChange2.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange2.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID3, UID1,
				UID2);
	}

	@Test
	public void failToRankBeforeConfigurationForNonExistingAttribute()
	{
		// given
		createConfigurations();

		// expect
		expectedException.expect(AttributeNotSupportedException.class);

		// when
		asConfigurationService.rankBeforeConfiguration(searchConfiguration, NON_EXISTING_ATTRIBUTE, UID1, UID2);
	}

	@Test
	public void failToRankBeforeConfigurationForNonCompatibleAttribute()
	{
		// given
		createConfigurations();

		// expect
		expectedException.expect(AttributeNotSupportedException.class);

		// when
		asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.BOOSTITEMSMERGEMODE, UID1, UID2);
	}

	@Test
	public void failToRankBeforeConfigurationForNonExistingRankBeforeUid()
	{
		// given
		createConfigurations();

		// when
		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, NON_EXISTING_UID, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.NO_OPERATION, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertNull(rankChange.getOldRank());
		assertNull(rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID1, UID2,
				UID3);
	}

	@Test
	public void failToRankBeforeConfigurationForNonExistingUid()
	{
		// given
		createConfigurations();

		// when
		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, UID2, NON_EXISTING_UID);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.NO_OPERATION, rankChange.getType());
		assertEquals(NON_EXISTING_UID, rankChange.getUid());
		assertNull(rankChange.getOldRank());
		assertNull(rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID1, UID2,
				UID3);
	}

	@Test
	public void rankAfterConfigurationToMidlePosition()
	{
		// given
		createConfigurations();

		// when
		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, UID2, UID1);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID1, rankChange.getUid());
		assertEquals(Integer.valueOf(0), rankChange.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID2, UID1,
				UID3);
	}

	@Test
	public void rankAfterConfigurationToSamePosition()
	{
		// given
		createConfigurations();

		// when
		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, UID2, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID1, UID2,
				UID3);
	}

	@Test
	public void rankAfterConfigurationToFirstPosition()
	{
		// given
		createConfigurations();

		// when
		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, null, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(0), rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID2, UID1,
				UID3);
	}

	@Test
	public void rankAfterConfigurationToLastPosition()
	{
		// given
		createConfigurations();

		// when
		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, UID3, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(2), rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID1, UID3,
				UID2);
	}

	@Test
	public void rankAfterConfigurationWithEmptyUids()
	{
		// given
		createConfigurations();

		// when
		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, UID2, new String[0]);

		// then
		assertThat(rankChanges).isEmpty();

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID1, UID2,
				UID3);
	}

	@Test
	public void rankAfterConfigurationWithMultipleUids()
	{
		// given
		createConfigurations();

		// when
		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, UID2, UID3, UID1);

		// then
		assertThat(rankChanges).hasSize(2);

		final AsRankChange rankChange1 = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange1.getType());
		assertEquals(UID3, rankChange1.getUid());
		assertEquals(Integer.valueOf(2), rankChange1.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange1.getNewRank());

		final AsRankChange rankChange2 = rankChanges.get(1);
		assertEquals(AsRankChangeType.MOVE, rankChange2.getType());
		assertEquals(UID1, rankChange2.getUid());
		assertEquals(Integer.valueOf(0), rankChange2.getOldRank());
		assertEquals(Integer.valueOf(2), rankChange2.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID2, UID3,
				UID1);
	}

	@Test
	public void failToRankAfterConfigurationForNonExistingAttribute()
	{
		// given
		createConfigurations();

		// expect
		expectedException.expect(AttributeNotSupportedException.class);

		// when
		asConfigurationService.rankAfterConfiguration(searchConfiguration, NON_EXISTING_ATTRIBUTE, UID1, UID2);
	}

	@Test
	public void failToRankAfterConfigurationForNonCompatibleAttribute()
	{
		// given
		createConfigurations();

		// expect
		expectedException.expect(AttributeNotSupportedException.class);

		// when
		asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.BOOSTITEMSMERGEMODE, UID1, UID2);
	}

	@Test
	public void failToRankAfterConfigurationForNonExistingRankAfterUid()
	{
		// given
		createConfigurations();

		// when
		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, NON_EXISTING_UID, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.NO_OPERATION, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertNull(rankChange.getOldRank());
		assertNull(rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID1, UID2,
				UID3);
	}

	@Test
	public void failToRankAfterConfigurationForNonExistingUid()
	{
		// given
		createConfigurations();

		// when
		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, UID2, NON_EXISTING_UID);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.NO_OPERATION, rankChange.getType());
		assertEquals(NON_EXISTING_UID, rankChange.getUid());
		assertNull(rankChange.getOldRank());
		assertNull(rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID1, UID2,
				UID3);
	}

	@Test
	public void rankConfigurationUp()
	{
		// given
		createConfigurations();

		// when
		final AsRankChange rankChange = asConfigurationService.rerankConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, UID2, -1);

		// then
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(0), rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID2, UID1,
				UID3);
	}

	@Test
	public void failToRankConfigurationUpForTooBigRankChange()
	{
		// given
		createConfigurations();

		// when
		final AsRankChange rankChange = asConfigurationService.rerankConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, UID2, -2);

		// then
		assertEquals(AsRankChangeType.NO_OPERATION, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertNull(rankChange.getOldRank());
		assertNull(rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID1, UID2,
				UID3);
	}

	@Test
	public void failToRankConfigurationUpForNonExistingAttribute()
	{
		// given
		createConfigurations();

		// expect
		expectedException.expect(AttributeNotSupportedException.class);

		// when
		asConfigurationService.rerankConfiguration(searchConfiguration, NON_EXISTING_ATTRIBUTE, UID2, -1);
	}

	@Test
	public void failToRankConfigurationUpForNonExistingUid()
	{
		// given
		createConfigurations();

		// when
		final AsRankChange rankChange = asConfigurationService.rerankConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, NON_EXISTING_UID, -1);

		// then
		assertEquals(AsRankChangeType.NO_OPERATION, rankChange.getType());
		assertEquals(NON_EXISTING_UID, rankChange.getUid());
		assertNull(rankChange.getOldRank());
		assertNull(rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID1, UID2,
				UID3);
	}

	@Test
	public void rankConfigurationDown()
	{
		// given
		createConfigurations();

		// when
		final AsRankChange rankChange = asConfigurationService.rerankConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, UID2, 1);

		// then
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(2), rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID1, UID3,
				UID2);
	}

	@Test
	public void failToRankConfigurationDownForTooBigRankChange()
	{
		// given
		createConfigurations();

		// when
		final AsRankChange rankChange = asConfigurationService.rerankConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, UID2, 2);

		// then
		assertEquals(AsRankChangeType.NO_OPERATION, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertNull(rankChange.getOldRank());
		assertNull(rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID1, UID2,
				UID3);
	}

	@Test
	public void failToRankConfigurationDownForNonExistingAttribute()
	{
		// given
		createConfigurations();

		// expect
		expectedException.expect(AttributeNotSupportedException.class);

		// when
		asConfigurationService.rerankConfiguration(searchConfiguration, NON_EXISTING_ATTRIBUTE, UID2, 1);
	}

	@Test
	public void failToRankConfigurationDownForNonExistingUid()
	{
		// given
		createConfigurations();

		// when
		final AsRankChange rankChange = asConfigurationService.rerankConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, NON_EXISTING_UID, 1);

		// then
		assertEquals(AsRankChangeType.NO_OPERATION, rankChange.getType());
		assertEquals(NON_EXISTING_UID, rankChange.getUid());
		assertNull(rankChange.getOldRank());
		assertNull(rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID1, UID2,
				UID3);
	}

	@Test
	public void rerankConfigurationToSamePosition()
	{
		// given
		createConfigurations();

		// when
		final AsRankChange rankChange = asConfigurationService.rerankConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDITEMS, UID2, 0);

		// then
		assertEquals(AsRankChangeType.NO_OPERATION, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertNull(rankChange.getOldRank());
		assertNull(rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedItems()).extracting(AbstractAsConfigurationModel.UID).containsExactly(UID1, UID2,
				UID3);
	}

	protected void createConfigurations()
	{
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

		final AsPromotedItemModel promotedItem3 = asConfigurationService.createConfiguration(AsPromotedItemModel.class);
		promotedItem3.setCatalogVersion(onlineCatalogVersion);
		promotedItem3.setUid(UID3);
		promotedItem3.setSearchConfiguration(searchConfiguration);
		promotedItem3.setItem(searchConfiguration);

		asConfigurationService.saveConfiguration(promotedItem1);
		asConfigurationService.saveConfiguration(promotedItem2);
		asConfigurationService.saveConfiguration(promotedItem3);

		modelService.refresh(searchConfiguration);
	}

}
