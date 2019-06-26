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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.sap.c4c.quote.constants.QuoteCsvColumns;
import de.hybris.platform.sap.c4c.quote.constants.QuoteEntryCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;


/**
 *
 */
public class DefaultQuoteEntryContributor implements RawItemContributor<QuoteModel>
{

	private static final Logger LOG = Logger.getLogger(DefaultQuoteEntryContributor.class);

	private static final Set<String> COLUMNS = new HashSet<>(Arrays.asList(QuoteCsvColumns.QUOTE_ID,
			QuoteEntryCsvColumns.ENTRY_NUMBER, QuoteEntryCsvColumns.PRODUCT_CODE, QuoteEntryCsvColumns.PRODUCT_NAME,
			QuoteEntryCsvColumns.QUANTITY, QuoteEntryCsvColumns.ENTRY_UNIT_CODE, QuoteCsvColumns.VERSION,
			QuoteCsvColumns.STATUS));

	@Override
	public Set<String> getColumns()
	{
		return COLUMNS;
	}

	@Override
	public List<Map<String, Object>> createRows(final QuoteModel quote)
	{
		final List<AbstractOrderEntryModel> entries = quote.getEntries();
		final List<Map<String, Object>> result = new ArrayList<>();

		for (final AbstractOrderEntryModel entry : entries)
		{
			validateMandatoryParameters(entry, quote);
			final Map<String, Object> row = new HashMap<>();
			row.put(QuoteCsvColumns.QUOTE_ID, quote.getCode());
			row.put(QuoteEntryCsvColumns.ENTRY_NUMBER, entry.getEntryNumber());
			row.put(QuoteEntryCsvColumns.QUANTITY, entry.getQuantity());
			row.put(QuoteEntryCsvColumns.PRODUCT_CODE, entry.getProduct().getCode());
			row.put(QuoteCsvColumns.VERSION, quote.getVersion());
			row.put(QuoteCsvColumns.STATUS, quote.getState().toString());
			final UnitModel unit = entry.getUnit();
			if (unit != null)
			{
				row.put(QuoteEntryCsvColumns.ENTRY_UNIT_CODE, unit.getCode());
			}
			else
			{
				LOG.info("Could not determine unit code for product " + entry.getProduct().getCode() + "as entry "
						+ entry.getEntryNumber() + "of order " + quote.getCode());
			}
			final String language = quote.getLocale() != null ? quote.getLocale() : quote.getStore().getDefaultLanguage()
					.getIsocode();
			final String shortText = determineItemShortText(entry, language);
			row.put(QuoteEntryCsvColumns.PRODUCT_NAME, shortText);
			result.add(row);
		}
		return result;
	}

	private void validateMandatoryParameters(AbstractOrderEntryModel entry, QuoteModel quote)
	{
		validateParameterNotNullStandardMessage(QuoteCsvColumns.CREATION_DATE, entry.getCreationtime());
		validateParameterNotNullStandardMessage(QuoteEntryCsvColumns.ENTRY_NUMBER, entry.getEntryNumber());
		validateParameterNotNullStandardMessage(QuoteEntryCsvColumns.QUANTITY, entry.getQuantity());
		validateParameterNotNullStandardMessage(QuoteEntryCsvColumns.PRODUCT_CODE, entry.getProduct().getCode());
		validateParameterNotNullStandardMessage(QuoteEntryCsvColumns.ENTRY_UNIT_CODE, entry.getUnit().getCode());
		validateParameterNotNullStandardMessage(QuoteCsvColumns.VERSION, quote.getVersion());
		validateParameterNotNullStandardMessage(QuoteCsvColumns.STATUS, quote.getState());
		final String language = quote.getLocale() != null ? quote.getLocale() : quote.getStore().getDefaultLanguage().getIsocode();
		final String shortText = determineItemShortText(entry, language);
		validateParameterNotNullStandardMessage(QuoteEntryCsvColumns.PRODUCT_NAME, shortText);
		validateParameterNotNullStandardMessage(QuoteCsvColumns.LANGUAGE_ISO_CODE, quote.getStore().getDefaultLanguage()
				.getIsocode());
	}

	protected String determineItemShortText(final AbstractOrderEntryModel item, final String language)
	{
		final String shortText = item.getProduct().getName(new java.util.Locale(language));
		return shortText == null ? "" : shortText;
	}

}
