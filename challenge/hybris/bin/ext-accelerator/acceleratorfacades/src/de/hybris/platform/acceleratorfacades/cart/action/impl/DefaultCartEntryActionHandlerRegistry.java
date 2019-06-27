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
package de.hybris.platform.acceleratorfacades.cart.action.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.acceleratorfacades.cart.action.CartEntryAction;
import de.hybris.platform.acceleratorfacades.cart.action.CartEntryActionHandler;
import de.hybris.platform.acceleratorfacades.cart.action.CartEntryActionHandlerRegistry;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Registry for {@link CartEntryActionHandler} implementations.
 */
public class DefaultCartEntryActionHandlerRegistry implements CartEntryActionHandlerRegistry
{
	private Map<CartEntryAction, CartEntryActionHandler> cartEntryActionHandlerMap;

	@Override
	public CartEntryActionHandler getHandler(final CartEntryAction cartEntryAction)
	{
		validateParameterNotNullStandardMessage("cartEntryAction", cartEntryAction);
		return getCartEntryActionHandlerMap().get(cartEntryAction);
	}

	protected Map<CartEntryAction, CartEntryActionHandler> getCartEntryActionHandlerMap()
	{
		return cartEntryActionHandlerMap;
	}

	@Required
	public void setCartEntryActionHandlerMap(final Map<CartEntryAction, CartEntryActionHandler> cartEntryActionHandlerMap)
	{
		this.cartEntryActionHandlerMap = cartEntryActionHandlerMap;
	}

}
