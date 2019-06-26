/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.c4c.quote.actions;

import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubHelper;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubResult;

import org.apache.log4j.Logger;


/**
 *
 */
public class C4CCheckQuoteAction extends AbstractSimpleDecisionAction<QuoteProcessModel>
{
	private static final Logger LOG = Logger.getLogger(C4CCheckQuoteAction.class);

	private SendToDataHubHelper<QuoteModel> sendQuoteToDataHubHelper;

	private QuoteService quoteService;

	@Override
	public Transition executeAction(final QuoteProcessModel process)
	{
		LOG.info("New Process for Quote");
		if (process.getQuoteCode() != null)
		{

			final QuoteModel model = quoteService.getCurrentQuoteForCode(process.getQuoteCode());
			final SendToDataHubResult result = sendQuoteToDataHubHelper.createAndSendRawItem(model);
			if (result.isSuccess())
			{
				return Transition.OK;
			}
		}
		return Transition.NOK;
	}

	public SendToDataHubHelper<QuoteModel> getSendQuoteToDataHubHelper()
	{
		return sendQuoteToDataHubHelper;
	}

	public void setSendQuoteToDataHubHelper(final SendToDataHubHelper<QuoteModel> sendQuoteToDataHubHelper)
	{
		this.sendQuoteToDataHubHelper = sendQuoteToDataHubHelper;
	}

	public QuoteService getQuoteService()
	{
		return quoteService;
	}

	public void setQuoteService(final QuoteService quoteService)
	{
		this.quoteService = quoteService;
	}

}
