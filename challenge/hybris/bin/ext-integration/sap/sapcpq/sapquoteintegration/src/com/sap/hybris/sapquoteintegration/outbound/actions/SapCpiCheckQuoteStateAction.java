/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.sapquoteintegration.outbound.actions;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;


/**
 *
 */
public class SapCpiCheckQuoteStateAction extends AbstractSimpleDecisionAction<QuoteProcessModel>
{
	private QuoteService quoteService;

	@Override
	public Transition executeAction(final QuoteProcessModel process)
	{

		if (process.getQuoteCode() != null)
		{
			final QuoteModel quote = getQuoteService().getCurrentQuoteForCode(process.getQuoteCode());
			if (QuoteState.CANCELLED.equals(quote.getState()))
			{
				return Transition.NOK;
			}
			else if (QuoteState.BUYER_ORDERED.equals(quote.getState()))
			{
				return Transition.OK;
			}
		}
		return null;
	}

	public QuoteService getQuoteService()
	{
		return quoteService;
	}

	@Required
	public void setQuoteService(final QuoteService quoteService)
	{
		this.quoteService = quoteService;
	}

}
