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
package de.hybris.platform.messagecentercsfacades.util;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;


public class JsonUtils
{
	private static final Logger LOG = Logger.getLogger(JsonUtils.class);
	private static final ObjectMapper mapper = new ObjectMapper();

	private JsonUtils()
	{
	}

	public static <T> String toJson(final List<T> objects)
	{
		try
		{
			return mapper.writeValueAsString(objects);// throws IOException
		}
		catch (final IOException e)
		{
			//we do nothing here and just log the error when catching the exception
			LOG.error("The input List was not converted to JSON correctly", e);
		}
		return null;
	}

	public static <T> List<T> fromJson(final String inputString, final Class<T> clazz)
	{
		try
		{
			return mapper.readValue(inputString, mapper.getTypeFactory().constructCollectionType(List.class, clazz));// throws IOException
		}
		catch (final IOException e)
		{
			//we do nothing here and just log the error when catching the exception
			LOG.error("The input is not a valid JSON string", e);
		}
		return Collections.emptyList();
	}
}

