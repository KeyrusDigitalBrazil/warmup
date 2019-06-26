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

import de.hybris.platform.acceleratorfacades.cart.action.CartEntryActionHandler;
import de.hybris.platform.acceleratorfacades.cart.action.exceptions.CartEntryActionException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.CartEntryModel;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * {@link CartEntryActionHandler} implementation for deleting cart entries.
 */
public class RemoveCartEntryActionHandler implements CartEntryActionHandler
{
	private CartFacade cartFacade;

	@Override
	public Optional<String> handleAction(final List<Long> entryNumbers) throws CartEntryActionException
	{
		validateParameterNotNullStandardMessage("entryNumbers", entryNumbers);

		// Since the entry number of the order entries might be updated after each remove of the entry, need to start removing from last entry
		final List<Long> uniqueEntryNumbers = entryNumbers.stream().distinct().collect(Collectors.toList());
		Collections.sort(uniqueEntryNumbers, Collections.reverseOrder());
		try
		{
			for (final Long entryNumber : uniqueEntryNumbers)
			{
				getCartFacade().updateCartEntry(entryNumber.longValue(), 0);
			}
		}
		catch (final CommerceCartModificationException e)
		{
			throw new CartEntryActionException("Failed to delete cart entry", e);
		}
		return Optional.empty();
	}

	@Override
	public String getSuccessMessageKey()
	{
		return "basket.page.message.remove";
	}

	@Override
	public String getErrorMessageKey()
	{
		return "basket.page.error.remove";
	}

	@Override
	public boolean supports(final CartEntryModel cartEntry)
	{
		return true;
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	@Required
	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

}
