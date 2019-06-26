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
package de.hybris.platform.ruleengineservices.calculation.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.calculation.AbstractRuleEngineTest;
import de.hybris.platform.ruleengineservices.enums.OrderEntrySelectionStrategy;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.EntriesSelectionStrategyRPD;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Maps;


/**
 * Contains unit tests for the {@link CheapestEntriesSelectionStrategy}.
 */
@UnitTest
public class CheapestEntriesSelectionStrategyTest extends AbstractRuleEngineTest
{
	private final CheapestEntriesSelectionStrategy selector = new CheapestEntriesSelectionStrategy();

	@Test
	public void testPickup2OrderEntries()
	{

		final int orderEntry1_unitQuantity = 3;
		final int toPickupUnitQuantity = 4;
		final CartRAO cartRao1 = createCartRAO("cart01", USD);
		final OrderEntryRAO orderEntry1 = createOrderEntryRAO(cartRao1, "10.00", USD, orderEntry1_unitQuantity);
		final OrderEntryRAO orderEntry2 = createOrderEntryRAO(cartRao1, "15.00", USD, 3);
		final EntriesSelectionStrategyRPD selectionStrategy1 = createEntriesSelectionStrategyRPD(
				OrderEntrySelectionStrategy.CHEAPEST, toPickupUnitQuantity, false, orderEntry1, orderEntry2);
		final Map<Integer, Integer> result = selector.pickup(selectionStrategy1, Maps.newHashMap());
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(Integer.valueOf(orderEntry1_unitQuantity), result.get(orderEntry1.getEntryNumber()));
		Assert.assertEquals(Integer.valueOf(toPickupUnitQuantity - orderEntry1_unitQuantity),
				result.get(orderEntry2.getEntryNumber()));
	}

	@Test
	public void testPickup1OrderEntry()
	{
		final int orderEntry1_unitQuantity = 3;
		final int toPickupUnitQuantity = 2;
		final CartRAO cartRao1 = createCartRAO("cart01", USD);
		final OrderEntryRAO orderEntry1 = createOrderEntryRAO(cartRao1, "10.00", USD, orderEntry1_unitQuantity);
		final OrderEntryRAO orderEntry2 = createOrderEntryRAO(cartRao1, "15.00", USD, 3);
		final EntriesSelectionStrategyRPD selectionStrategy1 = createEntriesSelectionStrategyRPD(
				OrderEntrySelectionStrategy.CHEAPEST, toPickupUnitQuantity, false, orderEntry1, orderEntry2);
		final Map<Integer, Integer> result = selector.pickup(selectionStrategy1, Maps.newHashMap());
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Integer.valueOf(toPickupUnitQuantity), result.get(orderEntry1.getEntryNumber()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPickupMoreThanHave()
	{
		final int orderEntry1_unitQuantity = 3;
		final int orderEntry2_unitQuantity = 3;
		final int toPickupUnitQuantity = 8;
		final CartRAO cartRao1 = createCartRAO("cart01", USD);
		final OrderEntryRAO orderEntry1 = createOrderEntryRAO(cartRao1, "10.00", USD, orderEntry1_unitQuantity);
		final OrderEntryRAO orderEntry2 = createOrderEntryRAO(cartRao1, "15.00", USD, orderEntry2_unitQuantity);
		final EntriesSelectionStrategyRPD selectionStrategy1 = createEntriesSelectionStrategyRPD(
				OrderEntrySelectionStrategy.CHEAPEST, toPickupUnitQuantity, false, orderEntry1, orderEntry2);
		selector.pickup(selectionStrategy1, Maps.newHashMap());
	}
}
