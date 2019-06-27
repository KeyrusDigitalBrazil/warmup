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
package de.hybris.platform.marketplaceservices.dataimport.batch.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.converter.ImpexConverter;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.ImpexTransformerTask;
import de.hybris.platform.marketplaceservices.dataimport.batch.MarketplaceBatchHeader;
import de.hybris.platform.marketplaceservices.dataimport.batch.util.DataIntegrationUtils;
import de.hybris.platform.util.CSVReader;


/**
 * Transformer that retrieves a CSV file and transforms it to an impex file for marketplace
 */
public class MarketplaceImpexTransformerTask extends ImpexTransformerTask
{
	private static final Logger LOGGER = Logger.getLogger(MarketplaceImpexTransformerTask.class);

	/**
	 * Returns the header string with all defined replacements including vendorCode
	 */
	@Override
	protected void buildReplacementSymbols(final Map<String, String> symbols, final BatchHeader header,
			final ImpexConverter converter)
	{
		super.buildReplacementSymbols(symbols, header, converter);

		if (header instanceof MarketplaceBatchHeader)
		{
			symbols.put("$VENDOR$", ((MarketplaceBatchHeader) header).getVendorCode());
			symbols.put("$TAXGROUP$", ((MarketplaceBatchHeader) header).getTaxGroup());
		}
	}

	/**
	 * Also add the error log in log directory
	 */
	@Override
	protected PrintWriter writeErrorLine(final File file, final CSVReader csvReader, final PrintWriter errorWriter,
			final IllegalArgumentException exc) throws UnsupportedEncodingException, FileNotFoundException
	{
		final File tempLogFile = DataIntegrationUtils.getTempLogFile(file);
		log(exc.getMessage() + ": " + csvReader.getSourceLine(), tempLogFile);
		return super.writeErrorLine(file, csvReader, errorWriter, exc);
	}

	protected void log(final String content, final File target)
	{
		try
		{
			FileUtils.writeLines(target, Arrays.asList(content), true);
		}
		catch (final IOException e)//NOSONAR
		{
			LOGGER.error("Error while adding log: " + target.getName());
		}
	}
}
