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
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.subscriptionservices.model.TierUsageChargeEntryModel;

import javax.annotation.Nonnull;


/**
 * Interceptor to validate that the tier start of a {@link TierUsageChargeEntryModel} instance is less that or equal to
 * its tier end.
 */
public class TierUsageChargeEntryValidateInterceptor implements ValidateInterceptor
{

	@Override
	public void onValidate(@Nonnull final Object model, @Nonnull final InterceptorContext ctx)
			throws InterceptorException
	{
		if (model instanceof TierUsageChargeEntryModel)
		{
			final TierUsageChargeEntryModel tierUsageChargeEntry = (TierUsageChargeEntryModel) model;

			if (tierUsageChargeEntry.getTierStart() > tierUsageChargeEntry.getTierEnd())
			{
				throw new InterceptorException("Tier start must be less than or equal to tier end");
			}
		}
	}
}
