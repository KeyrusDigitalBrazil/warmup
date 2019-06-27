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
package de.hybris.platform.sap.core.jco.rec.impl;

import de.hybris.platform.sap.core.jco.rec.JCoRecRuntimeException;
import de.hybris.platform.sap.core.jco.rec.VersionReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;


/**
 * Implementation of {@link VersionReader} interface.
 */
public class VersionReaderImpl implements VersionReader
{

	private static final Logger LOGGER = Logger.getLogger(VersionReaderImpl.class);
	private static final String VERSIONTAGNAME = "RepositoryVersion";

	private BufferedReader br;

	@Override
	public String getVersion(final File file)
	{
		try
		{
			initializeReader(file);
			String line = br.readLine();
			
			while(line != null)
			{
				final String version = readLine(line);
				if (version != null)
				{
					return version;
				}
				line = br.readLine();
			}
		}
		catch (final IOException e)
		{
			throw new JCoRecRuntimeException("An error occured while reading the repository file!", e);
		}
		finally
		{
			destroyReader();
		}
		return null;
	}

	/**
	 * Creates a new BufferedReader.
	 * 
	 * @param file
	 *           the file to read.
	 * @throws FileNotFoundException
	 *            if the file was not found.
	 */
	private void initializeReader(final File file) throws FileNotFoundException
	{
		br = new BufferedReader(new FileReader(file));
	}

	/**
	 * Closes and destroys the current BufferedReader.
	 */
	private void destroyReader()
	{
		if (br != null)
		{
			try
			{
				br.close();
				br = null;
			}
			catch (final IOException e)
			{
			    // if closing fails, reader is probably already closed 
			    // so nothing left to be done
				LOGGER.error(e);
			}
		}
	}

	/**
	 * Reads one line from the file as long as there is one line left. If the line contains a RepositoryVersion-tag, its
	 * value is returned.
	 * 
	 * @return Returns the value found in the file or null, if no appropriate tag was found.
	 * @throws IOException
	 *            raised by {@link BufferedReader#readLine()}.
	 */
	private String readLine(String line) throws IOException
	{

		if (line.contains(VERSIONTAGNAME))
		{
			final String[] parts = line.split(VERSIONTAGNAME);
			String version = parts[1];
			version = version.substring(1, version.length() - 2);

			return version;
		}

		return null;
	}

}
