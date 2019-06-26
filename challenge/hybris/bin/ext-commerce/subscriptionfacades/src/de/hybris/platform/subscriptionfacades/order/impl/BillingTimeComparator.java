package de.hybris.platform.subscriptionfacades.order.impl;

import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;

import java.util.Comparator;

import javax.annotation.Nonnull;

/**
 * Comparator for sorting BillingTime based on the sequence.
 */
public class BillingTimeComparator implements Comparator<BillingTimeModel> 
{
	@Override
	public int compare(@Nonnull final BillingTimeModel billingTime0, @Nonnull final BillingTimeModel billingTime1) 
	{
		ServicesUtil.validateParameterNotNullStandardMessage("billingTime0", billingTime0);
		ServicesUtil.validateParameterNotNullStandardMessage("billingTime1", billingTime1);

		if (billingTime0 == null || billingTime0.getOrder() == null) 
		{
			return 1;
		}

		if (billingTime1 == null || billingTime1.getOrder() == null) 
		{
			return -1;
		}

		return billingTime0.getOrder().compareTo(billingTime1.getOrder());
	}
}
