/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.subscriptionservices.interceptor.impl;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.subscriptionservices.model.BillingEventModel;
import de.hybris.platform.subscriptionservices.model.OneTimeChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;

import javax.annotation.Nonnull;
import java.util.Locale;


/**
 * Interceptor to validate that a {@link OneTimeChargeEntryModel} with a specific {@link BillingEventModel} is assigned
 * to a {@link SubscriptionPricePlanModel} only once.
 */
public class OneTimeChargeEntryValidateInterceptor extends AbstractParentChildValidateInterceptor
{
	@Override
	public void doValidate(@Nonnull final Object model, @Nonnull final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof OneTimeChargeEntryModel)
		{
			final OneTimeChargeEntryModel toValidate = (OneTimeChargeEntryModel) model;

			if (toValidate.getBillingEvent() == null)
			{
				// billing event has modifier optional="false" and thus the model will not be saved subsequently
				return;
			}

			final SubscriptionPricePlanModel subscriptionPricePlan = toValidate.getSubscriptionPricePlanOneTime();

			if (subscriptionPricePlan == null)
			{
				return;
			}

			for (final OneTimeChargeEntryModel entry : subscriptionPricePlan.getOneTimeChargeEntries())
			{
				if (!toValidate.equals(entry) && toValidate.getBillingEvent().equals(entry.getBillingEvent()))
				{
					throw new InterceptorException(
							"A one-time charge with billing event \""
									+ toValidate.getBillingEvent().getNameInCart(Locale.ENGLISH)
									+ "\" is already assigned to the price plan, please modify the "
									+ "existing one-time charge instead of assigning a second one.");
				}
			}
		}
	}
}
