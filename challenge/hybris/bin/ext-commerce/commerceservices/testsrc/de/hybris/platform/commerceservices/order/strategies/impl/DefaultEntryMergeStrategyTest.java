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
package de.hybris.platform.commerceservices.order.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.order.impl.EntryMergeFilterEntryGroup;
import de.hybris.platform.commerceservices.order.impl.EntryMergeFilterGiveAway;
import de.hybris.platform.commerceservices.order.impl.EntryMergeFilterIsEntryUpdatable;
import de.hybris.platform.commerceservices.order.impl.EntryMergeFilterProduct;
import de.hybris.platform.commerceservices.order.impl.EntryMergeFilterUnits;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultEntryMergeStrategyTest extends ServicelayerTransactionalTest
{
	@Resource
	private EntryMergeFilterIsEntryUpdatable entryMergeFilterIsEntryUpdatable;
	@Resource
	private EntryMergeFilterProduct entryMergeFilterProduct;
	@Resource
	private EntryMergeFilterUnits entryMergeFilterUnits;
	@Resource
	private EntryMergeFilterEntryGroup entryMergeFilterEntryGroup;
	@Resource
	private EntryMergeFilterGiveAway entryMergeFilterGiveAway;

	private DefaultEntryMergeStrategy strategy;

	@Before
	public void setup()
	{
		strategy = new DefaultEntryMergeStrategy();
		strategy.setEntryMergeFilters(Arrays.asList(entryMergeFilterIsEntryUpdatable, entryMergeFilterProduct,
				entryMergeFilterUnits, entryMergeFilterEntryGroup, entryMergeFilterGiveAway));
		strategy.setEntryModelComparator((o1, o2) -> {
			final Integer i1 = o1.getEntryNumber();
			final Integer i2 = o2.getEntryNumber();
			if (i1 == i2)
			{
				return 0;
			}
			if (i1 == null)
			{
				return 1;
			}
			if (i2 == null)
			{
				return -1;
			}
			return i1.compareTo(i2);
		});
	}

	@Test
	public void shouldReturnNullIfEntriesIsNull()
	{
		final AbstractOrderEntryModel result = strategy.getEntryToMerge(null, new AbstractOrderEntryModel());
		assertNull(result);
	}

	@Test
	public void shouldReturnNullIfEntriesIsEmpty()
	{
		final AbstractOrderEntryModel result = strategy.getEntryToMerge(Collections.emptyList(), new AbstractOrderEntryModel());
		assertNull(result);
	}

	@Test
	public void shouldReturnEntryWithTheSameProduct()
	{
		final AbstractOrderEntryModel entry1 = new AbstractOrderEntryModel();
		final AbstractOrderEntryModel entry2 = new AbstractOrderEntryModel();
		final ProductModel product1 = new ProductModel();
		final ProductModel product2 = new ProductModel();
		entry1.setProduct(product2);
		entry2.setProduct(product1);
		final AbstractOrderEntryModel newEntry = new AbstractOrderEntryModel();
		newEntry.setProduct(product1);

		final AbstractOrderEntryModel result = strategy.getEntryToMerge(Arrays.asList(entry1, entry2), newEntry);

		assertEquals(entry2, result);
	}

	@Test
	public void shouldReturnEntryWithTheSameUnit()
	{
		final AbstractOrderEntryModel entry1 = new AbstractOrderEntryModel();
		final AbstractOrderEntryModel entry2 = new AbstractOrderEntryModel();
		final UnitModel unit1 = new UnitModel();
		final UnitModel unit2 = new UnitModel();
		entry1.setUnit(unit1);
		entry2.setUnit(unit2);
		final AbstractOrderEntryModel newEntry = new AbstractOrderEntryModel();
		newEntry.setUnit(unit2);

		final AbstractOrderEntryModel result = strategy.getEntryToMerge(Arrays.asList(entry1, entry2), newEntry);

		assertEquals(entry2, result);
	}

	@Test
	public void shouldReturnEntryWithTheSameGroup()
	{
		final AbstractOrderEntryModel entry1 = new AbstractOrderEntryModel();
		final AbstractOrderEntryModel entry2 = new AbstractOrderEntryModel();
		entry1.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(1))));
		entry2.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(42))));
		final AbstractOrderEntryModel newEntry = new AbstractOrderEntryModel();
		newEntry.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(1))));

		final AbstractOrderEntryModel result = strategy.getEntryToMerge(Arrays.asList(entry1, entry2), newEntry);

		assertEquals(entry1, result);
	}

	@Test
	public void shouldIgnoreGiveawayEntries()
	{
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		final AbstractOrderEntryModel newEntry = new AbstractOrderEntryModel();
		assertNotNull(strategy.getEntryToMerge(Collections.singletonList(entry), newEntry));
		entry.setGiveAway(Boolean.TRUE);
		assertNull(strategy.getEntryToMerge(Collections.singletonList(entry), newEntry));
	}

	@Test
	public void shouldTakeTheFirstSuitableEntry()
	{
		final AbstractOrderEntryModel newEntry = new AbstractOrderEntryModel();
		final AbstractOrderEntryModel nonSuitableEntry = new AbstractOrderEntryModel();
		final AbstractOrderEntryModel firstSuitableEntry = new AbstractOrderEntryModel();
		final AbstractOrderEntryModel secondSuitableEntry = new AbstractOrderEntryModel();
		final ProductModel product = new ProductModel();
		firstSuitableEntry.setProduct(product);
		secondSuitableEntry.setProduct(product);
		newEntry.setProduct(product);

		final AbstractOrderEntryModel result = strategy.getEntryToMerge(
				Arrays.asList(nonSuitableEntry, firstSuitableEntry, secondSuitableEntry), newEntry);

		assertEquals(firstSuitableEntry, result);
	}

	@Test
	public void shouldSortApplicableEntries()
	{
		final AbstractOrderEntryModel newEntry = new AbstractOrderEntryModel();
		final AbstractOrderEntryModel nonSuitableEntry = new AbstractOrderEntryModel();
		final AbstractOrderEntryModel firstSuitableEntry = new AbstractOrderEntryModel();
		final AbstractOrderEntryModel secondSuitableEntry = new AbstractOrderEntryModel();
		final ProductModel product = new ProductModel();
		firstSuitableEntry.setProduct(product);
		secondSuitableEntry.setProduct(product);
		firstSuitableEntry.setEntryNumber(Integer.valueOf(12));
		secondSuitableEntry.setEntryNumber(Integer.valueOf(11));
		nonSuitableEntry.setEntryNumber(Integer.valueOf(1));
		newEntry.setProduct(product);

		final AbstractOrderEntryModel result = strategy.getEntryToMerge(
				Arrays.asList(nonSuitableEntry, firstSuitableEntry, secondSuitableEntry), newEntry);

		assertEquals(secondSuitableEntry, result);
	}

	@Test
	public void shouldReturnEntryWithGivenEntryNumber()
	{
		final AbstractOrderEntryModel newEntry = new AbstractOrderEntryModel();
		newEntry.setEntryNumber(Integer.valueOf(0));
		final AbstractOrderEntryModel firstSuitableEntry = new AbstractOrderEntryModel();
		final AbstractOrderEntryModel secondSuitableEntry = new AbstractOrderEntryModel();
		firstSuitableEntry.setEntryNumber(Integer.valueOf(1));
		secondSuitableEntry.setEntryNumber(Integer.valueOf(0));

		final AbstractOrderEntryModel result = strategy.getEntryToMerge(
				Arrays.asList(firstSuitableEntry, secondSuitableEntry), newEntry);

		assertEquals(secondSuitableEntry, result);
	}

	@Test
	public void shouldSkipTheCandidateInEntryList()
	{
		final AbstractOrderEntryModel newEntry = new AbstractOrderEntryModel();
		newEntry.setEntryNumber(Integer.valueOf(0));
		final AbstractOrderEntryModel firstSuitableEntry = new AbstractOrderEntryModel();
		final AbstractOrderEntryModel secondSuitableEntry = new AbstractOrderEntryModel();
		firstSuitableEntry.setEntryNumber(Integer.valueOf(1));
		secondSuitableEntry.setEntryNumber(Integer.valueOf(0));

		final AbstractOrderEntryModel result = strategy.getEntryToMerge(
				Arrays.asList(newEntry, firstSuitableEntry, secondSuitableEntry), newEntry);

		assertEquals(secondSuitableEntry, result);
	}

}
