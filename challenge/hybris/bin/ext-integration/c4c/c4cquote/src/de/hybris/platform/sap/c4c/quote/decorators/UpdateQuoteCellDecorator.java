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

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteVersionControlHelper;
import de.hybris.platform.util.CSVCellDecorator;


/**
 * Decorator class for resolving version while updating a given quote
 */
public class UpdateQuoteCellDecorator implements CSVCellDecorator
{

	private InboundQuoteVersionControlHelper inboundQuoteVersionControlHelper = (InboundQuoteVersionControlHelper) Registry
			.getApplicationContext().getBean("inboundQuoteVersionControlHelper");

	@Override
	public String decorate(int position, Map<Integer, String> impexLine)
	{
		final String quoteId = impexLine.get(Integer.valueOf(position));
		QuoteModel quote = getInboundQuoteVersionControlHelper().getQuoteforCode(quoteId);
		return Integer.toString(quote.getVersion());
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
