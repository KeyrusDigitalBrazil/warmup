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
import de.hybris.platform.commercefacades.order.VirtualEntryGroupStrategy;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.enums.GroupType;

import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;


/**
 * Default implementation of {@code VirtualEntryGroupStrategy}.
 * Creates separate root entry group for each standalone entry.
 */
public class DefaultVirtualEntryGroupStrategy implements VirtualEntryGroupStrategy
{
	private CommerceEntryGroupUtils entryGroupUtils;
	@Override
	public void createGroup(@Nonnull final List<EntryGroupData> rootGroups, @Nonnull final OrderEntryData standaloneEntry)
	{
		final int nextGroupNumber = getEntryGroupUtils().findMaxGroupNumber(rootGroups) + 1;
		final EntryGroupData virtualGroup = createVirtualGroup(standaloneEntry, nextGroupNumber);

		rootGroups.add(virtualGroup);
	}

	protected EntryGroupData createVirtualGroup(final @Nonnull OrderEntryData standaloneEntry, final int nextGroupNumber)
	{
		final EntryGroupData virtualGroup = new EntryGroupData();
		virtualGroup.setOrderEntries(Collections.singletonList(standaloneEntry));
		virtualGroup.setGroupType(GroupType.STANDALONE);
		virtualGroup.setLabel("");
		virtualGroup.setGroupNumber(nextGroupNumber);
		standaloneEntry.setEntryGroupNumbers(Collections.singletonList(virtualGroup.getGroupNumber()));
		return virtualGroup;
	}

	protected CommerceEntryGroupUtils getEntryGroupUtils()
	{
		return entryGroupUtils;
	}

	@Required
	public void setEntryGroupUtils(final CommerceEntryGroupUtils entryGroupUtils)
	{
		this.entryGroupUtils = entryGroupUtils;
	}
}
