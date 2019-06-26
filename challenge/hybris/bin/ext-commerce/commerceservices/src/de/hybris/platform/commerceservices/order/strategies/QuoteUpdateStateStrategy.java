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
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;


/**
 * Strategy to update the state of a quote on an action
 */
public interface QuoteUpdateStateStrategy
{
	/**
	 * Updates the state of the quote for the given action.
	 *
	 * @param quoteAction
	 *           the quote action to be performed
	 * @param quoteModel
	 *           the quote for which to update the state
	 * @param userModel
	 *           the user who wants to update quote state
	 * @return {@link QuoteModel} the updated quote model
	 * @throws IllegalArgumentException
	 *            if any of the parameters is null
	 */
	QuoteModel updateQuoteState(QuoteAction quoteAction, QuoteModel quoteModel, UserModel userModel);
}
