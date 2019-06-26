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

package de.hybris.platform.integrationservices.asserts;

import de.hybris.platform.integrationservices.util.JsonObject;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.WritableAssertionInfo;
import org.assertj.core.internal.Failures;

import com.jayway.jsonpath.Filter;

/**
 * Evaluates conditions around {@link de.hybris.platform.integrationservices.util.JsonObject}. This class facilitates reuse through
 * composition instead of reuse by inheritance.
 */
public class JsonEvaluator
{
	private static final JsonEvaluator INSTANCE = new JsonEvaluator();
	private final Failures failures;

	private JsonEvaluator()
	{
		failures = Failures.instance();
	}

	public static JsonEvaluator instance()
	{
		return INSTANCE;
	}

	public Object assertJsonPathContains(final WritableAssertionInfo info, final JsonObject json, final String path, final String expected)
	{
		final String actual = json.getString(path);
		if (!Objects.equals(actual, expected))
		{
			throw failures.failure(info, JsonErrors.pathShouldHaveValue(json, path, actual, expected));
		}
		return actual;
	}

	public void assertJsonPathContainsValueLike(final WritableAssertionInfo info, final JsonObject json, final String path, final String expected)
	{
		final String actual = json.getString(path);
		if (!StringUtils.contains(actual, expected))
		{
			throw failures.failure(info, JsonErrors.pathShouldHaveValue(json, path, actual, expected));
		}
	}

	public void assertJsonArrayPathContains(final WritableAssertionInfo info, final JsonObject json, final String path, final Filter filter)
	{
		final List<?> matchingItems = json.getFilteredCollection(path, filter);

		if (matchingItems.isEmpty())
		{
			throw failures.failure(info, JsonErrors.pathShouldHaveMatchingElements(json, path, filter));
		}
	}

	public void assertJsonArraySize(final WritableAssertionInfo info, final JsonObject json, final String path, final int numExpectedEntries)
	{
		final List<Object> matchingItems = json.getCollectionOfObjects(path);
		if (matchingItems.size() != numExpectedEntries)
		{
			throw failures.failure(info, JsonErrors.pathShouldContainNumberOfElements(json, path, matchingItems.size(), numExpectedEntries));
		}
	}

	public void assertJsonHasMatchingPath(final WritableAssertionInfo info, final JsonObject json, final String path)
	{
		if (!json.exists(path))
		{
			throw failures.failure(info, JsonErrors.pathShouldExist(json, path));
		}
	}

	public void assertJsonHasNoMatchingPath(final WritableAssertionInfo info, final JsonObject json, final String path)
	{
		if (json.exists(path))
		{
			throw failures.failure(info, JsonErrors.pathShouldNotExist(json, path));
		}
	}

	public void assertJsonIsEmpty(final WritableAssertionInfo info, final JsonObject json)
	{
		final String body = json.toString();
		if (!(body.trim().isEmpty() || body.matches("\\{?\\[?\\s*]?}?")))
		{
			throw failures.failure(info, JsonErrors.contentShouldBeEmpty(json));
		}
	}

	public Object assertJsonPathContainsEmptyCollection(final WritableAssertionInfo info, final JsonObject json, final String path)
	{
		final List<Object> pathItems = json.getCollectionOfObjects(path);
		if (pathItems != null && !pathItems.isEmpty())
		{
			throw failures.failure(info, JsonErrors.pathShouldContainEmptyCollections(json, path, pathItems));
		}
		return pathItems;
	}

	public void assertJsonPathContainsElementsExactly(final JsonObject json, final String path, final List<Object> expected)
	{
		final List<Object> pathItems = json.getCollectionOfObjects(path);
		Assertions.assertThat(pathItems)
				.describedAs("%nUnexpected collection values for path %s in%n%s", path, json)
				.containsExactlyElementsOf(expected);
	}
}
