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

import java.util.Collection;

import org.assertj.core.description.Description;
import org.assertj.core.error.AssertionErrorFactory;
import org.assertj.core.error.MessageFormatter;
import org.assertj.core.internal.Failures;
import org.assertj.core.presentation.Representation;

import com.jayway.jsonpath.Filter;

/**
 * A message factory for errors related to JSON content.
 */
public class JsonErrors implements AssertionErrorFactory
{
	private static final MessageFormatter FORMATTER = MessageFormatter.instance();
	private static final Failures FAILURES = Failures.instance();
	private static final String UNEXPECTED_PATH_VALUE = "%nPath %s is expected to contain <%s> but found <%s> in%n%s";
	private static final String CONTENT_NOT_EMPTY = "%nJSON body is expected to be empty but found%n%s";
	private static final String PATH_NOT_FOUND = "%nExpected to have %s path but it was not found in%n%s";
	private static final String UNEXPECTED_PATH_FOUND = "%nPath %s is not expected to exist but found in%n%s";
	private static final String UNEXPECTED_SIZE_OF_PATH_ARRAY = "%nPath %s is expected to contain %s elements but found %s in%n%s";
	private static final String NO_MATCHING_ELEMENTS_FOUND = "%nPath %s should contain element(s) matching %s condition but no matching elements found in%n%s";

	private final JsonObject jsonObject;
	private final String jsonPath;
	private final Object actualValue;
	private final Object expectedValue;
	private final String message;

	private JsonErrors(final JsonObject json, final String path, final Object actual, final Object expected, final String msg)
	{
		jsonObject = json;
		jsonPath = path;
		actualValue = actual;
		expectedValue = expected;
		message = msg;
	}

	public static AssertionErrorFactory pathShouldHaveValue(final JsonObject json, final String path, final String actual, final String expected)
	{
		return new JsonErrors(json, path, actual, expected, UNEXPECTED_PATH_VALUE);
	}

	public static AssertionErrorFactory contentShouldBeEmpty(final JsonObject json)
	{
		return new JsonErrors(json, json.toString(), "", "", CONTENT_NOT_EMPTY);
	}

	public static AssertionErrorFactory pathShouldContainEmptyCollections(final JsonObject json, final String path, final Collection<?> content)
	{
		return new JsonErrors(json, path, content, "[]", UNEXPECTED_PATH_VALUE);
	}

	public static AssertionErrorFactory pathShouldExist(final JsonObject json, final String path)
	{
		return new JsonErrors(json, path, json, json, PATH_NOT_FOUND);
	}

	public static AssertionErrorFactory pathShouldNotExist(final JsonObject json, final String path)
	{
		return new JsonErrors(json, path, json, json, UNEXPECTED_PATH_FOUND);
	}

	public static AssertionErrorFactory pathShouldContainNumberOfElements(final JsonObject json, final String path, final int actual, final int expected)
	{
		return new JsonErrors(json, path, actual, expected, UNEXPECTED_SIZE_OF_PATH_ARRAY);
	}

	public static AssertionErrorFactory pathShouldHaveMatchingElements(final JsonObject json, final String path, final Filter filter)
	{
		return new JsonErrors(json, path, json, filter, NO_MATCHING_ELEMENTS_FOUND);
	}

	@Override
	public AssertionError newAssertionError(final Description d, final Representation r)
	{
		final String msg = FORMATTER.format(d, r, message, jsonPath, expectedValue, actualValue, jsonObject);
		return FAILURES.failure(msg);
	}
}
