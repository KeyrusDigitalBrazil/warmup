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
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteHelper;
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteVersionControlHelper;
import de.hybris.platform.util.CSVCellDecorator;


public class QuoteConfirmationVersionCellDecorator implements CSVCellDecorator
{

	private InboundQuoteHelper inboundQuoteHelper = (InboundQuoteHelper) Registry.getApplicationContext().getBean(
			"inboundQuoteHelper");
	
	private InboundQuoteVersionControlHelper inboundQuoteVersionControlHelper = (InboundQuoteVersionControlHelper) Registry.getApplicationContext().getBean(
			"inboundQuoteVersionControlHelper");

	@Override
	public String decorate(int position, Map<Integer, String> impexLine)
	{
		String quoteId = impexLine.get(position);
		String result = null;
		QuoteModel quote = null;
		if (quoteId != null && !quoteId.isEmpty())
		{
			quote = inboundQuoteVersionControlHelper.getQuoteforCode(quoteId);
		}
		if (quote != null)
		{
			if(QuoteState.BUYER_SUBMITTED.equals(quote.getState()))
			{
				quote = inboundQuoteHelper.createQuoteSnapshot(quoteId, QuoteState.SELLER_REQUEST.toString());
			}
			result = quote.getVersion().toString();
		}
		return result;
	}

}
