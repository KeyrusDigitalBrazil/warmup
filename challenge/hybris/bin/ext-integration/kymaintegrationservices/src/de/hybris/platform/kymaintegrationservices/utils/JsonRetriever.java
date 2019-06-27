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
package de.hybris.platform.kymaintegrationservices.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;


/**
 * Helper class for retrieving json from url.
 */
public class JsonRetriever
{
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonRetriever.class);

	private JsonRetriever()
	{
	}

	public static String urlToJson(final String urlString) throws IOException
	{
		String json;

		try (final BufferedReader in = new BufferedReader(new InputStreamReader(getInputStreamFromUrl(urlString),  StandardCharsets.UTF_8)))
		{
			json = readFromBuffer(in);
		}
		catch (final IOException e)
		{
			LOGGER.error("Exception while reading JSON from URL - {}", urlString, e);
			throw e;
		}

		return json;
	}

	protected static String readFromBuffer(final BufferedReader reader) throws IOException
	{
		final StringBuilder sb = new StringBuilder();
		String inputLine;

		while ((inputLine = reader.readLine()) != null)
		{
			sb.append(inputLine);
		}

		return sb.toString();
	}

	protected static InputStream getInputStreamFromUrl(final String urlString) throws IOException
	{
		final URL url = new URL(urlString);
		final URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Accept", MediaType.APPLICATION_JSON_VALUE);
		return urlConnection.getInputStream();
	}

}
