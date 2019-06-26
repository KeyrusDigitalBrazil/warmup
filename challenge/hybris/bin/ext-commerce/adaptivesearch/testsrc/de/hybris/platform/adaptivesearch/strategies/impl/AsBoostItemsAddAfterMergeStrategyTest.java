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
package de.hybris.platform.adaptivesearch.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.data.AbstractAsBoostItemConfiguration;
import de.hybris.platform.adaptivesearch.data.AsConfigurationHolder;
import de.hybris.platform.adaptivesearch.data.AsExcludedItem;
import de.hybris.platform.adaptivesearch.data.AsPromotedItem;
import de.hybris.platform.adaptivesearch.util.MergeMap;
import de.hybris.platform.core.PK;

import java.util.List;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class AsBoostItemsAddAfterMergeStrategyTest extends AbstractAsBoostRulesMergeStrategyTest
{
	private AsBoostItemsAddAfterMergeStrategy mergeStrategy;

	@Before
	public void createMergeStrategy()
	{
		mergeStrategy = new AsBoostItemsAddAfterMergeStrategy();
		mergeStrategy.setAsSearchProfileResultFactory(getAsSearchProfileResultFactory());
	}

	@Test
	public void mergePromotedItems()
	{
		// given
		final AsPromotedItem promotedItem1 = new AsPromotedItem();
		promotedItem1.setItemPk(PK_1);
		promotedItem1.setUid(UID_1);

		final AsPromotedItem promotedItem2 = new AsPromotedItem();
		promotedItem2.setItemPk(PK_2);
		promotedItem2.setUid(UID_2);

		getTarget().getPromotedItems().put(promotedItem1.getItemPk(), createConfigurationHolder(promotedItem1));
		getSource().getPromotedItems().put(promotedItem2.getItemPk(), createConfigurationHolder(promotedItem2));

		// when
		mergeStrategy.mergeBoostItems(getSource(), getTarget());

		// then
		assertEquals(2, getTarget().getPromotedItems().size());
		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) getTarget()
				.getPromotedItems()).orderedValues();

		final AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration> promotedItem1Holder = promotedItems.get(0);
		assertSame(promotedItem1, promotedItem1Holder.getConfiguration());

		final AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration> promotedItem2Holder = promotedItems.get(1);
		assertSame(promotedItem2, promotedItem2Holder.getConfiguration());
	}

	@Test
	public void mergePromotedItemsWithDuplicates()
	{
		// given
		final AsPromotedItem promotedItem1 = new AsPromotedItem();
		promotedItem1.setItemPk(PK_1);
		promotedItem1.setUid(UID_2);

		final AsPromotedItem promotedItem2 = new AsPromotedItem();
		promotedItem2.setItemPk(PK_2);
		promotedItem2.setUid(UID_3);

		final AsExcludedItem excludedItem1 = new AsExcludedItem();
		excludedItem1.setItemPk(PK_2);
		excludedItem1.setUid(UID_4);

		final AsExcludedItem excludedItem2 = new AsExcludedItem();
		excludedItem2.setItemPk(PK_3);
		excludedItem2.setUid(UID_5);

		getTarget().getExcludedItems().put(excludedItem1.getItemPk(), createConfigurationHolder(excludedItem1));
		getTarget().getExcludedItems().put(excludedItem2.getItemPk(), createConfigurationHolder(excludedItem2));

		getSource().getPromotedItems().put(promotedItem1.getItemPk(), createConfigurationHolder(promotedItem1));
		getSource().getPromotedItems().put(promotedItem2.getItemPk(), createConfigurationHolder(promotedItem2));

		// when
		mergeStrategy.mergeBoostItems(getSource(), getTarget());

		// then
		assertEquals(2, getTarget().getPromotedItems().size());
		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) getTarget()
				.getPromotedItems()).orderedValues();

		final AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration> promotedItem1Holder = promotedItems.get(0);
		assertSame(promotedItem1, promotedItem1Holder.getConfiguration());

		final AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration> promotedItem2Holder = promotedItems.get(1);
		assertSame(promotedItem2, promotedItem2Holder.getConfiguration());

		assertEquals(1, getTarget().getExcludedItems().size());
		final List<AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>> excludedItems = ((MergeMap<PK, AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>>) getTarget()
				.getExcludedItems()).orderedValues();

		final AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration> excludedItem2Holder = excludedItems.get(0);
		assertSame(excludedItem2, excludedItem2Holder.getConfiguration());
	}

	@Test
	public void mergeExcludedItems()
	{
		// given
		final AsExcludedItem excludedItem1 = new AsExcludedItem();
		excludedItem1.setItemPk(PK_1);
		excludedItem1.setUid(UID_1);

		final AsExcludedItem excludedItem2 = new AsExcludedItem();
		excludedItem2.setItemPk(PK_2);
		excludedItem2.setUid(UID_2);

		getTarget().getExcludedItems().put(excludedItem1.getItemPk(), createConfigurationHolder(excludedItem1));
		getSource().getExcludedItems().put(excludedItem2.getItemPk(), createConfigurationHolder(excludedItem2));

		// when
		mergeStrategy.mergeBoostItems(getSource(), getTarget());

		// then
		assertEquals(2, getTarget().getExcludedItems().size());
		final List<AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>> excludedItems = ((MergeMap<PK, AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>>) getTarget()
				.getExcludedItems()).orderedValues();

		final AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration> excludedItem1Holder = excludedItems.get(0);
		assertSame(excludedItem1, excludedItem1Holder.getConfiguration());

		final AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration> excludedItem2Holder = excludedItems.get(1);
		assertSame(excludedItem2, excludedItem2Holder.getConfiguration());
	}

	@Test
	public void mergeExcludedItemsWithDuplicates()
	{
		// given
		final AsPromotedItem promotedItem1 = new AsPromotedItem();
		promotedItem1.setItemPk(PK_1);
		promotedItem1.setUid(UID_2);

		final AsPromotedItem promotedItem2 = new AsPromotedItem();
		promotedItem2.setItemPk(PK_2);
		promotedItem2.setUid(UID_3);

		final AsExcludedItem excludedItem1 = new AsExcludedItem();
		excludedItem1.setItemPk(PK_2);
		excludedItem1.setUid(UID_4);

		final AsExcludedItem excludedItem2 = new AsExcludedItem();
		excludedItem2.setItemPk(PK_3);
		excludedItem2.setUid(UID_5);

		getTarget().getPromotedItems().put(promotedItem1.getItemPk(), createConfigurationHolder(promotedItem1));
		getTarget().getPromotedItems().put(promotedItem2.getItemPk(), createConfigurationHolder(promotedItem2));

		getSource().getExcludedItems().put(excludedItem1.getItemPk(), createConfigurationHolder(excludedItem1));
		getSource().getExcludedItems().put(excludedItem2.getItemPk(), createConfigurationHolder(excludedItem2));

		// when
		mergeStrategy.mergeBoostItems(getSource(), getTarget());

		// then
		assertEquals(1, getTarget().getPromotedItems().size());
		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) getTarget()
				.getPromotedItems()).orderedValues();

		final AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration> promotedItem1Holder = promotedItems.get(0);
		assertSame(promotedItem1, promotedItem1Holder.getConfiguration());

		assertEquals(2, getTarget().getExcludedItems().size());
		final List<AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>> excludedItems = ((MergeMap<PK, AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>>) getTarget()
				.getExcludedItems()).orderedValues();

		final AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration> excludedItem1Holder = excludedItems.get(0);
		assertSame(excludedItem1, excludedItem1Holder.getConfiguration());

		final AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration> excludedItem2Holder = excludedItems.get(1);
		assertSame(excludedItem2, excludedItem2Holder.getConfiguration());
	}
}
