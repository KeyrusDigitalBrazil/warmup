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

import de.hybris.platform.subscriptionservices.model.OverageUsageChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.TierUsageChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.UsageChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.UsageChargeModel;
import de.hybris.platform.subscriptionservices.model.UsageChargeValidationService;
import de.hybris.platform.util.localization.Localization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * DefaultUsageChargeValidationService.
 */
public class DefaultUsageChargeValidationService implements UsageChargeValidationService
{
	private UsageChargeEntryModelSortService sortService;

	@Override
	@Nonnull
	public Collection<String> validate(@Nullable final Collection<UsageChargeModel> usageCharges)
	{
		final Collection<String> messages = new ArrayList<String>();

		if (usageCharges == null)
		{
			return messages;
		}

		for (final UsageChargeModel usageCharge : usageCharges)
		{
			final Collection<UsageChargeEntryModel> usageChargeEntries = usageCharge.getUsageChargeEntries();

			if (CollectionUtils.isEmpty(usageChargeEntries))
			{
				final Object[] args = { usageCharge.getName() };
				messages.add(Localization.getLocalizedString("subscriptionservices.customvalidation.usagecharges.noentries", args));
				continue;
			}

			final List<UsageChargeEntryModel> sortedEntries = getSortService().sort(usageChargeEntries);

			// we don't need the overage usage charge entry to check for gaps or overlaps c
			removeOverageUsageChargeEntry(sortedEntries);

			for (int i = 0; i < sortedEntries.size() - 1; i++)
			{
				final TierUsageChargeEntryModel entry1 = (TierUsageChargeEntryModel) sortedEntries.get(i);
				final TierUsageChargeEntryModel entry2 = (TierUsageChargeEntryModel) sortedEntries.get(i + 1);

				addMessageIfEntriesOverlap(messages, usageCharge, entry1, entry2);
				addMesaageIfGapBetweenEntries(messages, usageCharge, entry1, entry2);
			}
		}

		return messages;
	}

	protected void removeOverageUsageChargeEntry(final List<UsageChargeEntryModel> sortedEntries)
	{
		final int lastIndex = sortedEntries.size() - 1;
		if (sortedEntries.get(lastIndex) instanceof OverageUsageChargeEntryModel)
		{
			sortedEntries.remove(lastIndex);
		}
	}

	protected void addMesaageIfGapBetweenEntries(final Collection<String> messages, final UsageChargeModel usageCharge,
			final TierUsageChargeEntryModel entry1, final TierUsageChargeEntryModel entry2)
	{
		final int start2 = entry2.getTierStart();
		final int end1 = entry1.getTierEnd() == null ? Integer.MAX_VALUE : entry1.getTierEnd();
		if (start2 - end1 > 1)
		{
			final Object[] args = { usageCharge.getName(), end1, start2 };
			messages.add(Localization.getLocalizedString("subscriptionservices.customvalidation.usagecharges.gap", args));
		}
	}

	protected void addMessageIfEntriesOverlap(final Collection<String> messages, final UsageChargeModel usageCharge,
			final TierUsageChargeEntryModel entry1, final TierUsageChargeEntryModel entry2)
	{
		final int start2 = entry2.getTierStart();
		final int end1 = entry1.getTierEnd() == null ? Integer.MAX_VALUE : entry1.getTierEnd();
		if (start2 - end1 < 1)
		{
			if (start2 == end1)
			{
				final Object[] args = { usageCharge.getName(), start2 };
				messages.add(Localization.getLocalizedString("subscriptionservices.customvalidation.usagecharges.overlap.onetier",
						args));
			}
			else
			{
				final Object[] args = { usageCharge.getName(), start2, end1 };
				messages.add(Localization.getLocalizedString("subscriptionservices.customvalidation.usagecharges.overlap", args));

			}
		}
	}

	protected UsageChargeEntryModelSortService getSortService()
	{
		return sortService;
	}

	@Required
	public void setSortService(final UsageChargeEntryModelSortService sortService)
	{
		this.sortService = sortService;
	}

}
