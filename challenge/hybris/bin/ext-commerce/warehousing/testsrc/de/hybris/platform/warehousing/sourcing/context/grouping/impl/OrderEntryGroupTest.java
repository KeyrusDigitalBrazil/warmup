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

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.warehousing.sourcing.context.grouping.OrderEntryGroup;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;


public class OrderEntryGroupTest
{

	@Test
	public void shouldCreateGroup()
	{
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();

		final Collection<AbstractOrderEntryModel> entries = Sets.newHashSet(entry);
		final OrderEntryGroup group = new OrderEntryGroup(entries);

		Assert.assertEquals(entries.iterator().next(), group.getEntries().iterator().next());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCreateGroup_NullEntries()
	{
		new OrderEntryGroup(null);
		Assert.fail();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldReturnImmutableList()
	{
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		final Collection<AbstractOrderEntryModel> entries = Sets.newHashSet(entry);
		final OrderEntryGroup group = new OrderEntryGroup(entries);

		group.getEntries().add(entry);
	}
}
