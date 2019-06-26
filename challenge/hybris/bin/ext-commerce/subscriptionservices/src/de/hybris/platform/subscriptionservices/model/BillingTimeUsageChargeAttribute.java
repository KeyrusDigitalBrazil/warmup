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


import de.hybris.platform.core.model.product.ProductModel;

/**
 *
 * AttributeHandler for dynamic attribute UsageCharge.billingFrequency.
 *
 */
public class BillingTimeUsageChargeAttribute extends SubscriptionAwareAttributeHandler<BillingTimeModel, UsageChargeModel>
{
	@Override
	public BillingTimeModel get(final UsageChargeModel model)
	{
		final SubscriptionPricePlanModel subscriptionPricePlanModel = model.getSubscriptionPricePlanUsage();

		if (subscriptionPricePlanModel != null &&
				getSubscriptionProductService().isSubscription(subscriptionPricePlanModel.getProduct()))
		{
			final ProductModel subscriptionProduct = subscriptionPricePlanModel.getProduct();

			if (subscriptionProduct.getSubscriptionTerm() != null
					&& subscriptionProduct.getSubscriptionTerm().getBillingPlan() != null)
			{
				return subscriptionProduct.getSubscriptionTerm().getBillingPlan().getBillingFrequency();
			}
		}

		return null;
	}

	@Override
	public void set(final UsageChargeModel model, final BillingTimeModel value)
	{
		super.set(model, value);
	}

}
