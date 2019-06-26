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
package de.hybris.platform.subscriptionservices.model;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;


/**
 * 
 * AttributeHandler for dynamic attribute ChargeEntry.currency.
 * 
 */
public class CurrencyChargeEntryAttribute extends AbstractDynamicAttributeHandler<CurrencyModel, ChargeEntryModel>
{
	@Override
	public CurrencyModel get(final ChargeEntryModel model)
	{
		SubscriptionPricePlanModel subscriptionPriceRowModel = null;
		if (model instanceof OneTimeChargeEntryModel)
		{
			subscriptionPriceRowModel = ((OneTimeChargeEntryModel) model).getSubscriptionPricePlanOneTime();
		}
		else if (model instanceof RecurringChargeEntryModel)
		{
			subscriptionPriceRowModel = ((RecurringChargeEntryModel) model).getSubscriptionPricePlanRecurring();
		}
		else if (model instanceof UsageChargeEntryModel)
		{
			final UsageChargeModel usageChargeModel = ((UsageChargeEntryModel) model).getUsageCharge();
			if (usageChargeModel != null)
			{
				subscriptionPriceRowModel = usageChargeModel.getSubscriptionPricePlanUsage();
			}
		}

		if (subscriptionPriceRowModel != null)
		{
			return subscriptionPriceRowModel.getCurrency();
		}

		return null;
	}

	@Override
	public void set(final ChargeEntryModel model, final CurrencyModel value)
	{
		super.set(model, value);
	}

}
