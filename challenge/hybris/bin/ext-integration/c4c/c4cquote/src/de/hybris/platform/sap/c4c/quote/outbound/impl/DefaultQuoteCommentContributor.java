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
package de.hybris.platform.sap.c4c.quote.outbound.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.sap.c4c.quote.constants.QuoteCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;


public class DefaultQuoteCommentContributor implements RawItemContributor<QuoteModel>
{

	private static final Set<String> COLUMNS = new HashSet<>(Arrays.asList(QuoteCsvColumns.QUOTE_ID, QuoteCsvColumns.COMMENT_ID,
			QuoteCsvColumns.COMMENT));

	private QuoteService quoteService;

	@Override
	public Set<String> getColumns()
	{
		return COLUMNS;
	}

	@Override
	public List<Map<String, Object>> createRows(final QuoteModel quoteModel)
	{
		final List<Map<String, Object>> result = new ArrayList<>();
		if (quoteModel != null)
		{
			final List<CommentModel> comments = getDeltaComments(quoteModel);
			if (comments != null && !comments.isEmpty())
			{
				addCommentData(comments, result, quoteModel);
			}
		}
		return result;
	}
	
	protected List<CommentModel> getDeltaComments(final QuoteModel currentQuote)
	{
		List<CommentModel> latestComments = new ArrayList<>(currentQuote.getComments());
		if (currentQuote.getVersion() != 1 && !latestComments.isEmpty())
		{
			Integer previousVersion = currentQuote.getVersion() - 1;
			final QuoteModel outdatedQuote = getQuoteService().getQuoteForCodeAndVersion(currentQuote.getCode(), previousVersion);
			if (outdatedQuote != null)
			{
				List<CommentModel> outdatedComments = new ArrayList<>(outdatedQuote.getComments());
				if (!outdatedComments.isEmpty())
				{
					latestComments.removeAll(outdatedComments);
				}
			}
		}
		return latestComments;
	}
	
	protected void addCommentData(List<CommentModel> comments, List<Map<String, Object>> result, QuoteModel quoteModel)
	{
		int counter = 0;
		for (CommentModel comment : comments)
		{
			if (comment.getAuthor() instanceof B2BCustomerModel)
			{
				final Map<String, Object> row = new HashMap<>();
				row.put(QuoteCsvColumns.QUOTE_ID, quoteModel.getCode());
				row.put(QuoteCsvColumns.COMMENT, comment.getText());
				row.put(QuoteCsvColumns.COMMENT_ID, counter);
				result.add(row);
				counter++;
			}
		}
	}

	public QuoteService getQuoteService()
	{
		return quoteService;
	}

	public void setQuoteService(QuoteService quoteService)
	{
		this.quoteService = quoteService;
	}

}
