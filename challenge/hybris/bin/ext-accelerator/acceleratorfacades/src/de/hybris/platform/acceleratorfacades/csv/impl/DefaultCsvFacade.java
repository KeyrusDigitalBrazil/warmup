/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.acceleratorfacades.csv.impl;

import de.hybris.platform.acceleratorfacades.csv.CsvFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Default implementation of {@link de.hybris.platform.acceleratorfacades.csv.CsvFacade}
 */
public class DefaultCsvFacade implements CsvFacade
{
	public static final String LINE_SEPERATOR = "\n";
	public static final String DELIMITER = ",";

	@Override
	public void generateCsvFromCart(final List<String> headers, final boolean includeHeader, final CartData cartData,
			final Writer writer) throws IOException
	{
		if (includeHeader && CollectionUtils.isNotEmpty(headers))
		{
			final StringBuilder csvHeader = new StringBuilder();
			int i = 0;
			for (; i < headers.size() - 1; i++)
			{
				csvHeader.append(StringEscapeUtils.escapeCsv(headers.get(i))).append(DELIMITER);
			}
			csvHeader.append(StringEscapeUtils.escapeCsv(headers.get(i))).append(LINE_SEPERATOR);
			writer.write(csvHeader.toString());
		}

		if (cartData != null && CollectionUtils.isNotEmpty(cartData.getEntries()))
		{
			writeOrderEntries(writer, cartData.getEntries());
		}
	}

	protected void writeOrderEntries(final Writer writer, final List<OrderEntryData> entries) throws IOException
	{
		for (final OrderEntryData entry : entries)
		{
			if (Boolean.TRUE.equals(entry.getProduct().getMultidimensional()))
			{
				for (final OrderEntryData subEntry : entry.getEntries())
				{
					writeOrderEntry(writer, subEntry);
				}
			}
			else
			{
				writeOrderEntry(writer, entry);
			}
		}
	}

	protected void writeOrderEntry(final Writer writer, final OrderEntryData entry) throws IOException
	{
		final StringBuilder csvContent = new StringBuilder();
		csvContent.append(StringEscapeUtils.escapeCsv(entry.getProduct().getCode())).append(DELIMITER)
				.append(StringEscapeUtils.escapeCsv(entry.getQuantity().toString())).append(DELIMITER)
				.append(StringEscapeUtils.escapeCsv(entry.getProduct().getName())).append(DELIMITER)
				.append(StringEscapeUtils.escapeCsv(entry.getBasePrice().getFormattedValue())).append(LINE_SEPERATOR);

		writer.write(csvContent.toString());
	}
}
