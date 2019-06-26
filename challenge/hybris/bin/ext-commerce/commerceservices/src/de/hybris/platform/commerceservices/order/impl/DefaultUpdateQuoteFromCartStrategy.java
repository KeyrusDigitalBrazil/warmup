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
package de.hybris.platform.commerceservices.order.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.order.UpdateQuoteFromCartStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.strategies.impl.GenericAbstractOrderCloningStrategy;

import java.util.Optional;


/**
 * Default implementation of {@link de.hybris.platform.commerceservices.order.UpdateQuoteFromCartStrategy}
 */
public class DefaultUpdateQuoteFromCartStrategy
		extends GenericAbstractOrderCloningStrategy<QuoteModel, QuoteEntryModel, CartModel> implements UpdateQuoteFromCartStrategy
{

	public DefaultUpdateQuoteFromCartStrategy()
	{
		super(QuoteModel.class, QuoteEntryModel.class, CartModel.class);
	}

	@Override
	public QuoteModel updateQuoteFromCart(final CartModel cart)
	{
		validateParameterNotNullStandardMessage("cart", cart);

		final QuoteModel outdatedQuote = getQuoteForCart(cart);
		final QuoteModel updatedQuote = clone(cart, Optional.of(outdatedQuote.getCode()));

		updatedQuote.setVersion(outdatedQuote.getVersion());
		updatedQuote.setState(outdatedQuote.getState());
		updatedQuote.setPreviousEstimatedTotal(outdatedQuote.getPreviousEstimatedTotal());

		postProcess(cart, updatedQuote);

		return updatedQuote;
	}

	@Override
	protected void postProcess(final CartModel original, final QuoteModel copy)
	{
		copy.setCartReference(original);
		original.setQuoteReference(copy);
	}

	protected QuoteModel getQuoteForCart(final CartModel cart)
	{
		if (cart.getQuoteReference() == null)
		{
			throw new IllegalStateException(
					"Unable to update quote since cart is not created from a quote. Cart code: " + cart.getCode());
		}
		return cart.getQuoteReference();
	}
}
