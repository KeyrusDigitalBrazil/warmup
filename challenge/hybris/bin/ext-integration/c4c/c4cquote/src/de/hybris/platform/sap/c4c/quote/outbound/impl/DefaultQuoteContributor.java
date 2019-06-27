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


/**
 *
 */
public class DefaultQuoteContributor implements RawItemContributor<QuoteModel>
{

	private static final Set<String> COLUMNS = new HashSet<>(Arrays.asList(QuoteCsvColumns.QUOTE_ID,
			QuoteCsvColumns.CREATION_DATE, QuoteCsvColumns.QUOTE_NAME, QuoteCsvColumns.QUOTE_NAME, QuoteCsvColumns.DESCRIPTION,
			QuoteCsvColumns.BASESTORE, QuoteCsvColumns.LANGUAGE_ISO_CODE, QuoteCsvColumns.QUOTE_TYPE, QuoteCsvColumns.CURRENCY_CODE));

	@Override
	public Set<String> getColumns()
	{
		return COLUMNS;
	}

	@Override
	public List<Map<String, Object>> createRows(final QuoteModel quoteModel)
	{
		validateMandatoryParameters(quoteModel);
		final Map<String, Object> row = new HashMap<>();
		row.put(QuoteCsvColumns.QUOTE_ID, quoteModel.getCode());
		row.put(QuoteCsvColumns.CREATION_DATE, quoteModel.getDate());
		row.put(QuoteCsvColumns.QUOTE_TYPE, quoteModel.getStore().getSAPConfiguration().getQuoteType());
		row.put(QuoteCsvColumns.QUOTE_NAME, quoteModel.getName());
		row.put(QuoteCsvColumns.DESCRIPTION, quoteModel.getDescription());
		row.put(QuoteCsvColumns.BASESTORE, quoteModel.getStore().getUid());
		if (quoteModel.getCurrency() != null)
		{
			row.put(QuoteCsvColumns.CURRENCY_CODE, quoteModel.getCurrency().getSapCode());
		}

		row.put(QuoteCsvColumns.LANGUAGE_ISO_CODE, quoteModel.getStore().getDefaultLanguage().getIsocode().toUpperCase());

		return Arrays.asList(row);
	}

	private void validateMandatoryParameters(QuoteModel quoteModel)
	{
		validateParameterNotNullStandardMessage(QuoteModel._TYPECODE, quoteModel);
		validateParameterNotNullStandardMessage(QuoteCsvColumns.QUOTE_ID, quoteModel.getCode());
		validateParameterNotNullStandardMessage(QuoteCsvColumns.CREATION_DATE, quoteModel.getDate());
		validateParameterNotNullStandardMessage(QuoteCsvColumns.QUOTE_TYPE, quoteModel.getStore().getSAPConfiguration()
				.getQuoteType());
		validateParameterNotNullStandardMessage(QuoteCsvColumns.QUOTE_NAME, quoteModel.getName());
		validateParameterNotNullStandardMessage(QuoteModel.ENTRIES, quoteModel.getEntries());
		validateParameterNotNullStandardMessage(QuoteCsvColumns.BASESTORE, quoteModel.getStore().getUid());
      validateParameterNotNullStandardMessage(QuoteCsvColumns.CURRENCY_CODE, quoteModel.getCurrency());
		validateParameterNotNullStandardMessage(QuoteCsvColumns.LANGUAGE_ISO_CODE, quoteModel.getStore().getDefaultLanguage()
				.getIsocode());
	}

}
