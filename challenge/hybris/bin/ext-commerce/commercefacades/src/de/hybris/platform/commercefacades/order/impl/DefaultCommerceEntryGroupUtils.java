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

import de.hybris.platform.commercefacades.order.CommerceEntryGroupUtils;
import de.hybris.platform.commercefacades.order.EntryGroupData;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;


/**
 * Default implementation of {@code CommerceEntryGroupUtils}.
 */
public class DefaultCommerceEntryGroupUtils implements CommerceEntryGroupUtils
{
	@Nonnull
	@Override
	public List<EntryGroupData> getNestedGroups(@Nonnull final EntryGroupData root)
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
	@Override
	public List<EntryGroupData> getLeaves(@Nonnull final EntryGroupData root)
	{
		return getNestedGroups(root).stream()
				.filter(node -> CollectionUtils.isEmpty(node.getChildren()))
				.collect(Collectors.toList());
	}

	@Nonnull
	@Override
	public EntryGroupData getGroup(@Nonnull final AbstractOrderData order, @Nonnull final Integer groupNumber)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("order", order);
		ServicesUtil.validateParameterNotNullStandardMessage("groupNumber", groupNumber);
		ServicesUtil.validateParameterNotNullStandardMessage("order.entryGroups", order.getRootGroups());

		return order.getRootGroups().stream()
				.map(this::getNestedGroups)
				.flatMap(Collection::stream)
				.filter(e -> groupNumber.equals(e.getGroupNumber()))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("No group with number '" + groupNumber
						+ "' in the order with code '" + order.getCode() + "'"));
	}
	
	@Nonnull
	@Override
	public EntryGroupData getGroup(
			@Nonnull final AbstractOrderData order,
			@Nonnull final Collection<Integer> groupNumbers,
			@Nonnull final GroupType groupType)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("order.entryGroups", order.getRootGroups());
		ServicesUtil.validateParameterNotNullStandardMessage("groupNumbers", groupNumbers);
		ServicesUtil.validateParameterNotNullStandardMessage("groupType", groupType);

		return order.getRootGroups().stream()
				.map(this::getNestedGroups)
				.flatMap(Collection::stream)
				.filter(e -> groupType.equals(e.getGroupType()))
				.filter(e -> groupNumbers.contains(e.getGroupNumber()))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("Group of type " + groupType.getCode()
						+ " not found in order with code '" + order.getCode() + "'"));
	}

	@Override
	public int findMaxGroupNumber(final List<EntryGroupData> roots)
	{
		if (roots == null)
		{
			return 0;
		}
		return roots.stream()
				.map(this::getNestedGroups)
				.flatMap(Collection::stream)
				.map(EntryGroupData::getGroupNumber)
				.max(Integer::compareTo)
				.orElse(0);
	}
}
