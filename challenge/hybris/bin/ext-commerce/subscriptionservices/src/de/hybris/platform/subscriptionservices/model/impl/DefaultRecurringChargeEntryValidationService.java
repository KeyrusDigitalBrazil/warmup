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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.subscriptionservices.enums.TermOfServiceRenewal;
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryValidationService;
import de.hybris.platform.subscriptionservices.model.SubscriptionTermModel;
import de.hybris.platform.util.localization.Localization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation class for validating recurring charge entries of a subscription price plan.
 */
public class DefaultRecurringChargeEntryValidationService implements RecurringChargeEntryValidationService
{
	private RecurringChargeEntryModelSortService sortService;

	@Override
	@Nonnull
	public Collection<String> validate(@Nullable final Collection<RecurringChargeEntryModel> recurringChargeEntries)
	{
		final Collection<String> messages = new ArrayList<String>();

		if (CollectionUtils.isEmpty(recurringChargeEntries))
		{
			return messages;
		}

		final Collection<String> gapsAndOverlaps = identifyGapsAndOverlaps(recurringChargeEntries);

		if (CollectionUtils.isNotEmpty(gapsAndOverlaps))
		{
			messages.addAll(gapsAndOverlaps);
		}

		final Collection<String> errors = validateRecurringChargeEntryAndSubscriptionTerm(recurringChargeEntries);

		if (CollectionUtils.isNotEmpty(errors))
		{
			messages.addAll(errors);
		}

		return messages;
	}

	/**
	 * Method for identifying gaps and overlaps for the set of recurring charge entries.
	 *
	 * @param recurringChargeEntries
	 * @return validation messages
	 */
	@SuppressWarnings("boxing")
	private Collection<String> identifyGapsAndOverlaps(final Collection<RecurringChargeEntryModel> recurringChargeEntries)
	{
		final Collection<String> messages = new ArrayList<String>();
		final List<RecurringChargeEntryModel> sortedEntries = getSortService().sort(recurringChargeEntries);

		for (int i = 0; i < sortedEntries.size() - 1; i++)
		{
			final RecurringChargeEntryModel entry1 = sortedEntries.get(i);
			final RecurringChargeEntryModel entry2 = sortedEntries.get(i + 1);

			final int start2 = entry2.getCycleStart().intValue();
			final int end1 = entry1.getCycleEnd() == null ? Integer.MAX_VALUE : entry1.getCycleEnd().intValue();

			// is there an overlap?
			if (start2 - end1 < 1)
			{
				if (start2 == end1)
				{
					final Object[] args = {start2};
					messages.add(Localization.getLocalizedString(
							"subscriptionservices.customvalidation.recurringcharges.overlap.oneperiod", args));
				}
				else if (end1 == Integer.MAX_VALUE)
				{
					final Object[] args = {start2};
					messages.add(Localization.getLocalizedString(
							"subscriptionservices.customvalidation.recurringcharges.overlap.oneperiodunbound", args));
				}
				else
				{
					final Object[] args = {start2, end1};
					messages.add(Localization.getLocalizedString("subscriptionservices.customvalidation.recurringcharges.overlap",
							args));
				}
			}
			// is there a gap
			if (start2 - end1 > 1)
			{
				final Object[] args = {end1, start2};
				messages.add(Localization.getLocalizedString("subscriptionservices.customvalidation.recurringcharges.gap", args));
			}
		}

		return messages;
	}

