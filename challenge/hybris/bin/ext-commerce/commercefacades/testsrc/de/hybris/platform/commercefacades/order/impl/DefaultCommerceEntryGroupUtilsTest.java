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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CommerceEntryGroupUtils;
import de.hybris.platform.commercefacades.order.EntryGroupData;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.hybris.platform.commercefacades.order.impl.DefaultCommerceEntryGroupUtils;
import org.junit.Test;


@UnitTest
public class DefaultCommerceEntryGroupUtilsTest
{
	final protected CommerceEntryGroupUtils utils = new DefaultCommerceEntryGroupUtils();

	@Test
	public void shouldFlattenGroups()
	{
		final EntryGroupData root = group(1,
				group(2),
				group(3,
						group(4)),
				group(5));

		final List<EntryGroupData> list = utils.getNestedGroups(root);

		assertThat(list, containsInAnyOrder(
				hasProperty("groupNumber", is(1)),
				hasProperty("groupNumber", is(2)),
				hasProperty("groupNumber", is(3)),
				hasProperty("groupNumber", is(4)),
				hasProperty("groupNumber", is(5))
		));
	}

	@Test(expected = IllegalArgumentException.class)
	public void flatterShouldReportAboutNullArg()
	{
		utils.getNestedGroups(null);
	}

	@Test
	public void shouldPutRootOnFirstPlace()
	{
		final EntryGroupData root = group(1,
				group(2),
				group(3,
						group(4)),
				group(5));

		final List<EntryGroupData> list = utils.getNestedGroups(root);

		assertEquals(1, (int) list.get(0).getGroupNumber());
	}

	@Test
	public void shouldKeepChildOrder()
	{
		final EntryGroupData former = group(2);
		final EntryGroupData latter = group(5);
		final EntryGroupData root = group(1,
				former,
				group(3,
						group(4)),
				latter);

		final List<EntryGroupData> list = utils.getNestedGroups(root);

		assertTrue(list.indexOf(former) < list.indexOf(latter));
	}

	@Test
	public void shouldReturnLeaves()
	{
		final EntryGroupData root = group(1,
				group(2),
				group(3,
						group(4)),
				group(5));

		final List<EntryGroupData> list = utils.getLeaves(root);

		assertThat(list, containsInAnyOrder(
				hasProperty("groupNumber", is(2)),
				hasProperty("groupNumber", is(4)),
				hasProperty("groupNumber", is(5))
		));
	}

	@Test(expected = IllegalArgumentException.class)
	public void leavesShouldReportAboutNullArg()
	{
		utils.getLeaves(null);
	}

	@Test
	public void shouldReturnMaxGroupNumber()
	{
		assertEquals(42, utils.findMaxGroupNumber(Arrays.asList(
				group(13),
				group(1,
						group(41,
								group(42)),
						group(11)))));
	}

	@Test
	public void findMaxShouldAcceptNullRootList()
	{
		assertEquals(0, utils.findMaxGroupNumber(null));
	}

	@Test
	public void findMaxShouldAcceptEmptyRootList()
	{
		assertEquals(0, utils.findMaxGroupNumber(Collections.emptyList()));
	}

	protected EntryGroupData group(final Integer number, final EntryGroupData... children)
	{
		final EntryGroupData result = new EntryGroupData();
		result.setGroupNumber(number);
		result.setChildren(Arrays.asList(children));
		for (final EntryGroupData child : children)
		{
			child.setParent(result);
		}
		return result;
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldReportIfGroupDoesNotExist()
	{
		final AbstractOrderData order = new AbstractOrderData();
		order.setRootGroups(Arrays.asList(
				entryGroupData(1,
						entryGroupData(2),
						entryGroupData(3,
								entryGroupData(10))),
				entryGroupData(4)));
		utils.getGroup(order, 11);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfNullOrder()
	{
		utils.getGroup(null, 11);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfEmptyRootGroupList()
	{
		final AbstractOrderData order = new AbstractOrderData();
		order.setRootGroups(Collections.emptyList());
		utils.getGroup(order, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfNullGroupNumber()
	{
		utils.getGroup(new AbstractOrderData(), null);
	}

	@Test
	public void shouldReturnEntryGroupByNumber()
	{
		final CartData cartData = new CartData();
		cartData.setRootGroups(Arrays.asList(entryGroupData(1), entryGroupData(4)));
		final EntryGroupData result = utils.getGroup(cartData, 4);
		assertEquals(Integer.valueOf(4), result.getGroupNumber());
	}

	@Test
	public void shouldReturnChildEntryGroupByNumber()
	{
		final CartData cartData = new CartData();
		cartData.setRootGroups(Arrays.asList(
				entryGroupData(1,
						entryGroupData(2)),
				entryGroupData(4,
						entryGroupData(5),
						entryGroupData(6))));
		final EntryGroupData result = utils.getGroup(cartData, 6);
		assertEquals(Integer.valueOf(6), result.getGroupNumber());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailOnGettingNonExistingGroup()
	{
		final CartData cartData = new CartData();
		cartData.setRootGroups(Arrays.asList(entryGroupData(1), entryGroupData(4)));

		utils.getGroup(cartData, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailOnGettingNullGroup()
	{
		final CartData cartData = new CartData();
		cartData.setRootGroups(Arrays.asList(entryGroupData(1), entryGroupData(4)));

		utils.getGroup(cartData, null);
	}

	protected EntryGroupData entryGroupData(final Integer number, final EntryGroupData... children)
	{
		final EntryGroupData result = new EntryGroupData();
		result.setGroupNumber(number);
		result.setChildren(Stream.of(children)
				.peek(child -> child.setParent(result))
				.collect(Collectors.toList()));
		return result;
	}
}
