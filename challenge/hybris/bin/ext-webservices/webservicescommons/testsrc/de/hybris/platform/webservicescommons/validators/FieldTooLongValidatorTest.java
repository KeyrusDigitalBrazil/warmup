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
public class FieldTooLongValidatorTest
{
	private static final int MAX_LENGTH = 10;
	private FieldTooLongValidator validator;
	TestObject testObject;

	@Before
	public void prepare()
	{
		validator = new FieldTooLongValidator();
		validator.setMaxLength(MAX_LENGTH);
		validator.setFieldPath("stringField");
		testObject = new TestObject();
	}

	private Errors createErrors(final Object object, final String name)
	{
		return new BeanPropertyBindingResult(object, name);
	}

	@Test
	public void validateCorrectStringTest()
	{
		//given
		testObject.setStringField("correct");
		final Errors errors = createErrors(testObject, "testObject");

		//when
		validator.validate(testObject, errors);

		//then
		Assert.assertFalse(errors.hasErrors());
	}

	@Test
	public void validateTooLongStringTest()
	{
		//given
		testObject.setStringField("tooLongString");
		final Errors errors = createErrors(testObject, "testObject");

		//when
		validator.validate(testObject, errors);

		//then
		Assert.assertTrue(errors.hasErrors());
	}

	@Test
	public void validateEmptyStringTest()
	{
		//given
		testObject.setStringField("");
		final Errors errors = createErrors(testObject, "testObject");

		//when
		validator.validate(testObject, errors);

		//then
		Assert.assertFalse(errors.hasErrors());
	}
	@Test
	public void validateNullStringTest()
	{
		testObject.setStringField("");
		final Errors errors = createErrors(testObject, "testObject");

		//when
		validator.validate(testObject, errors);

		//then
		Assert.assertFalse(errors.hasErrors());
	}

	private class TestObject
	{
		String stringField;

		public String getStringField() {
			return stringField;
		}

		public void setStringField(String stringField) {
			this.stringField = stringField;
		}
	}
}
