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

import de.hybris.platform.integrationservices.util.HttpStatus;

import java.util.Objects;

import org.assertj.core.api.WritableAssertionInfo;
import org.assertj.core.internal.Failures;

/**
 * Evaluates conditions around {@link de.hybris.platform.odata2services.odata.ODataResponse}. This class facilitates reuse through
 * composition instead of reuse by inheritance.
 */
public class ODataResponseEvaluator
{
	private static final ODataResponseEvaluator INSTANCE = new ODataResponseEvaluator();
	private final Failures failures;

	private ODataResponseEvaluator()
	{
		failures = Failures.instance();
	}

	public static ODataResponseEvaluator instance()
	{
		return INSTANCE;
	}

	public void assertStatusEqual(final WritableAssertionInfo info, final int actual, final int expected)
	{
		if (actual != expected)
		{
			throw failures.failure(info, ResponseErrors.statusShouldBeEqualTo(actual, expected));
		}
	}

	public void assertStatusSuccessful(final WritableAssertionInfo info, final int actual)
	{
		if (! HttpStatus.valueOf(actual).isSuccessful())
		{
			throw failures.failure(info, ResponseErrors.shouldBeSuccessful(actual));
		}
	}

	public void assertBodyEqual(final WritableAssertionInfo info, final String actual, final String expected)
	{
		if (!Objects.equals(actual, expected))
		{
			throw failures.failure(info, ResponseErrors.bodyShouldBeEqual(actual, expected));
		}
	}
}
