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
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteCommentConversionHelper;
import de.hybris.platform.util.CSVCellDecorator;


/**
 * Decorator class to convert inbound quote header comments to  quote comment 
 */
public class QuoteCommentCellDecorator implements CSVCellDecorator
{

	private static final Logger LOG = LoggerFactory.getLogger(QuoteCommentCellDecorator.class);
	private InboundQuoteCommentConversionHelper inboundQuoteCommentConversionHelper = (InboundQuoteCommentConversionHelper) Registry
			.getApplicationContext().getBean("inboundQuoteCommentConversionHelper");

	@Override
	public String decorate(int position, Map<Integer, String> impexLine)
	{
		LOG.info("Decorating header comments information from canonical into comments models.");
		final String comments = impexLine.get(Integer.valueOf(position));
		String result = null;
		if (comments != null && !comments.equals(C4cquoteConstants.IGNORE))
		{
			final List<String> commentData = Arrays.asList(StringUtils.split(comments, '|'));
			final String quoteId = commentData.get(0);
			final String commentText = commentData.get(1);
			final String userId = commentData.get(2);
			if (StringUtils.isNotEmpty(commentText))
			{
				result = getInboundQuoteCommentConversionHelper().createHeaderComment(quoteId,commentText,userId);
			}
			else
			{
				result = C4cquoteConstants.IGNORE;
			}
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
