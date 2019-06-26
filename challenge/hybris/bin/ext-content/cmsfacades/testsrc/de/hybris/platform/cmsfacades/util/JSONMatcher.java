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
package de.hybris.platform.cmsfacades.util;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Custom {@link BaseMatcher Matcher} to assert that the serialization of an object matches the one stored in file.
 *
 * @param <T>
 *           the type parameter of the matcher
 */
public class JSONMatcher<T> extends BaseMatcher<T>
{
	private final ObjectMapper mapper = new ObjectMapper();
	private final String expectedSerialization;

	/**
	 * Will return true if the JSON serialization of the actual is equal to the trimmed serialization of the JSON
	 * contained in the provided path
	 *
	 * @param pathToExpected
	 *           path in the classpath to the file containing the expected JSON string, it can contain a beautified JSON
	 * @throws IOException
	 */
	public JSONMatcher(final String pathToExpected) throws IOException
	{
		try (final ByteArrayOutputStream bout = new ByteArrayOutputStream())
		{
			bout.write(getClass().getResourceAsStream(pathToExpected));
			expectedSerialization = bout.toString().replaceAll("[\t\n\r\\s]+", "");
		}
	}

	@Override
	public boolean matches(final Object actual)
	{
		try
		{
			final String actualSerialization = mapper.writeValueAsString(actual);

			return Objects.equals(actualSerialization, expectedSerialization);
		}
		catch (final IOException e)
		{
			return false;
		}
	}

	@Override
	public void describeTo(final Description description)
	{
		description.appendText("was expecting ").appendText(expectedSerialization);
	}
}
