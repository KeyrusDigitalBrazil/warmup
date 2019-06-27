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
package de.hybris.platform.commerceservices.stock.impl;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Overrides the base class to set infinite stock level for subscription products.
 * <p>
 *    Subscription products are not physical, so there is unlimited quantity available.
 * </p>
 *
 * @see SubscriptionProductService
 */
public class SubscriptionCommerceStockService extends DefaultCommerceStockService
{
	private SubscriptionProductService subscriptionProductService;


	@Override
	public StockLevelStatus getStockLevelStatusForProductAndBaseStore(final ProductModel product, final BaseStoreModel baseStore)
	{
		if (getSubscriptionProductService().isSubscription(product))
		{
			return StockLevelStatus.INSTOCK;
		}
		return super.getStockLevelStatusForProductAndBaseStore(product, baseStore);
	}

	@Override
	public Long getStockLevelForProductAndBaseStore(final ProductModel product, final BaseStoreModel baseStore)
	{
		if (getSubscriptionProductService().isSubscription(product))
		{
			return null;
		}
		return super.getStockLevelForProductAndBaseStore(product, baseStore);
	}

	@Override
	public StockLevelStatus getStockLevelStatusForProductAndPointOfService(final ProductModel product,
			final PointOfServiceModel pointOfService)
	{
		if (getSubscriptionProductService().isSubscription(product))
		{
			return StockLevelStatus.INSTOCK;
		}
		return super.getStockLevelStatusForProductAndPointOfService(product, pointOfService);
	}


	protected SubscriptionProductService getSubscriptionProductService()
	{
		return subscriptionProductService;
	}

	@Override
	public Long getStockLevelForProductAndPointOfService(final ProductModel product, final PointOfServiceModel pointOfService)
	{
		if (getSubscriptionProductService().isSubscription(product))
		{
			return null;
		}
		return super.getStockLevelForProductAndPointOfService(product, pointOfService);
	}


	@Required
	public void setSubscriptionProductService(final SubscriptionProductService subscriptionProductService)
	{
		this.subscriptionProductService = subscriptionProductService;
	}
}
