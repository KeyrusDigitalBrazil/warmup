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
package de.hybris.platform.selectivecartsplitlistaddon.cart.action.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.acceleratorfacades.cart.action.CartEntryActionHandler;
import de.hybris.platform.acceleratorfacades.cart.action.exceptions.CartEntryActionException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.selectivecartfacades.SelectiveCartFacade;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Deals with cart entries when moving them into Save for Later
 */
public class SaveForLaterActionHandler implements CartEntryActionHandler
{
	private SelectiveCartFacade selectiveCartFacade;

	/**
	 * Moves selected entries into Save for Later
	 * 
	 * @param entryNumbers
	 *           the list of entry numbers
	 * @return the empty optional
	 * @throws CartEntryActionException
	 *            throws when moving product to Save for Later failed
	 */
	@Override
	public Optional<String> handleAction(final List<Long> entryNumbers) throws CartEntryActionException
	{
		validateParameterNotNullStandardMessage("entryNumbers", entryNumbers);

		final List<Long> uniqueEntryNumbers = entryNumbers.stream().distinct().collect(Collectors.toList());
		Collections.sort(uniqueEntryNumbers, Collections.reverseOrder());
		try
		{
			for (final Long entryNumber : uniqueEntryNumbers)
			{
				getSelectiveCartFacade().addToWishlistFromCart(Integer.valueOf(entryNumber.intValue()));
			}
		}
		catch (final CommerceCartModificationException e)
		{
			throw new CartEntryActionException("Failed to move the product to Save for Later", e);
		}
		return Optional.empty();

	}

	@Override
	public String getSuccessMessageKey()
	{
		return "basket.page.message.saveforlater";
	}

	@Override
	public String getErrorMessageKey()
	{
		return "basket.page.error.saveforlater";
	}

	@Override
	public boolean supports(final CartEntryModel cartEntry)
	{
		return true;
	}


	protected SelectiveCartFacade getSelectiveCartFacade()
	{
		return selectiveCartFacade;
	}

	@Required
	public void setSelectiveCartFacade(final SelectiveCartFacade selectiveCartFacade)
	{
		this.selectiveCartFacade = selectiveCartFacade;
	}

}
