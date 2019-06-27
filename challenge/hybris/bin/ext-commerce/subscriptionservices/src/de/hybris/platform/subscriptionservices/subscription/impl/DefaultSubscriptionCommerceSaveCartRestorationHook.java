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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.hook.CommerceSaveCartRestorationMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;

import javax.annotation.Nonnull;


/**
 * Subscription specific pre and post hooks for the {@link CommerceSaveCartRestorationMethodHook} method.
 */
public class DefaultSubscriptionCommerceSaveCartRestorationHook implements CommerceSaveCartRestorationMethodHook
{

	@Override
	public void beforeRestoringCart(@Nonnull final CommerceCartParameter parameters) throws CommerceCartRestorationException
	{

		validateParameterNotNull(parameters, "parameters cannot be null");

		final CartModel cartToBeRestored = parameters.getCart();

		if (null != cartToBeRestored.getParent())
		{
			throw new CommerceCartRestorationException("The provided cart [" + cartToBeRestored.getCode()
					+ "] is a child cart. Only master carts can be restored.");
		}
	}

	@Override
	public void afterRestoringCart(final CommerceCartParameter parameters) throws CommerceCartRestorationException
	{
		// default implementation doesn't do any after restoring cart logic
	}
}
