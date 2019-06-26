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
package de.hybris.platform.subscriptionservices.price;

import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.subscriptionservices.model.BillingEventModel;
import de.hybris.platform.subscriptionservices.model.OneTimeChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.NotImplementedException;


/**
 *
 * Extends the {@link CommercePriceService} in order to retrieve the applicable {@link SubscriptionPricePlanModel} for a
 * product as the {@link CommercePriceService} returns the price information only. The standard filtering functionality
 * for {@link PriceRowModel}s of the active PriceFactory is used.
 *
 * @spring.bean subscriptionCommercePriceService
 */
public interface SubscriptionCommercePriceService extends CommercePriceService
{

	/**
	 *
	 * @deprecated Since 5.9.9 Use
	 *             {@link SubscriptionCommercePriceService#getSubscriptionPricePlanForProduct(ProductModel)} instead.
	 *
	 * @param subscriptionProduct
	 *           product
	 * @return product's price plan
	 */
	@Deprecated
	@Nullable
	SubscriptionPricePlanModel getSubscriptionPricePlanForProduct(SubscriptionProductModel subscriptionProduct);

	/**
	 * Search a {@link SubscriptionPricePlanModel} for the given <code>subscriptionProduct</code>.
	 *
	 * @param subscriptionProduct
	 *           the {@link SubscriptionProductModel}
	 * @return the applicable {@link SubscriptionPricePlanModel} or null
	 */
	@Nullable
	default SubscriptionPricePlanModel getSubscriptionPricePlanForProduct(@Nonnull ProductModel subscriptionProduct)
	{
		throw new NotImplementedException("Method SubscriptionPricePlanModel#getSubscriptionPricePlanForProduct is not implememted");
	}

	/**
	 * Search a {@link SubscriptionPricePlanModel} for the given <code>entry</code>'s product.
	 *
	 * @param entry
	 *           the {@link AbstractOrderEntryModel}
	 * @return the applicable {@link SubscriptionPricePlanModel} or null
	 */
	@Nullable
	SubscriptionPricePlanModel getSubscriptionPricePlanForEntry(AbstractOrderEntryModel entry);

	/**
	 * Returns the first recurring price of the given {@link SubscriptionPricePlanModel}. It is assumed that the
	 * recurring prices are sorted in a way so that the price increases as time goes by. Therefore, the returned
	 * recurring price is the lowest price. If price logic is different this function must be overridden.
	 *
	 * @param pricePlan
	 *           {@link SubscriptionPricePlanModel}
	 * @return the first recurring price of the given <code>pricePlan</code> or null
	 */
	@Nullable
	RecurringChargeEntryModel getFirstRecurringPriceFromPlan(@Nullable final SubscriptionPricePlanModel pricePlan);

	/**
	 * Returns the last recurring price of the given {@link SubscriptionPricePlanModel}. It is assumed that the recurring
	 * prices are sorted in a way so that the price increases as time goes by. Therefore, the returned recurring price is
	 * the highest price. If price logic is different this function must be overridden.
	 *
	 * @param pricePlan
	 *           {@link SubscriptionPricePlanModel}
	 * @return the last recurring price of the given <code>pricePlan</code> or null
	 */
	@Nullable
	RecurringChargeEntryModel getLastRecurringPriceFromPlan(@Nullable final SubscriptionPricePlanModel pricePlan);

	/**
	 * Returns the one time charge entry of the given {@link SubscriptionPricePlanModel}.
	 *
	 * @param pricePlan
	 *           {@link SubscriptionPricePlanModel}
	 * @param billingEvent
	 *           {@link BillingEventModel}
	 * @return the OneTimeChargeEntry
	 */
	@Nullable
	OneTimeChargeEntryModel getOneTimeChargeEntryPlan(@Nonnull final SubscriptionPricePlanModel pricePlan, 
			@Nonnull final BillingEventModel billingEvent);
}
