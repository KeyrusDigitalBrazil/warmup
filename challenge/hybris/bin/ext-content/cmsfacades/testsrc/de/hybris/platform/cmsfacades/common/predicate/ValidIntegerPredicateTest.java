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
package de.hybris.platform.cmsfacades.common.predicate;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;


@UnitTest
public class ValidIntegerPredicateTest
{
	private final ValidIntegerPredicate predicate = new ValidIntegerPredicate();

	@Test
	public void nonNumbersWillFail()
	{
		final boolean result = predicate.test("sdfgsdfgs");
		assertThat("sdfgsdfgs is not an Integer", result, equalTo(false));
	}

	@Test
	public void nonIntegerWillFail()
	{
		final boolean result = predicate.test("123.45");
		assertThat("123.45 is not an Integer", result, equalTo(false));
	}

	@Test
	public void integersWillPass()
	{
		final boolean result = predicate.test("-34");
		assertThat("-34 is an Integer", result, equalTo(true));
	}

	@Test
	public void integersWillFailIfLessThanMin()
	{
		predicate.setMin(10);

		final boolean result = predicate.test("9");
		assertThat("9 is lower than minimum 10", result, equalTo(false));
	}

	@Test
	public void integersWillFailIfGreaterThanMax()
	{
		predicate.setMax(10);

		final boolean result = predicate.test("19");
		assertThat("19 is greater than maximum 10", result, equalTo(false));
	}

	@Test
	public void integersWillPassWhenWithinBoundaries()
	{
		predicate.setMin(5);
		predicate.setMax(10);

		final boolean result = predicate.test("7");
		assertThat("7 is within 5-10 range", result, equalTo(true));
	}

}
