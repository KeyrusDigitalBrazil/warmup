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
package de.hybris.platform.subscriptionservices.subscription.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.order.impl.AbstractCommerceCartStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionCommerceCartStrategy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Temporary solution until the DefaultSubscriptionCommmerceCartService is adjusted to the pre/post-hook refactoring.
 * Concrete implementation of the abstract class {@link AbstractCommerceCartStrategy} in order to expose some helper
 * methods that have been moved from the DefaultCommerceCartService to the {@link AbstractCommerceCartStrategy}.
 */
public class DefaultSubscriptionCommerceCartStrategy extends AbstractCommerceCartStrategy implements
		SubscriptionCommerceCartStrategy
{

	@Override
	public void normalizeEntryNumbers(@Nonnull final CartModel cartModel)
	{
		validateParameterNotNullStandardMessage("cartModel", cartModel);
		super.normalizeEntryNumbers(cartModel);
	}

	@Override
	@Nullable
	public AbstractOrderEntryModel getEntryForNumber(@Nonnull final AbstractOrderModel order, final int number)
	{
		validateParameterNotNullStandardMessage("order", order);
		return super.getEntryForNumber(order, number);
	}

	@Override
	public long getAvailableStockLevel(@Nonnull final ProductModel productModel,@Nullable final PointOfServiceModel pointOfServiceModel)
	{
		return super.getAvailableStockLevel(productModel, pointOfServiceModel);
	}

	@Override
	public long getForceInStockMaxQuantity()
	{
		return super.getForceInStockMaxQuantity();
	}
}
