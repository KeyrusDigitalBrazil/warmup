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
package de.hybris.platform.acceleratorservices.util;

import static com.google.common.base.Preconditions.checkArgument;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class HtmlElementHelper
{
	private static final String CLASS_ATTRIBUTE = "class";
	private static final Logger LOG = Logger.getLogger(HtmlElementHelper.class);

	public void writeOpenElement(final PageContext pageContext, final String elementName, final Map<String, String> attributes)
	{
		validateParameterNotNull(pageContext, "Parameter pageContext must not be null");
		validateParameterNotNull(elementName, "Parameter elementName must not be null");
		checkArgument(StringUtils.isNotBlank(elementName), "Parameter elementName must not be blank");


		final JspWriter out = pageContext.getOut();

		try
		{
			out.write("<" + elementName);
			if (attributes != null && !attributes.isEmpty())
			{
				for (final Map.Entry<String, String> entry : attributes.entrySet())
				{
					out.write(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
				}
			}
			out.write(">");
			out.write("\n");
		}
		catch (final IOException e)
		{
			LOG.warn("Could not write open element: " + e);
		}
	}

	public void writeEndElement(final PageContext pageContext, final String elementName)
	{
		validateParameterNotNull(pageContext, "Parameter pageContext must not be null");
		validateParameterNotNull(elementName, "Parameter elementName must not be null");
		checkArgument(StringUtils.isNotBlank(elementName), "Parameter elementName must not be blank");

		final JspWriter out = pageContext.getOut();

		try
		{
			out.write("</" + elementName + ">");
		}
		catch (final IOException e)
		{
			LOG.warn("Could not write end element: " + e);
		}
	}

	/**
	 * Performs a merge of given attribute maps.
	 * 
	 * @param maps maps to merge
	 * @return merged attribute map from given maps
	 */
	public Map<String, String> mergeAttributeMaps(final Map<String, String>... maps)
	{
		Map<String, String> result = new HashMap<>();

		if (maps != null && maps.length > 0)
		{
			for (final Map<String, String> map : maps)
			{
				if (map != null)
				{
					result = mergeSingleAttributeMap(map, result);
				}
			}
		}

		return result;
	}

	/**
	 * Processes a given attribute map and merges it with global result map.
	 * For entry key "class" it concatenates values with a space character as a separator (example: "class", "css1 css2").
	 * For keys other than "class" a new value (if not null) replaces an old value.
	 * For null values it removes the entry for given key from the map.
	 * @param map currently processes map
	 * @param result result map
	 * @return result map
	 */
	protected Map<String, String> mergeSingleAttributeMap(final Map<String, String> map, final Map<String, String> result)
	{
		final Map<String, String> localResult = result;
		for (final Map.Entry<String, String> entry : map.entrySet())
		{
			if (entry.getValue() != null)
			{
				localResult.merge(entry.getKey(), entry.getValue(), (oldValue, newValue) -> mergeAttributeValue(entry.getKey(), oldValue, newValue));
			} 
			else
			{
				if (!CLASS_ATTRIBUTE.equals(entry.getKey()))
				{
					localResult.remove(entry.getKey());
				}
			}
		}
		return localResult;
	}

	/**
	 * Performs a merge of current and new value. For key="class" concatenates both values with a space character as a separator.
	 * 
	 * @param key key
	 * @param currentValue current value in map
	 * @param newValue new value to put
	 * @return new value to put in map 
	 */
	protected String mergeAttributeValue(final String key, final String currentValue, final String newValue)
	{
		if (CLASS_ATTRIBUTE.equals(key) && !currentValue.contains(newValue))
		{
			return currentValue + " " + newValue;
		}
		return newValue;
	}
}
