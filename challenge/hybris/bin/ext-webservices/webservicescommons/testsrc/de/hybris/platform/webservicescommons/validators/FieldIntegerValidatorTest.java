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
package de.hybris.platform.webservicescommons.validators;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;


@UnitTest
public class FieldIntegerValidatorTest
{
	private FieldIntegerValidator fieldIntegerValidator;

	@Before
	public void prepare()
	{
		fieldIntegerValidator = new FieldIntegerValidator();
	}

	private Errors createErrors(final Object object, final String name)
	{
		return new BeanPropertyBindingResult(object, name);
	}

	@Test
	public void validateIntegerTest()
	{
		//given
		final Integer integerValue = Integer.valueOf(Integer.MIN_VALUE);
		final Errors errors = createErrors(integerValue, "integerValue");

		//when
		fieldIntegerValidator.validate(integerValue, errors);

		//then
		Assert.assertFalse(errors.hasErrors());
	}

	@Test
	public void validateFloatTest()
	{
		//given
		final Float floatValue = Float.valueOf("23.3");
		final Errors errors = createErrors(floatValue, "floatValue");

		//when
		fieldIntegerValidator.validate(floatValue, errors);

		//then
		Assert.assertTrue(errors.hasErrors());
	}

	@Test
	public void validateNullStringTest()
	{
		//given
		final String emptyString = null;
		final Errors errors = createErrors(emptyString, "emptyString");

		//when
		fieldIntegerValidator.validate(emptyString, errors);

		//then
		Assert.assertTrue(errors.hasErrors());
	}

	@Test
	public void validateEmptyStringTest()
	{
		//given
		final String emptyString = "";
		final Errors errors = createErrors(emptyString, "emptyString");

		//when
		fieldIntegerValidator.validate(emptyString, errors);

		//then
		Assert.assertTrue(errors.hasErrors());
	}

	@Test
	public void validateIntegerStringValueTest()
	{
		//given
		final String intValue = "123456";
		final Errors errors = createErrors(intValue, "intValue");

		//when
		fieldIntegerValidator.validate(intValue, errors);

		//then
		Assert.assertFalse(errors.hasErrors());
	}

	@Test
	public void validateFloatStringTest()
	{
		//given
		final String floatValue = "12.3";
		final Errors errors = createErrors(floatValue, "floatValue");

		//when
		fieldIntegerValidator.validate(floatValue, errors);

		//then
		Assert.assertTrue(errors.hasErrors());
	}

	@Test
	public void validateNegativeStringValueTest()
	{
		//given
		final String negativeValue = "-123";
		final Errors errors = createErrors(negativeValue, "negativeValue");

		//when
		fieldIntegerValidator.validate(negativeValue, errors);

		//then
		Assert.assertFalse(errors.hasErrors());
	}

	@Test
	public void validateLetterStringValueTest()
	{
		//given
		final String letterValue = "abc";
		final Errors errors = createErrors(letterValue, "letterValue");

		//when
		fieldIntegerValidator.validate(letterValue, errors);

		//then
		Assert.assertTrue(errors.hasErrors());
	}

	@Test
	public void validateWhitespaceValueTest()
	{
		//given
		final String whitespaceValue = "12 4";
		final Errors errors = createErrors(whitespaceValue, "whitespaceValue");

		//when
		fieldIntegerValidator.validate(whitespaceValue, errors);

		//then
		Assert.assertTrue(errors.hasErrors());
	}
}
