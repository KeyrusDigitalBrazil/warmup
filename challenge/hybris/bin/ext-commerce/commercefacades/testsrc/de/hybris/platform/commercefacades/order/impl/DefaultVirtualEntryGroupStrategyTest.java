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
package de.hybris.platform.commercefacades.order.impl;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import de.hybris.platform.commercefacades.order.CommerceEntryGroupUtils;
import de.hybris.platform.commercefacades.order.EntryGroupData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.impl.DefaultVirtualEntryGroupStrategy;
import de.hybris.platform.core.enums.GroupType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class DefaultVirtualEntryGroupStrategyTest
{
	private DefaultVirtualEntryGroupStrategy strategy;
	private CommerceEntryGroupUtils entryGroupUtils;

	@Before
	public void setup()
	{
		entryGroupUtils = mock(CommerceEntryGroupUtils.class);
		strategy = new DefaultVirtualEntryGroupStrategy();
		strategy.setEntryGroupUtils(entryGroupUtils);
		given(entryGroupUtils.getNestedGroups(any(EntryGroupData.class))).willAnswer(
				invocationOnMock -> Collections.singletonList(invocationOnMock.getArguments()[0]));
	}

	@Test
	public void shouldCreateRootGroup()
	{
		final List<EntryGroupData> groups = new ArrayList<>();
		final OrderEntryData entry = new OrderEntryData();

		getStrategy().createGroup(groups, entry);

		assertThat(groups, iterableWithSize(1));
		final EntryGroupData groupData = groups.get(0);
		assertThat(groupData.getOrderEntries(), contains(entry));
		assertEquals(GroupType.STANDALONE, groupData.getGroupType());
		assertNull(groupData.getParent());
	}

	@Test
	public void shouldAssignFreeGroupNumber()
	{
		final List<EntryGroupData> groups = new ArrayList<>();
		final EntryGroupData root = new EntryGroupData();
		root.setGroupNumber(Integer.valueOf(100));
		groups.add(root);
		final EntryGroupData child = new EntryGroupData();
		child.setGroupNumber(Integer.valueOf(102));
		final OrderEntryData entry = new OrderEntryData();
		given(entryGroupUtils.getNestedGroups(groups.get(0))).willReturn(Arrays.asList(groups.get(0), child));

		getStrategy().createGroup(groups, entry);

		assertNotEquals(root.getGroupNumber().intValue(), groups.get(1).getGroupNumber().intValue());
		assertNotEquals(root.getGroupNumber().intValue(), groups.get(1).getGroupNumber().intValue());
		assertTrue(entry.getEntryGroupNumbers().contains(groups.get(1).getGroupNumber()));
	}

	protected DefaultVirtualEntryGroupStrategy getStrategy()
	{
		return strategy;
	}
}
