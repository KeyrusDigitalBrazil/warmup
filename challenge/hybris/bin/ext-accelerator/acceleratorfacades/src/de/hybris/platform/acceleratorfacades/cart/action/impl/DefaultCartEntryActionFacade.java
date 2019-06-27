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

import de.hybris.platform.acceleratorfacades.cart.action.CartEntryAction;
import de.hybris.platform.acceleratorfacades.cart.action.CartEntryActionFacade;
import de.hybris.platform.acceleratorfacades.cart.action.CartEntryActionHandler;
import de.hybris.platform.acceleratorfacades.cart.action.CartEntryActionHandlerRegistry;
import de.hybris.platform.acceleratorfacades.cart.action.exceptions.CartEntryActionException;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link CartEntryActionFacade} interface.
 */
public class DefaultCartEntryActionFacade implements CartEntryActionFacade
{
	private CartEntryActionHandlerRegistry cartEntryActionHandlerRegistry;

	@Override
	public Optional<String> executeAction(final CartEntryAction action, final List<Long> entryNumbers)
			throws CartEntryActionException
	{
		final CartEntryActionHandler handler = getCartEntryActionHandlerRegistry().getHandler(action);
		if (handler == null)
		{
			throw new CartEntryActionException(String.format("No handler found for action %s", action));
		}
		return handler.handleAction(entryNumbers);
	}

	@Override
	public Optional<String> getSuccessMessageKey(final CartEntryAction action)
	{
		final CartEntryActionHandler handler = getCartEntryActionHandlerRegistry().getHandler(action);
		if (handler == null || StringUtils.isEmpty(handler.getSuccessMessageKey()))
		{
			return Optional.empty();
		}
		return Optional.of(handler.getSuccessMessageKey());
	}

	@Override
	public Optional<String> getErrorMessageKey(final CartEntryAction action)
	{
		final CartEntryActionHandler handler = getCartEntryActionHandlerRegistry().getHandler(action);
		if (handler == null || StringUtils.isEmpty(handler.getErrorMessageKey()))
		{
			return Optional.empty();
		}
		return Optional.of(handler.getErrorMessageKey());
	}

	protected CartEntryActionHandlerRegistry getCartEntryActionHandlerRegistry()
	{
		return cartEntryActionHandlerRegistry;
	}

	@Required
	public void setCartEntryActionHandlerRegistry(final CartEntryActionHandlerRegistry cartEntryActionHandlerRegistry)
	{
		this.cartEntryActionHandlerRegistry = cartEntryActionHandlerRegistry;
	}

}
