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
import de.hybris.platform.subscriptionservices.model.OverageUsageChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.UsageChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.UsageChargeModel;

import javax.annotation.Nonnull;
import java.util.Locale;


/**
 * Interceptor to validate that there is only one {@link OverageUsageChargeEntryModel} assigned to a
 * {@link UsageChargeModel}.
 */
public class OverageUsageChargeEntryValidateInterceptor extends AbstractParentChildValidateInterceptor
{

	@Override
	public void doValidate(@Nonnull final Object model, @Nonnull final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof OverageUsageChargeEntryModel)
		{

			final OverageUsageChargeEntryModel overageUsageChargeEntry = (OverageUsageChargeEntryModel) model;
			final UsageChargeModel usageCharge = overageUsageChargeEntry.getUsageCharge();

			if (usageCharge == null)
			{
				return;
			}

			for (final UsageChargeEntryModel entry : usageCharge.getUsageChargeEntries())
			{
				if (entry == null || entry.equals(overageUsageChargeEntry))
				{
					continue;
				}
				if (entry instanceof OverageUsageChargeEntryModel)
				{
					throw new InterceptorException(
							"The usage charge \""
									+ usageCharge.getName(Locale.ENGLISH)
									+ "\" does already have an overage charge entry, please "
									+ "modify the existing one instead of creating a new one.");
				}
			}
		}
	}

}
