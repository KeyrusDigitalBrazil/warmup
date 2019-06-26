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
package de.hybris.platform.marketplaceservices.dataimport.batch.util;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.Assert;

import de.hybris.platform.acceleratorservices.dataimport.batch.util.BatchDirectoryUtils;


/**
 * Utility methods used in Marketplace
 */

public class DataIntegrationUtils
{
	protected static final String LOG_DIRECTORY = "log";
	protected static final String LOG_FILE_SUFFIX = ".log";
	protected static final String LOG_TEMP_FILE_SUFFIX = ".log.tmp";

	private DataIntegrationUtils()
	{
	}

	/**
	 * resolve vendor code from file parent directories
	 *
	 * @param file
	 *           the csv being imported
	 * @return the vendor code
	 */
	public static String resolveVendorCode(final File file)
	{
		Assert.notNull(file, "The file cannot be null.");
		final File processingFolder = file.getParentFile();
		Assert.notNull(processingFolder, "The processingFolder cannot be null.");
		final File vendorFolder = processingFolder.getParentFile();
		Assert.notNull(vendorFolder, "The vendorFolder cannot be null.");
		return vendorFolder.getName();
	}

	/**
	 * get the log file for an imported csv file
	 * 
	 * @param file
	 *           the csv being imported
	 * @return the log file
	 */
	public static File getLogFile(final File file)
	{
		return createFile(file, LOG_DIRECTORY, LOG_FILE_SUFFIX);
	}

	/**
	 * get the temp log file for an imported csv file
	 * 
	 * @param file
	 *           the csv being imported
	 * @return the log temp file
	 */
	public static File getTempLogFile(final File file)
	{
		return createFile(file, LOG_DIRECTORY, LOG_TEMP_FILE_SUFFIX);
	}

	/**
	 * @param file
	 *           the csv being imported
	 * @param dir
	 *           the directory where the file to be created
	 * @param extension
	 *           the file suffix
	 * @return the log file
	 */
	public static File createFile(final File file, final String dir, final String ext)
	{
		final String fileDir = BatchDirectoryUtils
				.verify(BatchDirectoryUtils.getRelativeBaseDirectory(file) + File.separator + dir);
		return new File(fileDir, FilenameUtils.getBaseName(file.getName()) + ext);
	}
}
