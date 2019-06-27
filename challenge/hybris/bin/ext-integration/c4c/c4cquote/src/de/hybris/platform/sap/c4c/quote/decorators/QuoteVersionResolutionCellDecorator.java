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
package de.hybris.platform.sap.c4c.quote.decorators;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteVersionControlHelper;
import de.hybris.platform.util.CSVCellDecorator;


/**
 * Decorator class for importing the Quote Entry for the latest version of given
 * quoteId
 */
public class QuoteVersionResolutionCellDecorator implements CSVCellDecorator
{
	private static final Logger LOG = LoggerFactory.getLogger(QuoteVersionResolutionCellDecorator.class);
	private static final String SEPARATOR = ":";
	private InboundQuoteVersionControlHelper inboundQuoteVersionControlHelper = (InboundQuoteVersionControlHelper) Registry
			.getApplicationContext().getBean("inboundQuoteVersionControlHelper");

	@Override
	public String decorate(int position, Map<Integer, String> impexLine)
	{
		final String quoteId = impexLine.get(Integer.valueOf(position));
		QuoteModel quote = getInboundQuoteVersionControlHelper().getQuoteforCode(quoteId);
		StringBuilder result = new StringBuilder();
		if (quote != null)
		{
			result = result.append(quote.getCode()).append(SEPARATOR).append(quote.getVersion());
		}
		else
		{
			LOG.error("No quote exist in system with quoteId= ", quoteId);
		}
		return result.toString();
	}

	public InboundQuoteVersionControlHelper getInboundQuoteVersionControlHelper()
	{
		return inboundQuoteVersionControlHelper;
	}

	public void setInboundQuoteVersionControlHelper(InboundQuoteVersionControlHelper inboundQuoteVersionControlHelper)
	{
		this.inboundQuoteVersionControlHelper = inboundQuoteVersionControlHelper;
	}

}
