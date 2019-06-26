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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;

import javax.annotation.Nonnull;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * Default implementation of {@link SubscriptionProductService}.
 */
public class DefaultSubscriptionProductService implements SubscriptionProductService
{
	@Override
	public boolean isSubscription(@Nonnull final ProductModel product)
	{
		validateParameterNotNull(product, "Product must not be null");
		return product.getSubscriptionTerm() != null;
	}
}
