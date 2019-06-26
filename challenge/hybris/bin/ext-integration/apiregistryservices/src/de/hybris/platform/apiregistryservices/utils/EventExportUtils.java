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
package de.hybris.platform.apiregistryservices.utils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.util.Config;


/**
 * Helper class.
 */
public class EventExportUtils
{
	private static final String NOT_EMPTY_REGEXP = ".+";

	public static final String DELIMITER_PROP = "apiregistryservices.eventPropertyConfiguration.delimiter";
	public static final String EXPORTING_PROP = "apiregistryservices.events.exporting";
	public static final String EXPORTING_OVERRIDDEN_PROP = "apiregistryservices.events.exporting.overridden";
	public static final String BLACKLIST_PROP = "apiregistryservices.events.blacklist";
	private static final String ALLOWED_URL_PROTOCOLS = "apiregistryservices.allowedUrlProtocols";

	private static final Logger LOG = LoggerFactory.getLogger(EventExportUtils.class);

	private EventExportUtils()
	{
	}

	/**
	 * Reads apiregistryservices.eventPropertyConfiguration.delimiter property
	 * 
	 * @return delimiter, default value is .
	 */
	public static final String getDelimiter()
	{
		return Config.getString(DELIMITER_PROP, "\\.");
	}

	/**
	 * reads the kymaintegrationservices.events.exporting property false by default
	 */
	public static boolean isEventExportActive()
	{
		return Config.getBoolean(EXPORTING_PROP, false);
	}

	/**
	 * reads kymaintegrationservices.events.blacklist property return empty array if not defined
	 */
	public static String[] getBlacklist()
	{
		final String property = Config.getString(BLACKLIST_PROP, "");
		if (StringUtils.isNotBlank(property))
		{
			return property.split(",");
		}
		else
		{
			return new String[0];
		}
	}

	/**
	 * Checks if it possible to split the original string with delimiter.
	 *
	 * @param reference
	 * @param delimiter
	 * @return boolean
	 */
	public static boolean canSplitReference(final String reference, final String delimiter)
	{
		return reference.matches(NOT_EMPTY_REGEXP + delimiter + NOT_EMPTY_REGEXP);
	}

	/**
	 * Splits the original string to 2 parts
	 *
	 * @param reference
	 * @param delimiter
	 * @return array of 2 strings
	 */
	public static String[] splitReference(final String reference, final String delimiter)
	{
		return EventExportUtils.canSplitReference(reference, delimiter) ? reference.split(delimiter, 2) : new String[]
		{ reference };
	}

	/**
	 * Checks if the url string is a valid URL regarding to allowed protocols
	 *
	 * @param urlString
	 *           url string that is validated
	 * @return boolean
	 */
	public static boolean isUrlValid(final String urlString)
	{
		boolean isValid;
		final List<String> allowedUrlProtocols = Arrays.asList(Config.getString(ALLOWED_URL_PROTOCOLS, "https").split("\\s*,\\s*"));

		try
		{
			final URL url = new URL(urlString);
			isValid = allowedUrlProtocols.stream().anyMatch(url.getProtocol()::equalsIgnoreCase);
			if (!isValid)
			{
				LOG.error("Protocol {} is not allowed. To enable it, it needs to be added to property '{}'.", url.getProtocol(), ALLOWED_URL_PROTOCOLS);
			}
			url.toURI();
		}
		catch (final MalformedURLException | URISyntaxException e)
		{
			isValid = false;
			LOG.error(String.format("Malformed Url: %s", urlString), e);
		}
		return isValid;
	}
}
