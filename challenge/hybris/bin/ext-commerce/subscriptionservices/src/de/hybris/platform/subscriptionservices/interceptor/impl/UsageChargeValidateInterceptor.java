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
package de.hybris.platform.subscriptionservices.interceptor.impl;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.UsageChargeModel;

import javax.annotation.Nonnull;
import java.util.Locale;


/**
 * Interceptor to validate that the {@link UsageChargeModel}'s parent objects are marked as modified.
 */
public class UsageChargeValidateInterceptor extends AbstractParentChildValidateInterceptor
{

	@Override
	public void doValidate(@Nonnull final Object model, @Nonnull final InterceptorContext ctx)
			throws InterceptorException
	{
		if (model instanceof UsageChargeModel)
		{
			final UsageChargeModel toValidate = (UsageChargeModel) model;
			final SubscriptionPricePlanModel pricePlan = toValidate.getSubscriptionPricePlanUsage();

			if (pricePlan == null)
			{
				return;
			}

			for (final UsageChargeModel usageCharge : pricePlan.getUsageCharges())
			{
				if (usageCharge.equals(toValidate))
				{
					continue;
				}

				if (usageCharge.getUsageUnit().equals(toValidate.getUsageUnit()))
				{
					throw new InterceptorException("A usage charge with unit "
							+ usageCharge.getUsageUnit().getName(Locale.ENGLISH)
							+ " is already assigned to the price plan, please modify "
							+ "the existing one instead of creating a second one.");
				}
			}

		}
	}

}
