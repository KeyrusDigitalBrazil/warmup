/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.integrationservices.util;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.Predicate;

import net.minidev.json.JSONArray;

/**
 * A helper object for evaluating Json content by utilizing JsonPath expressions.
 */
public class JsonObject
{
	private static final Logger LOG = LoggerFactory.getLogger(JsonObject.class);
	private static final String PATH_NOT_FOUND_MSG = "Path %s not found";

	private final DocumentContext document;

	private JsonObject(final DocumentContext ctx)
	{
		document = ctx;
	}

	/**
	 * Parses JSON content from the input stream and creates new json object
	 *
	 * @param in an input stream containing JSON
	 * @return presentation of the parsed content
	 */
	public static JsonObject createFrom(final InputStream in)
	{
		final DocumentContext context = JsonPath.parse(in, Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS));
		return new JsonObject(context);
	}

	/**
	 * Parses JSON content and creates new json object
	 *
	 * @param json a string in JSON format
	 * @return presentation of the parsed content
	 */
	public static JsonObject createFrom(final String json)
	{
		final DocumentContext context = JsonPath.parse(json);
		return new JsonObject(context);
	}

	/**
	 * Looks up a value in the parsed JSON
	 *
	 * @param jsonPath a JSON path, e.g. {@code product.code}, pointing to the element whose value needs to be retrieved.
	 * @return value of the element matching the {@code jsonPath} location.
	 */
	public String getString(final String jsonPath)
	{
		final Object value = getObject(jsonPath);
		return value != null ? value.toString() : null;
	}

	/**
	 * Looks up a value in the parsed JSON. This method is convenient when it's not known what type the path should return.
	 *
	 * @param jsonPath a JSON path, e.g. {@code product.code}, pointing to the element whose value needs to be retrieved.
	 * @return value of the element matching the {@code jsonPath} location.
	 */
	public Object getObject(final String jsonPath)
	{
		try
		{
			final JsonPath path = JsonPath.compile(jsonPath);
			return document.read(path, Object.class);
		}
		catch (final PathNotFoundException e)
		{
			LOG.trace(PATH_NOT_FOUND_MSG, jsonPath, e);
			return null;
		}
	}

	/**
	 * Looks up matching entries in the parsed JSONArray
	 *
	 * @param jsonPath a JSON path, e.g. {@code product.code}, pointing to the element whose value needs to be retrieved.
	 * @param filter Predicate used for filtering matching entry results
	 * @return List of entries matching the {@code jsonPath} location and the predicate.
	 */
	public List<Map<String, Object>> getFilteredCollection(final String jsonPath, final Predicate filter)
	{
		return document.read(jsonPath, filter);
	}

	/**
	 * Looks up entries in the parsed JSONArray
	 *
	 * @param jsonPath a JSON path, e.g. {@code product.code}, pointing to the element whose value needs to be retrieved.
	 * @return List of entries matching the {@code jsonPath} location.
	 * @throws ClassCastException when the path points to a non-collection value
	 * @throws PathNotFoundException when the path does not exist
	 */
	public List<Map<String, Object>> getCollection(final String jsonPath)
	{
		return document.read(jsonPath);
	}

	/**
	 * Retrieves a collection of data at the location in this object specified by the path.
	 * @param path path expression to select a collection of objects.
	 * @return a collection of data selected by the path expression; {@code null} means the path is invalid or does not exist or
	 * the path is pointing to a non-collection element in this json object.
	 */
	public List<Object> getCollectionOfObjects(final String path)
	{
		final Object result = getObject(path);
		return result instanceof List
				? (List<Object>) result
				: null;
	}

	/**
	 * Looks up a {@code boolean} value in the parsed JSON
	 *
	 * @param jsonPath a JSON path, e.g. {@code catalog.active}, pointing to the element whose value needs to be retrieved.
	 * @return value of the element matching the {@code jsonPath} location.
	 * @see Boolean#parseBoolean(String) for the rules about how the boolean value is derived
	 */
	public boolean getBoolean(final String jsonPath)
	{
		final String value = getString(jsonPath);
		return Boolean.parseBoolean(value);
	}

	/**
	 * Checks whether the specified path exists in this JSON object.
	 *
	 * @param path a path to check.
	 * @return {@code true}, if the path exists in this JSON object and contains non-null value or non-empty value in case when the
	 * path resolves to an array; {@code false}, otherwise.
	 */
	public boolean exists(final String path)
	{
		try
		{
			final Object value = document.read(path, Object.class);
			/*
			Although some valid path may contain an empty array, there is an ambiguity with it. Because an empty
			array is returned for exceptions not matching conditions, e.g. [?(@.element == 'not existing value')] or when not
			existing index element is referred in the path expression. Therefore, empty array is treated like null value - it's
			an absent value.
			 */
			return value != null && (!isEmptyArray(value));
		}
		catch (final PathNotFoundException e)
		{
			LOG.trace(PATH_NOT_FOUND_MSG, path, e);
			return false;
		}
	}

	private boolean isEmptyArray(final Object value)
	{
		return value instanceof JSONArray && ((JSONArray) value).isEmpty();
	}

	@Override
	public String toString()
	{
		return document.jsonString();
	}
}
