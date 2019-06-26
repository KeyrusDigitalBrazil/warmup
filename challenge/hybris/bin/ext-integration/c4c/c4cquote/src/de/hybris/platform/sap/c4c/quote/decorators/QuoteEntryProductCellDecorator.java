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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.Registry;
import de.hybris.platform.sap.c4c.quote.constants.C4cquoteConstants;
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteHelper;
import de.hybris.platform.util.CSVCellDecorator;


/**
 * Decorator class to resolve product catalog 
 */
public class QuoteEntryProductCellDecorator implements CSVCellDecorator
{

	private static final Logger LOG = LoggerFactory.getLogger(QuoteEntryProductCellDecorator.class);

	private InboundQuoteHelper inboundQuoteHelper = (InboundQuoteHelper) Registry.getApplicationContext().getBean(
			"inboundQuoteHelper");


	@Override
	public String decorate(int position, Map<Integer, String> impexLine)
	{
		LOG.info("Decorating product and catalog into quote entry models.");
		final String prod = impexLine.get(Integer.valueOf(position));
		String result = null;
		if (prod != null && !prod.equals(C4cquoteConstants.IGNORE))
		{
			final List<String> commentData = Arrays.asList(StringUtils.split(prod, '|'));
			final String quoteId = commentData.get(0);
			final String productId = commentData.get(1);
			if (StringUtils.isNotEmpty(quoteId) && StringUtils.isNotEmpty(productId))
			{
				result = getInboundQuoteHelper().createQuoteEntryProduct(quoteId,productId);
			}
			else
			{
				result = C4cquoteConstants.IGNORE;
			}
		}
		return result;
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
