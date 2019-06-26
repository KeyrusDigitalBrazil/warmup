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
import de.hybris.platform.sap.c4c.quote.constants.C4cquoteConstants;
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteCommentConversionHelper;
import de.hybris.platform.util.CSVCellDecorator;


/**
 * Decorator class to previous version quote comments to  quote comments
 */
public class PreviousQuoteCommentCellDecorator implements CSVCellDecorator
{

	private static final Logger LOG = LoggerFactory.getLogger(PreviousQuoteCommentCellDecorator.class);
	private InboundQuoteCommentConversionHelper inboundQuoteCommentConversionHelper = (InboundQuoteCommentConversionHelper) Registry
			.getApplicationContext().getBean("inboundQuoteCommentConversionHelper");

	@Override
	public String decorate(int position, Map<Integer, String> impexLine)
	{
		LOG.info("Decorating comments information from previous quote version");
		final String quoteId = impexLine.get(Integer.valueOf(position));
		String result = null;
		if (quoteId != null && !quoteId.equals(C4cquoteConstants.IGNORE))
		{
			result = getInboundQuoteCommentConversionHelper().getQuoteComments(quoteId);
		}
		else
		{
			result = C4cquoteConstants.IGNORE;
		}
		return result;
	}

	public InboundQuoteCommentConversionHelper getInboundQuoteCommentConversionHelper()
	{
		return inboundQuoteCommentConversionHelper;
	}

	public void setInboundQuoteCommentConversionHelper(
			InboundQuoteCommentConversionHelper inboundQuoteCommentConversionHelper)
	{
		this.inboundQuoteCommentConversionHelper = inboundQuoteCommentConversionHelper;
	}

}
