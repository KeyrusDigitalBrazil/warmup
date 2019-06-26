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

import de.hybris.platform.commercefacades.order.EntryGroupData;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.selectivecartfacades.strategies.CartEntriesOrderingStrategy;
import de.hybris.platform.selectivecartservices.enums.CartSourceType;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;


/**
 * Default implementation of {@link CartEntriesOrderingStrategy}
 */
public class SelectiveCartEntriesOrderingStrategy implements CartEntriesOrderingStrategy
{

	@Override
	public CartData ordering(final CartData cartData)
	{
		cartData.setEntries(groupEntries(cartData.getEntries(), cartData));
		return cartData;
	}

	protected List<OrderEntryData> groupEntries(final List<OrderEntryData> entries, final AbstractOrderData order)
	{
		sortEntriesWithCartTime(entries);

		final List<EntryGroupData> newRootGroups = new ArrayList<>();

		for (final OrderEntryData entry : entries)
		{
			if (entry.getCartSourceType().equals(CartSourceType.WISHLIST))
			{
				setEntryGroups(entry, newRootGroups);
			}
			else
			{
				resetEntryGroupNumber(order, entry, newRootGroups);
			}
		}
		if (CollectionUtils.isNotEmpty(newRootGroups))
		{
			order.setRootGroups(newRootGroups);
		}
		return entries;
	}

	protected void sortEntriesWithCartTime(final List<OrderEntryData> entries)
	{
		Collections.sort(entries, (e1, e2) -> e2.getAddToCartTime().compareTo(e1.getAddToCartTime()));
	}

	protected void resetEntryGroupNumber(final AbstractOrderData order, final OrderEntryData entry,
			final List<EntryGroupData> newRootGroups)
	{
		final EntryGroupData rootGroup = getEntryGroup(order, entry.getProduct(), GroupType.STANDALONE);
		final int newGroupNumber = findMaxGroupNumber(newRootGroups) + 1;
		rootGroup.setGroupNumber(newGroupNumber);
		entry.setEntryGroupNumbers(Collections.singletonList(newGroupNumber));
		if (newRootGroups != null)
		{
			newRootGroups.add(rootGroup);
		}
	}

	protected void setEntryGroups(final OrderEntryData entry, final List<EntryGroupData> newRootGroups)
	{
		if (CollectionUtils.isEmpty(entry.getEntryGroupNumbers()))
		{
			final EntryGroupData rootGroup = createRootGroup(entry, newRootGroups);
			rootGroup.setOrderEntries(Collections.singletonList(entry));
			entry.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(rootGroup.getGroupNumber())));
			newRootGroups.add(rootGroup);
		}
	}

	protected EntryGroupData createRootGroup(final OrderEntryData groupedOrderEntry, final List<EntryGroupData> newRootGroups)
	{
		final EntryGroupData rootGroup = new EntryGroupData();
		rootGroup.setGroupNumber(Integer.valueOf(findMaxGroupNumber(newRootGroups) + 1));
		rootGroup.setGroupType(GroupType.STANDALONE);
		rootGroup.setChildren(new ArrayList<>());
		rootGroup.setOrderEntries(new ArrayList<>());
		rootGroup.getOrderEntries().add(groupedOrderEntry);
		return rootGroup;
	}

	@Nonnull
	protected List<EntryGroupData> getNestedGroups(@Nonnull final EntryGroupData root)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("root", root);

		final List<EntryGroupData> result = new ArrayList<>();
		result.add(root);
		for (int i = 0; i < result.size(); i++)
		{
			final List<EntryGroupData> children = result.get(i).getChildren();
			if (children != null)
			{
				result.addAll(children);
			}
		}
		return result;
	}

	@Nonnull
	protected EntryGroupData getEntryGroup(@Nonnull final AbstractOrderData order, @Nonnull final ProductData productData,
			@Nonnull final GroupType groupType)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("order.entryGroups", order.getRootGroups());
		ServicesUtil.validateParameterNotNullStandardMessage("product", productData);
		ServicesUtil.validateParameterNotNullStandardMessage("groupType", groupType);

		return order
				.getRootGroups()
				.stream()
				.map(this::getNestedGroups)
				.flatMap(Collection::stream)
				.filter(e -> groupType.equals(e.getGroupType()))
				.filter(
						e -> e.getOrderEntries().stream().map(OrderEntryData::getProduct).collect(Collectors.toSet())
								.contains(productData))
				.findAny()
				.orElseThrow(
						() -> new IllegalArgumentException("Group of type " + groupType.getCode() + " not found in order with code '"
								+ order.getCode() + "'"));
	}

	private int findMaxGroupNumber(final List<EntryGroupData> roots)
	{
		if (roots == null)
		{
			return 0;
		}
		return roots.stream().map(this::getNestedGroups).flatMap(Collection::stream).map(EntryGroupData::getGroupNumber)
				.max(Integer::compareTo).orElse(0);
	}

}
