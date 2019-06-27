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

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.AbstractAssert;

import com.jayway.jsonpath.Filter;

/**
 * Assertions for {@link de.hybris.platform.integrationservices.util.JsonObject}
 */
public class JsonObjectAssertion extends AbstractAssert<JsonObjectAssertion, JsonObject>
{
	private static final JsonEvaluator EVALUATOR = JsonEvaluator.instance();
	private final JsonObject json;
	private Object actual;

	private JsonObjectAssertion(final JsonObject obj)
	{
		super(obj, JsonObjectAssertion.class);
		json = obj;
	}

	public static JsonObjectAssertion assertionOf(final JsonObject json)
	{
		return new JsonObjectAssertion(json);
	}

	public JsonObjectAssertion hasPathWithValue(final String path, final String value)
	{
		actual = EVALUATOR.assertJsonPathContains(info, json, path, value);
		return myself;
	}

	public JsonObjectAssertion hasPathWithValueContaining(final String path, final String value)
	{
		EVALUATOR.assertJsonPathContainsValueLike(info, json, path, value);
		return myself;
	}

	/**
	 * Verifies whether the specified path pointing to a JSON array/collection element contains an element matching the condition.
	 * @param path a path to verify
	 * @param filter a condition to be satisfied by at least one path value element.
	 * @return the assertion for verification of this condition
	 */
	public JsonObjectAssertion pathContainsMatchingElement(final String path, final Filter filter)
	{
		EVALUATOR.assertJsonArrayPathContains(info, json, path, filter);
		return myself;
	}

	/**
	 * Verifies whether the specified path exists and resolves to a value in the context JsonObject.
	 * @param path a path to check.
	 * @return the assertion for verififcation of this condition.
	 */
	public JsonObjectAssertion hasPath(final String path)
	{
		EVALUATOR.assertJsonHasMatchingPath(info, json, path);
		return myself;
	}

	/**
	 * Verifies whether the specified path does not exist or does not resolve to a value in the context JsonObject.
	 * @param path a path to check.
	 * @return the assertion for verififcation of this condition.
	 */
	public JsonObjectAssertion doesNotHavePath(final String path)
	{
		EVALUATOR.assertJsonHasNoMatchingPath(info, json, path);
		return myself;
	}

	/**
	 * Verifies whether the specified path contains a JSON array/collection of the expected size.
	 * @param path a path to check in the context JSON object.
	 * @param numExpectedEntries number of elements expected in the path
	 * @return the assertion for verification of this condition
	 */
	public JsonObjectAssertion pathHasSize(final String path, final int numExpectedEntries)
	{
		EVALUATOR.assertJsonArraySize(info, json, path, numExpectedEntries);
		return myself;
	}

	public JsonObjectAssertion isEmpty()
	{
		EVALUATOR.assertJsonIsEmpty(info, json);
		return myself;
	}

	public JsonObjectAssertion hasEmptyCollectionForPath(final String path)
	{
		actual = EVALUATOR.assertJsonPathContainsEmptyCollection(info, json, path);
		return myself;
	}

	public JsonObjectAssertion hasPathContainingExactly(final String path, final Object... expected)
	{
		return hasPathContainingExactly(path, Arrays.asList(expected));
	}

	public JsonObjectAssertion hasPathContainingExactly(final String path, final List<Object> expected)
	{
		EVALUATOR.assertJsonPathContainsElementsExactly(json, path, expected);
		return myself;
	}

	@Override
	public String toString()
	{
		return actual != null ? actual.toString() : json.toString();
	}
}
