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
package de.hybris.platform.selectivecartfacades.strategies.impl;

import static java.math.BigDecimal.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.EntryGroupData;
import de.hybris.platform.commercefacades.order.converters.populator.GroupOrderEntryPopulator;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.BaseOptionData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.selectivecartservices.enums.CartSourceType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


/**
 * Junit test suite for {@link SelectiveCartEntriesOrderingStrategy}
 */
@UnitTest
public class SelectiveCartEntriesOrderingStrategyTest
{
	public static final BigDecimal TEST_PRICE = valueOf(10L);
	public static final int TEST_QUANTITY = 10;

	SelectiveCartEntriesOrderingStrategy selectiveCartEntriesOrderingStrategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		selectiveCartEntriesOrderingStrategy = new SelectiveCartEntriesOrderingStrategy();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldGroupEntriesWithEntryGroups()
	{
		final CartData cart = new CartData();

		final DateTime secondEntryTime = new DateTime();
		final DateTime firstEntryTime = secondEntryTime.minusMinutes(1);
		final DateTime thirdEntryTime = secondEntryTime.plusMinutes(2);
		final DateTime fourthEntryTime = secondEntryTime.plusMinutes(3);

		//Wishlist Entries
		final OrderEntryData entryA = createOrderEntry("A");
		entryA.setAddToCartTime(secondEntryTime.toDate());
		entryA.setCartSourceType(CartSourceType.WISHLIST);
		final OrderEntryData entryC = createOrderEntry("C");
		entryC.setAddToCartTime(thirdEntryTime.toDate());
		entryC.setCartSourceType(CartSourceType.WISHLIST);

		//Normal Order Entries
		final OrderEntryData entryB = createOrderEntry("B");
		entryB.setEntryNumber(3);
		final EntryGroupData groupB = createEntryGroup(7);
		groupB.setOrderEntries(Collections.singletonList(entryB));
		entryB.setEntryGroupNumbers(Collections.singleton(7));
		entryB.setAddToCartTime(firstEntryTime.toDate());
		entryB.setCartSourceType(CartSourceType.STOREFRONT);
		final OrderEntryData entryD = createOrderEntry("D");
		entryD.setEntryNumber(5);
		final EntryGroupData groupD = createEntryGroup(4);
		groupD.setOrderEntries(Collections.singletonList(entryD));
		entryD.setEntryGroupNumbers(Collections.singleton(4));
		entryD.setAddToCartTime(fourthEntryTime.toDate());
		entryD.setCartSourceType(CartSourceType.STOREFRONT);

		cart.setEntries(new ArrayList<OrderEntryData>(Arrays.asList(entryA, entryB, entryC, entryD)));
		cart.setRootGroups(new ArrayList<EntryGroupData>(Arrays.asList(groupB, groupD)));

		final CartData result = selectiveCartEntriesOrderingStrategy.ordering(cart);

		final List<EntryGroupData> resultEntryGroups = result.getRootGroups();
		assertNotNull(resultEntryGroups);
		assertThat(resultEntryGroups, iterableWithSize(4));
		assertThat(
				resultEntryGroups,
				contains(
						allOf(hasProperty("groupType", is(GroupType.STANDALONE)), hasProperty("orderEntries", contains(entryD)),
								hasProperty("groupNumber", is(1))),
						allOf(hasProperty("groupType", is(GroupType.STANDALONE)), hasProperty("orderEntries", contains(entryC)),
								hasProperty("groupNumber", is(2))),
						allOf(hasProperty("groupType", is(GroupType.STANDALONE)), hasProperty("orderEntries", contains(entryA)),
								hasProperty("groupNumber", is(3))),
						allOf(hasProperty("groupType", is(GroupType.STANDALONE)), hasProperty("orderEntries", contains(entryB)),
								hasProperty("groupNumber", is(4)))));
	}

	protected OrderEntryData createOrderEntry(final String productCode)
	{
		final OrderEntryData entry = new OrderEntryData();

		final ProductData product = new ProductData();
		product.setCode(productCode);
		product.setBaseOptions(new ArrayList<BaseOptionData>());
		if (productCode != null)
		{
			final BaseOptionData baseOption = new BaseOptionData();
			baseOption.setVariantType(GroupOrderEntryPopulator.VARIANT_TYPE);
			product.getBaseOptions().add(baseOption);
		}
		product.setImages(new ArrayList<ImageData>());

		product.setMultidimensional(false);

		entry.setProduct(product);

		entry.setBasePrice(new PriceData());
		entry.getBasePrice().setValue(TEST_PRICE);
		entry.getBasePrice().setCurrencyIso("USD");
		entry.getBasePrice().setPriceType(PriceDataType.BUY);

		entry.setTotalPrice(new PriceData());
		entry.getTotalPrice().setCurrencyIso("USD");
		entry.getTotalPrice().setValue(TEST_PRICE);
		entry.getTotalPrice().setPriceType(PriceDataType.BUY);

		entry.setQuantity(Long.valueOf(1L));
		entry.setEntryGroupNumbers(Collections.emptyList());

		return entry;
	}

	protected EntryGroupData createEntryGroup(final int groupNumber)
	{
		final EntryGroupData group = new EntryGroupData();
		group.setGroupNumber(groupNumber);
		group.setGroupType(GroupType.STANDALONE);
		group.setChildren(new ArrayList<>());
		group.setOrderEntries(new ArrayList<>());
		return group;
	}
}
