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
package de.hybris.platform.commerceservices.order.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.order.strategies.QuoteStateSelectionStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteUpdateStateStrategy;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link QuoteUpdateStateStrategy}.
 */
public class DefaultQuoteUpdateStateStrategy implements QuoteUpdateStateStrategy
{
	private QuoteStateSelectionStrategy quoteStateSelectionStrategy;

	@Override
	public QuoteModel updateQuoteState(final QuoteAction quoteAction, final QuoteModel quoteModel, final UserModel userModel)
	{
		validateParameterNotNullStandardMessage("Quote action", quoteAction);
		validateParameterNotNullStandardMessage("Quote", quoteModel);
		validateParameterNotNullStandardMessage("User", userModel);

		final Optional<QuoteState> state = getQuoteStateSelectionStrategy().getTransitionStateForAction(quoteAction, userModel);

		if (state.isPresent() && !state.get().equals(quoteModel.getState()))
		{
			quoteModel.setState(state.get());
		}

		return quoteModel;
	}

	protected QuoteStateSelectionStrategy getQuoteStateSelectionStrategy()
	{
		return quoteStateSelectionStrategy;
	}

	@Required
	public void setQuoteStateSelectionStrategy(final QuoteStateSelectionStrategy quoteStateSelectionStrategy)
	{
		this.quoteStateSelectionStrategy = quoteStateSelectionStrategy;
	}
}
