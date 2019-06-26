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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.Registry;
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteHelper;
import de.hybris.platform.util.CSVCellDecorator;


/**
 * Decorator class to Set the name of the Quote coming from C4C
 */
public class QuoteNameCellDecorator implements CSVCellDecorator
{

	private static final Logger LOG = LoggerFactory.getLogger(QuoteNameCellDecorator.class);

	private InboundQuoteHelper inboundQuoteHelper = (InboundQuoteHelper) Registry.getApplicationContext().getBean(
			"inboundQuoteHelper");

	@Override
	public String decorate(int position, Map<Integer, String> impexLine)
	{
		LOG.debug("Decorating the quote name field");
		String quoteName = null;
		final String quoteId = impexLine.get(Integer.valueOf(position));
		if (StringUtils.isNotEmpty(quoteId))
		{
			quoteName = getInboundQuoteHelper().getNameForQuote(quoteId);
		}
		return quoteName;
	}

	public InboundQuoteHelper getInboundQuoteHelper()
	{
		return inboundQuoteHelper;
	}

	public void setInboundQuoteHelper(InboundQuoteHelper inboundQuoteHelper)
	{
		this.inboundQuoteHelper = inboundQuoteHelper;
	}
}
