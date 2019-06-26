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
package de.hybris.platform.sap.c4c.quote.inbound.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteVersionControlHelper;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;


/**
 * Helper Class implementation for updating the quote version in inbound scenarios
 */
public class DefaultInboundQuoteVersionControlHelper implements InboundQuoteVersionControlHelper
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultInboundQuoteVersionControlHelper.class);

	private QuoteService quoteService;

	public QuoteService getQuoteService()
	{
		return quoteService;
	}

	public void setQuoteService(QuoteService quoteService)
	{
		this.quoteService = quoteService;
	}

	@Override
	public QuoteModel getQuoteforCode(String code)
	{
		QuoteModel quote = null;
		try
		{
			quote = getQuoteService().getCurrentQuoteForCode(code);
		}
		catch (ModelNotFoundException e)
		{
			LOG.info("No existing quote found with this code " + code + " Creating new quote with quoteId :"+ code );
			return null;
		}

		return quote;
	}

	@Override
	public Integer getUpdatedVersionNumber(QuoteModel quote)
	{
		return Integer.valueOf(quote.getVersion().intValue() + 1);
	}

}