	/**
	 * Method for validating recurring charge entries against the subscription term. 1. the duration of all recurring
	 * charge entries should not be lower than the term of service 2. the cycle start of a recurring charge entry should
	 * not exceed the term of service 3. the cycle end of a recurring charge entry should not exceed the term of service
	 *
	 * @param recurringChargeEntries
	 * @return validation messages
	 */
	@SuppressWarnings("boxing")
	private Collection<String> validateRecurringChargeEntryAndSubscriptionTerm(
			final Collection<RecurringChargeEntryModel> recurringChargeEntries)
	{
		final Collection<String> errors = new ArrayList<String>();
		final ArrayList<RecurringChargeEntryModel> rcEntries = new ArrayList(recurringChargeEntries);

		if (CollectionUtils.isEmpty(rcEntries))
		{
			return errors;
		}

		final List<RecurringChargeEntryModel> recurringChargeslist = new ArrayList<RecurringChargeEntryModel>(
				recurringChargeEntries);
		final ProductModel subscriptionProduct = recurringChargeslist.get(0).getSubscriptionPricePlanRecurring().getProduct();
		final SubscriptionTermModel term = subscriptionProduct.getSubscriptionTerm();

		// check, if termOfServiceNumber is not reached
		final RecurringChargeEntryModel lastRecurringChargeEntry = recurringChargeslist.get(recurringChargeEntries.size() - 1);
		if (lastRecurringChargeEntry.getCycleEnd() == null || term.getTermOfServiceNumber() == null)
		{
			return errors;
		}
		addErrorIfTermOfServiceNumberNotReached(errors, term, lastRecurringChargeEntry);

		// check, if termOfServiceNumber is exceeded
		if (term.getTermOfServiceNumber() == null || !TermOfServiceRenewal.NON_RENEWING.equals(term.getTermOfServiceRenewal()))
		{
			return errors;
		}

		for (final RecurringChargeEntryModel rcEntry : rcEntries)
		{
			addErrorIfTermOfServiceNumberExceeded(errors, term, rcEntry);
		}

		return errors;
	}

	protected void addErrorIfTermOfServiceNumberExceeded(final Collection<String> errors, final SubscriptionTermModel term,
			final RecurringChargeEntryModel rcEntry)
	{
		if (!StringUtils.equals(rcEntry.getBillingTime().getCode(), term.getTermOfServiceFrequency().getCode()))
		{
			return;
		}

		if (rcEntry.getCycleStart() != null && rcEntry.getCycleStart() > term.getTermOfServiceNumber())
		{

			final Object[] args =
			{ Localization.getLocalizedString("type.RecurringChargeEntry.cycleStart.name"), rcEntry.getCycleStart(),
					term.getTermOfServiceNumber() };
			errors.add(Localization.getLocalizedString("subscriptionservices.customvalidation.termofservicenumber.exceeded", args));
		}

		if (rcEntry.getCycleEnd() != null && rcEntry.getCycleEnd() > term.getTermOfServiceNumber())
		{
			final Object[] args =
			{ Localization.getLocalizedString("type.RecurringChargeEntry.cycleEnd.name"), rcEntry.getCycleEnd(),
					term.getTermOfServiceNumber() };
			errors.add(Localization.getLocalizedString("subscriptionservices.customvalidation.termofservicenumber.exceeded", args));
		}
	}

	protected void addErrorIfTermOfServiceNumberNotReached(final Collection<String> errors, final SubscriptionTermModel term,
			final RecurringChargeEntryModel lastRecurringChargeEntry)
	{
		if (lastRecurringChargeEntry.getCycleEnd() != -1
				&& lastRecurringChargeEntry.getCycleEnd() < term.getTermOfServiceNumber()
				&& StringUtils
						.equals(lastRecurringChargeEntry.getBillingTime().getCode(), term.getTermOfServiceFrequency().getCode()))
		{
			final Object[] args =
			{ lastRecurringChargeEntry.getCycleEnd(), term.getTermOfServiceNumber() };
			errors.add(Localization.getLocalizedString("subscriptionservices.customvalidation.termofservicenumber.notreached", args));
		}
	}

	protected RecurringChargeEntryModelSortService getSortService()
	{
		return sortService;
	}

	@Required
	public void setSortService(final RecurringChargeEntryModelSortService sortService)
	{
		this.sortService = sortService;
	}

}
