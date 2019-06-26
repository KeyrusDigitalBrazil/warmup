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
package de.hybris.platform.subscriptionservices.model.impl;

import de.hybris.platform.subscriptionservices.model.ModelSortService;
import de.hybris.platform.subscriptionservices.model.OverageUsageChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.TierUsageChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.UsageChargeEntryModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;


/**
 * This service provides functionality for sorting collections of {@link UsageChargeEntryModel} instances. It sorts
 * instances of {@link TierUsageChargeEntryModel} by their "tierStart" attribute (ascending) and adds any instance of
 * {@link OverageUsageChargeEntryModel} to the end of the sorted list.
 */
public class UsageChargeEntryModelSortService implements ModelSortService<UsageChargeEntryModel>
{
	@Override
	@Nullable
	public List<UsageChargeEntryModel> sort(@Nullable final Collection<UsageChargeEntryModel> unsorted)
	{
		if (unsorted == null)
		{
			return null;
		}

		final Collection<UsageChargeEntryModel> unsortedCopy = new ArrayList<UsageChargeEntryModel>();
		unsortedCopy.addAll(unsorted);
		return sort(new ArrayList<UsageChargeEntryModel>(), unsortedCopy);
	}

	protected List<UsageChargeEntryModel> sort(final List<UsageChargeEntryModel> sorted,
			final Collection<UsageChargeEntryModel> unsorted)
	{
		if (unsorted.isEmpty())
		{
			return sorted;
		}

		TierUsageChargeEntryModel lowestTierUsageChargeEntry = null;
		UsageChargeEntryModel nextEntry = null;

		for (final UsageChargeEntryModel entry : unsorted)
		{
			if (entry instanceof TierUsageChargeEntryModel)
			{
				final TierUsageChargeEntryModel tierUsageChargeEntry = (TierUsageChargeEntryModel) entry;
				if (isLowestTierUsageChargeEntry(lowestTierUsageChargeEntry, tierUsageChargeEntry))
				{
					lowestTierUsageChargeEntry = tierUsageChargeEntry;
					nextEntry = lowestTierUsageChargeEntry;
				}
			}
			// OverageUsageChargeEntryModel
			else if (unsorted.size() == 1) // the OverageUsageEntryModel must be added last
			{
				nextEntry = entry;
			}
		}

		if (nextEntry == null)
		{
			throw new IllegalArgumentException(
					"The passed collection to be sorted contains more than one overage usage charge entry.");
		}

		sorted.add(nextEntry);
		unsorted.remove(nextEntry);

		return sort(sorted, unsorted);
	}

	protected boolean isLowestTierUsageChargeEntry(final TierUsageChargeEntryModel lowestTierUsageChargeEntry,
			final TierUsageChargeEntryModel tierUsageChargeEntry)
	{
		return lowestTierUsageChargeEntry == null
				|| getIntValue(tierUsageChargeEntry.getTierStart()) < getIntValue(lowestTierUsageChargeEntry.getTierStart())
				|| getIntValue(tierUsageChargeEntry.getTierStart()) == getIntValue(lowestTierUsageChargeEntry.getTierStart())
				&& getIntValue(tierUsageChargeEntry.getTierEnd()) < getIntValue(lowestTierUsageChargeEntry.getTierEnd());
	}

	protected int getIntValue(final Integer value)
	{
		return value == null ? Integer.MAX_VALUE : value;
	}

}
