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
package de.hybris.platform.commerceservices.order.strategies;


import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.order.exceptions.IllegalQuoteStateException;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;


/**
 * Strategy that validates whether a user can perform an action on a quote.
 */
public interface QuoteActionValidationStrategy
{
	/**
	 * Checks whether a user can perform a certain action on a quote.
	 *
	 * @param quoteAction
	 *           the quote action to be performed
	 * @param quoteModel
	 *           the quote on which the action is to be performed
	 * @param userModel
	 *           the user that wants to perform the action
	 * @throws IllegalQuoteStateException
	 *            if the action cannot be performed for a quote
	 */
	void validate(QuoteAction quoteAction, QuoteModel quoteModel, UserModel userModel);

	/**
	 * Indicates whether a user can perform a certain action on a quote.
	 *
	 * @param quoteAction
	 *           the quote action to be performed
	 * @param quoteModel
	 *           the quote on which the action is to be performed
	 * @param userModel
	 *           the user that wants to perform the action
	 * @return true if the action is valid for given quote and user, false otherwise
	 */
	boolean isValidAction(QuoteAction quoteAction, QuoteModel quoteModel, UserModel userModel);
}
