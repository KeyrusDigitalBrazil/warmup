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

import de.hybris.platform.subscriptionservices.model.OneTimeChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.OneTimeChargeEntryValidationService;
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryModel;
import de.hybris.platform.util.localization.Localization;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Implementation class for validating recurring charge entries of a subscription price plan.
 */
public class DefaultOneTimeChargeEntryValidationService implements OneTimeChargeEntryValidationService
{
	public static final String ON_FIRST_BILL = "onfirstbill";


	@Override
	@Nonnull
	public Collection<String> validate(@Nullable final Collection<OneTimeChargeEntryModel> oneTimeChargeEntries)
	{
		final Collection<String> messages = new ArrayList<String>();

		if (CollectionUtils.isEmpty(oneTimeChargeEntries))
		{
			return messages;
		}

		final Collection<String> errors = validateOneTimeAndSubscriptionTerm(oneTimeChargeEntries);

		if (CollectionUtils.isNotEmpty(errors))
		{
			messages.addAll(errors);
		}

		return messages;
	}

	/**
	 * Checks whether an onFirstBill one time charge is defined but no recurring charge entry exists.
	 * 
	 * @param oneTimeChargeEntries
	 *           collection of one time charge entries to check
	 * @return collection of validation messages, may be empty
	 */
	private Collection<String> validateOneTimeAndSubscriptionTerm(final Collection<OneTimeChargeEntryModel> oneTimeChargeEntries)
	{
		final Collection<String> messages = new ArrayList<String>();

		for (final OneTimeChargeEntryModel otcEntry : oneTimeChargeEntries)
		{
			final Collection<RecurringChargeEntryModel> recurringChargeEntries = otcEntry.getSubscriptionPricePlanOneTime()
					.getRecurringChargeEntries();

			if (ON_FIRST_BILL.equalsIgnoreCase(otcEntry.getBillingEvent().getCode())
					&& CollectionUtils.isEmpty(recurringChargeEntries))
			{
				final Object[] args = {};
				messages.add(Localization.getLocalizedString(
						"subscriptionservices.customvalidation.onetimecharges.onfirstbill.norecurringcharge", args));
			}
		}

		return messages;
	}
}
