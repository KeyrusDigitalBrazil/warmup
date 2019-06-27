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

package de.hybris.platform.odata2services.odata.asserts;

import org.assertj.core.description.Description;
import org.assertj.core.error.AssertionErrorFactory;
import org.assertj.core.error.MessageFormatter;
import org.assertj.core.internal.Failures;
import org.assertj.core.presentation.Representation;

/**
 * Message factory for HTTP status conditions
 */
public class ResponseErrors implements AssertionErrorFactory
{
	private static final MessageFormatter FORMATTER = MessageFormatter.instance();
	private static final Failures FAILURES = Failures.instance();
	private static final String STATUS_NOT_EQUAL = "%nHTTP status code <%s> is not equal to expected value of <%s>";
	private static final String STATUS_NOT_SUCCESSFUL = "%nExpected successful HTTP status code but received <%s>";
	private static final String BODY_NOT_EQUAL = "%nResponse body <%s> is not equal to expected value of <%s>";

	private final String message;
	private final Object actual;
	private final Object expected;

	private ResponseErrors(final String m, final Object a, final Object e)
	{
		message = m;
		actual = a;
		expected = e;
	}

	public static AssertionErrorFactory statusShouldBeEqualTo(final int actual, final int expected)
	{
		return new ResponseErrors(STATUS_NOT_EQUAL, actual, expected);
	}

	public static AssertionErrorFactory shouldBeSuccessful(final int actual)
	{
		return new ResponseErrors(STATUS_NOT_SUCCESSFUL, actual, -1);
	}

	public static AssertionErrorFactory bodyShouldBeEqual(final String actual, final String expected)
	{
		return new ResponseErrors(BODY_NOT_EQUAL, actual, expected);
	}

	@Override
	public AssertionError newAssertionError(final Description d, final Representation r)
	{
		final String msg = FORMATTER.format(d, r, message, actual, expected);
		return FAILURES.failure(msg);
	}
}
