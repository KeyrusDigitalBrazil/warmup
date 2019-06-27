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
package de.hybris.platform.cmsfacades.common.service.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


@UnitTest
public class DefaultStringDecapitalizerTest
{
	class TESTItemData
	{
	}

	private final DefaultStringDecapitalizer decapitalizer = new DefaultStringDecapitalizer();

	@Test
	public void testDecapitalizeNullClassShouldReturnOptionalEmpty()
	{
		final Optional<String> result = decapitalizer.decapitalize((Class<?>) null);
		Assert.assertThat(result, Matchers.is(Optional.empty()));
	}

	@Test
	public void testDecapitalizeClassShouldReturnCorrectOptionalString()
	{
		final Optional<String> result = decapitalizer.decapitalize(TESTItemData.class);
		Assert.assertThat(result, Matchers.is(Optional.of("testItemData")));
	}

	@Test
	public void testEmpty()
	{
		//given
		final String input = "";
		final String expected = "";

		//when
		final String actual = decapitalizer.decapitalizeString(input);

		//then
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testNoCapital()
	{
		//given
		final String input = "abc";
		final String expected = "abc";

		//when
		final String actual = decapitalizer.decapitalizeString(input);

		//then
		assertThat(actual, equalTo(expected));
	}

	@Test
	public void testStartWithLowercase()
	{
		//given
		final String input = "abC";
		final String expected = "abC";

		//when
		final String actual = decapitalizer.decapitalizeString(input);

		//then
		assertThat(actual, equalTo(expected));
	}

	@Test
	public void testStartWithSingleCapital()
	{
		//given
		final String input = "Abc";
		final String expected = "abc";

		//when
		final String actual = decapitalizer.decapitalizeString(input);

		//then
		assertThat(actual, equalTo(expected));
	}

	@Test
	public void testStartWithTwoCapital()
	{
		//given
		final String input = "ABc";
		final String expected = "aBc";

		//when
		final String actual = decapitalizer.decapitalizeString(input);

		//then
		assertThat(actual, equalTo(expected));
	}

	@Test
	public void testAllCapital()
	{
		//given
		final String input = "ABC";
		final String expected = "abc";

		//when
		final String actual = decapitalizer.decapitalizeString(input);

		//then
		assertThat(actual, equalTo(expected));
	}

	@Test
	public void testCamelCase()
	{
		//given
		final String input = "AbC";
		final String expected = "abC";

		//when
		final String actual = decapitalizer.decapitalizeString(input);

		//then
		assertThat(actual, equalTo(expected));
	}


	@Test
	public void testStartWithTwoCapitalAndCamelCase()
	{
		//given
		final String input = "ABcD";
		final String expected = "aBcD";

		//when
		final String actual = decapitalizer.decapitalizeString(input);

		//then
		assertThat(actual, equalTo(expected));
	}


	@Test
	public void testStartWithSingleCapitalAndDigit()
	{
		//given
		final String input = "A2BcD";
		final String expected = "a2BcD";

		//when
		final String actual = decapitalizer.decapitalizeString(input);

		//then
		assertThat(actual, equalTo(expected));
	}

	@Test
	public void testStartWithManyCapitalsAndDigit()
	{
		//given
		final String input = "ABCD2BcD";
		final String expected = "abcd2BcD";

		//when
		final String actual = decapitalizer.decapitalizeString(input);

		//then
		assertThat(actual, equalTo(expected));
	}

	@Test
	public void testStartWithDigit()
	{
		//given
		final String input = "123ABCdEfGh";
		final String expected = "123ABCdEfGh";

		//when
		final String actual = decapitalizer.decapitalizeString(input);

		//then
		assertThat(actual, equalTo(expected));
	}
}
