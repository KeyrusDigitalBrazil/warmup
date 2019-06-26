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
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;


/**
 * This service provides functionality for sorting collections of {@link RecurringChargeEntryModel} instances by their
 * "cycleStart" attribute (ascending).
 */
public class RecurringChargeEntryModelSortService implements ModelSortService<RecurringChargeEntryModel>
{
	@Override
	@Nullable
	public List<RecurringChargeEntryModel> sort(@Nullable final Collection<RecurringChargeEntryModel> unsorted)
	{
		if (unsorted == null)
		{
			return null;
		}

		final Collection<RecurringChargeEntryModel> unsortedCopy = new ArrayList<RecurringChargeEntryModel>();
		unsortedCopy.addAll(unsorted);
		return sort(new ArrayList<RecurringChargeEntryModel>(), unsortedCopy);
	}

	private List<RecurringChargeEntryModel> sort(final List<RecurringChargeEntryModel> sorted,
			final Collection<RecurringChargeEntryModel> unsorted)
	{
		if (unsorted.isEmpty())
		{
			return sorted;
		}

		RecurringChargeEntryModel lowest = null;
		for (final RecurringChargeEntryModel entry : unsorted)
		{
			if (lowest == null || getIntValue(entry.getCycleStart()) < getIntValue(lowest.getCycleStart())
					|| getIntValue(entry.getCycleStart()) == getIntValue(lowest.getCycleStart())
					&& getIntValue(entry.getCycleEnd()) < getIntValue(lowest.getCycleEnd()))
			{
				lowest = entry;
			}
		}
		sorted.add(lowest);
		unsorted.remove(lowest);

		return sort(sorted, unsorted);
	}

	protected int getIntValue(final Integer value)
	{
		return value == null ? Integer.MAX_VALUE : value.intValue();
	}

}
