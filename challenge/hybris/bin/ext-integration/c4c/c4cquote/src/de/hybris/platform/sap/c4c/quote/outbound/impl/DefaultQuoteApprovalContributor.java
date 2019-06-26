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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.c4c.quote.constants.QuoteCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;


public class DefaultQuoteApprovalContributor implements RawItemContributor<QuoteModel>
{

	private static final Set<String> COLUMNS = new HashSet<>(Arrays.asList(QuoteCsvColumns.C4C_QUOTE_ID, QuoteCsvColumns.ORDER_ID));

	@Override
	public Set<String> getColumns()
	{
		return COLUMNS;
	}

	@Override
	public List<Map<String, Object>> createRows(final QuoteModel quoteModel)
	{

		final Map<String, Object> row = new HashMap<>();
		validateMandatoryParameters(quoteModel);
		row.put(QuoteCsvColumns.C4C_QUOTE_ID, quoteModel.getC4cQuoteId());
		row.put(QuoteCsvColumns.ORDER_ID, cutOffZeros(quoteModel.getOrderId()));

		return Arrays.asList(row);
	}

	private void validateMandatoryParameters(final QuoteModel quoteModel)
	{
		validateParameterNotNullStandardMessage(QuoteModel._TYPECODE, quoteModel);
		validateParameterNotNullStandardMessage(QuoteCsvColumns.C4C_QUOTE_ID, quoteModel.getC4cQuoteId());
		validateParameterNotNullStandardMessage(QuoteCsvColumns.ORDER_ID, quoteModel.getOrderId());
	}
	
	/**
	 * Cuts of the leading zeros for document ID display and document item display. If an format exception occurs, the
	 * input is returned.
	 *
	 * @param argument
	 *           the String with leading zeros
	 * @return the String without leading zeros
	 */
	public static String cutOffZeros(final String argument)
	{
		final int size = argument.length();
		int firstNonZeroIndex = 0;
		boolean nonZeroReached = false;

		for (int i = 0; i < size; i++)
		{
			final char ch = argument.charAt(i);
			if (!Character.isDigit(ch))
			{
				return argument;
			}
			if ('0' == ch && !nonZeroReached)
			{
				firstNonZeroIndex = i + 1;
			}
			else
			{
				nonZeroReached = true;
			}
		}
		if (firstNonZeroIndex == 0)
		{
			return argument;
		}
		if (!nonZeroReached)
		{
			return argument;
		}
		return argument.substring(firstNonZeroIndex);
	}

}
