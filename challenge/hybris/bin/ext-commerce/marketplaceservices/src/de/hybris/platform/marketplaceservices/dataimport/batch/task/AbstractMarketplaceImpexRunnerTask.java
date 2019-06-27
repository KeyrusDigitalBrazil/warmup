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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.AbstractImpexRunnerTask;
import de.hybris.platform.marketplaceservices.dataimport.batch.util.DataIntegrationUtils;
import de.hybris.platform.servicelayer.impex.ImpExResource;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.impl.StreamBasedImpExResource;
import de.hybris.platform.servicelayer.session.Session;


/**
 * Impex runner task for marketplace, add the functionality to create log files
 */
public abstract class AbstractMarketplaceImpexRunnerTask extends AbstractImpexRunnerTask
{
	protected static final String DATE_SEPARATOR = "_";
	private static final Logger LOGGER = Logger.getLogger(AbstractMarketplaceImpexRunnerTask.class);
	protected static final String LOG_DIRECTORY = "log";
	protected static final String LOG_DEFAULT_SUCCESS_MSG = "Import success.";

	@Override
	public BatchHeader execute(final BatchHeader header) throws FileNotFoundException
	{
		Assert.notNull(header, "The header can not be null.");
		Assert.notNull(header.getEncoding(), "The header encoding can not be null.");

		final File tempLogFile = DataIntegrationUtils.getTempLogFile(header.getFile());
		final File logFile = DataIntegrationUtils.getLogFile(header.getFile());
		final List<ImportResult> results = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(header.getTransformedFiles()))
		{
			final Session localSession = getSessionService().createNewSession();

			try
			{
				for (final File file : header.getTransformedFiles())
				{
					results.add(process(file, header.getEncoding()));
				}
				log(results, tempLogFile);
			}
			finally
			{
				if (!tempLogFile.renameTo(logFile))
				{
					LOGGER.error("Error when generating log file:" + logFile.getName());
				}
				getSessionService().closeSession(localSession);
			}
		}
		return header;
	}

	protected ImportResult process(final File file, final String encoding) throws FileNotFoundException
	{
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(file);
			final ImportConfig config = getImportConfig();
			final ImpExResource resource = new StreamBasedImpExResource(fis, encoding);
			config.setScript(resource);
			return getImportService().importData(config);
		}
		finally
		{
			IOUtils.closeQuietly(fis);
		}
	}

	protected void log(final List<ImportResult> results, final File target)
	{
		try
		{
			FileUtils.writeStringToFile(target, createLogMsg(results), true);
		}
		catch (final IOException e)//NOSONAR
		{
			LOGGER.error("Error while adding log: " + target.getName());
		}
	}

	protected String createLogMsg(final List<ImportResult> results)
	{
		final String error = results.stream().filter(rs -> rs != null && rs.isError() && rs.hasUnresolvedLines())
				.map(x -> x.getUnresolvedLines().getPreview()).collect(Collectors.joining(System.lineSeparator()));
		return StringUtils.isBlank(error) ? LOG_DEFAULT_SUCCESS_MSG : error;
	}
}
