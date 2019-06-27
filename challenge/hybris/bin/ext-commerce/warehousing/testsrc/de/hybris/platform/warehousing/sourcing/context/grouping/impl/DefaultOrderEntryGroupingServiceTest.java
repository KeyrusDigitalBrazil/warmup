/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.sourcing.context.grouping.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.warehousing.sourcing.context.grouping.OrderEntryGroup;
import de.hybris.platform.warehousing.sourcing.context.grouping.OrderEntryMatcher;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderEntryGroupingServiceTest
{
	private static final String MATCHING_INFO_A = "MATCH A";
	private static final String MATCHING_INFO_B = "MATCH B";
	private static final String MATCHING_INFO_C = "MATCH C";
	private static final String MATCHING_INFO_D = "MATCH D";

	private static final Integer MATCHING_NUMBER_1 = 1;
	private static final Integer MATCHING_NUMBER_2 = 2;
	private static final Integer MATCHING_NUMBER_3 = 3;
	private static final Integer MATCHING_NUMBER_4 = 4;

	private static final Long DEFAULT_QUANTITY = Long.valueOf(4L);

	@InjectMocks
	private final DefaultOrderEntryGroupingService groupingService = new DefaultOrderEntryGroupingService();
	private final OrderEntryMatcher<String> infoMatcher = new InfoMatcher();
	private final OrderEntryMatcher<Integer> numberMatcher = new EntryNumberMatcher();
	private OrderEntryGroup singleGroup;

	@Mock
	private OrderEntryModel orderEntry1;
	@Mock
	private OrderEntryModel orderEntry2;
	@Mock
	private OrderEntryModel orderEntry3;
	@Mock
	private OrderEntryModel orderEntry4;

	@Before
	public void setUp()
	{

	}

	/*
	 * splitGroupByMatcher tests
	 */
	@Test
	public void shouldSplitSingleGroupByMatcherIntoSingleGroup_SingleEntry()
	{
		when(orderEntry1.getInfo()).thenReturn(MATCHING_INFO_A);
		singleGroup = new OrderEntryGroup(Sets.newHashSet(orderEntry1));

		final Set<OrderEntryGroup> groups = groupingService.splitGroupByMatcher(singleGroup, infoMatcher);
		Assert.assertEquals(1, groups.size());

		final Collection<AbstractOrderEntryModel> entries = groups.iterator().next().getEntries();
		Assert.assertEquals(1, entries.size());
		Assert.assertEquals(MATCHING_INFO_A, entries.iterator().next().getInfo());
	}

	@Test
	public void shouldSplitSingleGroupByMatcherIntoSingleGroup_MultipleEntries()
	{
		when(orderEntry1.getInfo()).thenReturn(MATCHING_INFO_A);
		when(orderEntry2.getInfo()).thenReturn(MATCHING_INFO_A);

		singleGroup = new OrderEntryGroup(Sets.newHashSet(orderEntry1, orderEntry2));

		final Set<OrderEntryGroup> groups = groupingService.splitGroupByMatcher(singleGroup, infoMatcher);
		Assert.assertEquals(1, groups.size());

		final Collection<AbstractOrderEntryModel> entries = groups.iterator().next().getEntries();
		Assert.assertEquals(2, entries.size());
		entries.forEach(entry -> Assert.assertEquals(MATCHING_INFO_A, entry.getInfo()));
	}

	@Test
	public void shouldSplitSingleGroupByMatcherIntoMultipleGroups_MultipleEntries()
	{
		when(orderEntry1.getInfo()).thenReturn(MATCHING_INFO_A);
		when(orderEntry2.getInfo()).thenReturn(MATCHING_INFO_B);
		when(orderEntry3.getInfo()).thenReturn(MATCHING_INFO_B);
		when(orderEntry4.getInfo()).thenReturn(MATCHING_INFO_A);

		singleGroup = new OrderEntryGroup(Sets.newHashSet(orderEntry1, orderEntry2, orderEntry3, orderEntry4));

		final Set<OrderEntryGroup> groups = groupingService.splitGroupByMatcher(singleGroup, infoMatcher);
		Assert.assertEquals(2, groups.size());

		for(final OrderEntryGroup group : groups)
		{
			final Collection<AbstractOrderEntryModel> entries = group.getEntries();
			Assert.assertEquals(2, entries.size());
			Assert.assertEquals(entries.iterator().next().getInfo(), entries.iterator().next().getInfo());
		}
	}

	/*
	 * splitGroupsByMatcher tests
	 */
	@Test
	public void shouldSplitSingleGroupSetByMatcherIntoSingleGroup_SingleEntry()
	{
		when(orderEntry1.getInfo()).thenReturn(MATCHING_INFO_A);
		singleGroup = new OrderEntryGroup(Sets.newHashSet(orderEntry1));

		final Set<OrderEntryGroup> groups = groupingService.splitGroupsByMatcher(Sets.newHashSet(singleGroup), infoMatcher);
		Assert.assertEquals(1, groups.size());

		final Collection<AbstractOrderEntryModel> entries = groups.iterator().next().getEntries();
		Assert.assertEquals(1, entries.size());
		Assert.assertEquals(MATCHING_INFO_A, entries.iterator().next().getInfo());
	}

	@Test
	public void shouldSplitSingleGroupSetByMatcherIntoSingleGroup_MultipleEntries()
	{
		when(orderEntry1.getInfo()).thenReturn(MATCHING_INFO_A);
		when(orderEntry2.getInfo()).thenReturn(MATCHING_INFO_A);

		singleGroup = new OrderEntryGroup(Sets.newHashSet(orderEntry1, orderEntry2));

		final Set<OrderEntryGroup> groups = groupingService.splitGroupsByMatcher(Sets.newHashSet(singleGroup), infoMatcher);
		Assert.assertEquals(1, groups.size());

		final Collection<AbstractOrderEntryModel> entries = groups.iterator().next().getEntries();
		Assert.assertEquals(2, entries.size());
		entries.forEach(entry -> Assert.assertEquals(MATCHING_INFO_A, entry.getInfo()));
	}

	@Test
	public void shouldSplitSingleGroupSetByMatcherIntoMultipleGroups_MultipleEntries()
	{
		when(orderEntry1.getInfo()).thenReturn(MATCHING_INFO_A);
		when(orderEntry2.getInfo()).thenReturn(MATCHING_INFO_B);
		when(orderEntry3.getInfo()).thenReturn(MATCHING_INFO_B);
		when(orderEntry4.getInfo()).thenReturn(MATCHING_INFO_A);
		singleGroup = new OrderEntryGroup(Sets.newHashSet(orderEntry1, orderEntry2, orderEntry3, orderEntry4));

		final Set<OrderEntryGroup> groups = groupingService.splitGroupsByMatcher(Sets.newHashSet(singleGroup), infoMatcher);
		Assert.assertEquals(2, groups.size());

		for (final OrderEntryGroup group : groups)
		{
			final Collection<AbstractOrderEntryModel> entries = group.getEntries();
			Assert.assertEquals(2, entries.size());
			Assert.assertEquals(entries.iterator().next().getInfo(), entries.iterator().next().getInfo());
		}
	}

	@Test
	public void shouldSplitMultipleGroupSetsByMatcherIntoSameGroups()
	{
		when(orderEntry1.getInfo()).thenReturn(MATCHING_INFO_A);
		when(orderEntry2.getInfo()).thenReturn(MATCHING_INFO_A);
		when(orderEntry3.getInfo()).thenReturn(MATCHING_INFO_B);
		when(orderEntry4.getInfo()).thenReturn(MATCHING_INFO_B);

		final OrderEntryGroup groupA = new OrderEntryGroup(Sets.newHashSet(orderEntry1, orderEntry2));
		final OrderEntryGroup groupB = new OrderEntryGroup(Sets.newHashSet(orderEntry3, orderEntry4));

		final Set<OrderEntryGroup> groups = groupingService.splitGroupsByMatcher(Sets.newHashSet(groupA, groupB), infoMatcher);
		Assert.assertEquals(2, groups.size());

		for (final OrderEntryGroup group : groups)
		{
			final Collection<AbstractOrderEntryModel> entries = group.getEntries();
			Assert.assertEquals(2, entries.size());
			Assert.assertEquals(entries.iterator().next().getInfo(), entries.iterator().next().getInfo());
		}
	}

	@Test
	public void shouldSplitMultipleGroupSetsByMatcherIntoSmallerGroups()
	{
		when(orderEntry1.getInfo()).thenReturn(MATCHING_INFO_A);
		when(orderEntry2.getInfo()).thenReturn(MATCHING_INFO_B);
		when(orderEntry3.getInfo()).thenReturn(MATCHING_INFO_C);
		when(orderEntry4.getInfo()).thenReturn(MATCHING_INFO_D);

		final OrderEntryGroup groupA = new OrderEntryGroup(Sets.newHashSet(orderEntry1, orderEntry2));
		final OrderEntryGroup groupB = new OrderEntryGroup(Sets.newHashSet(orderEntry3, orderEntry4));

		final Set<OrderEntryGroup> groups = groupingService.splitGroupsByMatcher(Sets.newHashSet(groupA, groupB), infoMatcher);
		Assert.assertEquals(4, groups.size());
		groups.forEach(group -> Assert.assertEquals(1, group.getEntries().size()));
	}

	/*
	 * splitOrderByMatchers tests
	 */
	@Test
	public void shouldSplitSingleEntryOrderBySingleMatcherIntoSingleGroup()
	{
		final AbstractOrderModel order = new AbstractOrderModel();
		when(orderEntry1.getInfo()).thenReturn(MATCHING_INFO_A);
		when(orderEntry1.getQuantityUnallocated()).thenReturn(DEFAULT_QUANTITY);
		order.setEntries(Lists.newArrayList(orderEntry1));

		final Set<OrderEntryGroup> groups = groupingService.splitOrderByMatchers(order, Sets.newHashSet(infoMatcher));
		Assert.assertEquals(1, groups.size());

		final Collection<AbstractOrderEntryModel> entries = groups.iterator().next().getEntries();
		Assert.assertEquals(1, entries.size());
		Assert.assertEquals(MATCHING_INFO_A, entries.iterator().next().getInfo());
	}

	@Test
	public void shouldSplitMultiEntryOrderBySingleMatcherIntoSingleGroup()
	{
		final AbstractOrderModel order = new AbstractOrderModel();
		when(orderEntry1.getInfo()).thenReturn(MATCHING_INFO_A);
		when(orderEntry1.getQuantityUnallocated()).thenReturn(DEFAULT_QUANTITY);
		when(orderEntry2.getInfo()).thenReturn(MATCHING_INFO_A);
		when(orderEntry2.getQuantityUnallocated()).thenReturn(DEFAULT_QUANTITY);

		order.setEntries(Lists.newArrayList(orderEntry1, orderEntry2));
		final Set<OrderEntryGroup> groups = groupingService.splitOrderByMatchers(order, Sets.newHashSet(infoMatcher));
		Assert.assertEquals(1, groups.size());

		final Collection<AbstractOrderEntryModel> entries = groups.iterator().next().getEntries();
		Assert.assertEquals(2, entries.size());
		entries.forEach(entry -> Assert.assertEquals(MATCHING_INFO_A, entry.getInfo()));
	}

	@Test
	public void shouldSplitSingleEntryOrderByMultipleMatchersIntoSingleGroup()
	{
		final AbstractOrderModel order = new AbstractOrderModel();
		when(orderEntry1.getInfo()).thenReturn(MATCHING_INFO_A);
		when(orderEntry1.getQuantityUnallocated()).thenReturn(DEFAULT_QUANTITY);
		when(orderEntry1.getEntryNumber()).thenReturn(MATCHING_NUMBER_1);
		order.setEntries(Lists.newArrayList(orderEntry1));

		final Set<OrderEntryGroup> groups = groupingService
				.splitOrderByMatchers(order, Sets.newHashSet(infoMatcher, numberMatcher));
		Assert.assertEquals(1, groups.size());

		final Collection<AbstractOrderEntryModel> entries = groups.iterator().next().getEntries();
		Assert.assertEquals(1, entries.size());

		final AbstractOrderEntryModel entry = entries.iterator().next();
		Assert.assertEquals(MATCHING_INFO_A, entry.getInfo());
		Assert.assertEquals(MATCHING_NUMBER_1, entry.getEntryNumber());
	}

	@Test
	public void shouldSplitMultiEntryOrderByMultipleMatchersIntoSingleGroup()
	{
		final AbstractOrderModel order = new AbstractOrderModel();
		when(orderEntry1.getInfo()).thenReturn(MATCHING_INFO_A);
		when(orderEntry1.getQuantityUnallocated()).thenReturn(DEFAULT_QUANTITY);
		when(orderEntry1.getEntryNumber()).thenReturn(MATCHING_NUMBER_1);
		when(orderEntry2.getInfo()).thenReturn(MATCHING_INFO_A);
		when(orderEntry2.getQuantityUnallocated()).thenReturn(DEFAULT_QUANTITY);
		when(orderEntry2.getEntryNumber()).thenReturn(MATCHING_NUMBER_1);
		order.setEntries(Lists.newArrayList(orderEntry1, orderEntry2));

		final Set<OrderEntryGroup> groups = groupingService
				.splitOrderByMatchers(order, Sets.newHashSet(infoMatcher, numberMatcher));
		Assert.assertEquals(1, groups.size());

		final Collection<AbstractOrderEntryModel> entries = groups.iterator().next().getEntries();
		Assert.assertEquals(2, entries.size());
		entries.forEach(entry -> {
			Assert.assertEquals(MATCHING_INFO_A, entry.getInfo());
			Assert.assertEquals(MATCHING_NUMBER_1, entry.getEntryNumber());
		});
	}

	@Test
	public void shouldSplitMultiEntryOrderByMultipleMatchersIntoTwoGroups()
	{
		final AbstractOrderModel order = new AbstractOrderModel();
		when(orderEntry1.getInfo()).thenReturn(MATCHING_INFO_A);
		when(orderEntry1.getQuantityUnallocated()).thenReturn(DEFAULT_QUANTITY);
		when(orderEntry1.getEntryNumber()).thenReturn(MATCHING_NUMBER_1);

		when(orderEntry2.getInfo()).thenReturn(MATCHING_INFO_A);
		when(orderEntry2.getQuantityUnallocated()).thenReturn(DEFAULT_QUANTITY);
		when(orderEntry2.getEntryNumber()).thenReturn(MATCHING_NUMBER_1);

		when(orderEntry3.getInfo()).thenReturn(MATCHING_INFO_B);
		when(orderEntry3.getQuantityUnallocated()).thenReturn(DEFAULT_QUANTITY);
		when(orderEntry3.getEntryNumber()).thenReturn(MATCHING_NUMBER_2);

		when(orderEntry4.getInfo()).thenReturn(MATCHING_INFO_B);
		when(orderEntry4.getQuantityUnallocated()).thenReturn(DEFAULT_QUANTITY);
		when(orderEntry4.getEntryNumber()).thenReturn(MATCHING_NUMBER_2);

		order.setEntries(Lists.newArrayList(orderEntry1, orderEntry2, orderEntry3, orderEntry4));

		final Set<OrderEntryGroup> groups = groupingService
				.splitOrderByMatchers(order, Sets.newHashSet(infoMatcher, numberMatcher));
		Assert.assertEquals(2, groups.size());

		for (final OrderEntryGroup group : groups)
		{
			final Collection<AbstractOrderEntryModel> entries = group.getEntries();
			Assert.assertEquals(2, entries.size());

			final Iterator<AbstractOrderEntryModel> it = entries.iterator();
			final AbstractOrderEntryModel entry1 = it.next();
			final AbstractOrderEntryModel entry2 = it.next();
			Assert.assertEquals(entry1.getInfo(), entry2.getInfo());
			Assert.assertEquals(entry1.getEntryNumber(), entry2.getEntryNumber());
		}
	}

	@Test
	public void shouldSplitMultiEntryOrderByMultipleMatchersIntoFourGroups()
	{
		final AbstractOrderModel order = new AbstractOrderModel();
		when(orderEntry1.getInfo()).thenReturn(MATCHING_INFO_A);
		when(orderEntry1.getQuantityUnallocated()).thenReturn(DEFAULT_QUANTITY);
		when(orderEntry1.getEntryNumber()).thenReturn(MATCHING_NUMBER_1);

		when(orderEntry2.getInfo()).thenReturn(MATCHING_INFO_A);
		when(orderEntry2.getQuantityUnallocated()).thenReturn(DEFAULT_QUANTITY);
		when(orderEntry2.getEntryNumber()).thenReturn(MATCHING_NUMBER_2);

		when(orderEntry3.getInfo()).thenReturn(MATCHING_INFO_B);
		when(orderEntry3.getQuantityUnallocated()).thenReturn(DEFAULT_QUANTITY);
		when(orderEntry3.getEntryNumber()).thenReturn(MATCHING_NUMBER_3);

		when(orderEntry4.getInfo()).thenReturn(MATCHING_INFO_B);
		when(orderEntry4.getQuantityUnallocated()).thenReturn(DEFAULT_QUANTITY);
		when(orderEntry4.getEntryNumber()).thenReturn(MATCHING_NUMBER_4);

		order.setEntries(Lists.newArrayList(orderEntry1, orderEntry2, orderEntry3, orderEntry4));

		final Set<OrderEntryGroup> groups = groupingService
				.splitOrderByMatchers(order, Sets.newHashSet(infoMatcher, numberMatcher));
		Assert.assertEquals(4, groups.size());
		groups.forEach(group -> Assert.assertEquals(1, group.getEntries().size()));
	}

	/**
	 * Matcher to match order entries based on {@link AbstractOrderEntryModel#getInfo()} attribute.
	 */
	private static class InfoMatcher implements OrderEntryMatcher<String>
	{
		@Override
		public String getMatchingObject(final AbstractOrderEntryModel orderEntry)
		{
			return orderEntry.getInfo();
		}
	}

	/**
	 * Matcher to match order entries based on {@link AbstractOrderEntryModel#getEntryNumber()} attribute.
	 */
	private static class EntryNumberMatcher implements OrderEntryMatcher<Integer>
	{
		@Override
		public Integer getMatchingObject(final AbstractOrderEntryModel orderEntry)
		{
			return orderEntry.getEntryNumber();
		}
	}
}
