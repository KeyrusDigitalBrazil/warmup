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

package de.hybris.platform.configurablebundlefacades.order.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@UnitTest
public class BundleOrderEntryPopulatorTest
{

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private final BundleOrderEntryPopulator bundlingOrderEntryPopulator = new BundleOrderEntryPopulator();

	@Test
	public void testSourceParamCannotBeNull()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter source can not be null");

		bundlingOrderEntryPopulator.populate(null, new OrderEntryData());
	}

	@Test
	public void testTargetParamCannotBeNull()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter target can not be null");

		bundlingOrderEntryPopulator.populate(new OrderEntryModel(), null);
	}

	@Test
	public void testStandAloneProductsNotEditable()
	{
		final OrderEntryData entry = new OrderEntryData();
		final CartEntryModel cartEntry = new CartEntryModel();
		cartEntry.setBundleNo(Integer.valueOf(0));
		bundlingOrderEntryPopulator.adjustEditable(entry, cartEntry, null);
		Assert.assertFalse(entry.isEditable());
	}

	@Test
	public void sourceCanBeNotACartEntryForPopulating()
	{
		final OrderEntryData entryData = new OrderEntryData();
		final AbstractOrderEntryModel entryModel = new AbstractOrderEntryModel();
		entryModel.setOrder(new CartModel());
		bundlingOrderEntryPopulator.populate(entryModel, entryData);
	}

	@Test
	public void sourceCanBeNotACartEntryForAdjustment()
	{
		final OrderEntryData entry = new OrderEntryData();
		final AbstractOrderEntryModel cartEntry = new AbstractOrderEntryModel();
		bundlingOrderEntryPopulator.adjustUpdateable(entry, cartEntry);
	}
}
