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
package de.hybris.platform.sap.productconfig.frontend.validator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.sap.productconfig.facades.UiValidationType;
import de.hybris.platform.sap.productconfig.frontend.validator.ValidatorTestData;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.Errors;


@UnitTest
public class NumericCheckerImplTest
{
	private final NumericCheckerImpl classUnderTest = new NumericCheckerImpl();
	@Mock
	private I18NService i18nService;
	@Mock
	private Errors errors;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(i18nService.getCurrentLocale()).thenReturn(Locale.ENGLISH);

		classUnderTest.setI18NService(i18nService);

	}

	@Test
	public void testValidWithPoint() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "123.41").getCstics().get(0);
		csticData.setTypeLength(5);
		csticData.setNumberScale(2);

		assertEquals("123.41", classUnderTest.validate(csticData, errors, csticData.getFormattedValue()));
		Mockito.verifyZeroInteractions(errors);
	}



	@Test
	public void testValidErrorFlag() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "123.41").getCstics().get(0);
		csticData.setTypeLength(5);
		csticData.setNumberScale(2);
		csticData.setCsticStatus(CsticStatusType.DEFAULT);

		classUnderTest.validate(csticData, errors, csticData.getFormattedValue());
		assertEquals("CStic should not have any error", CsticStatusType.DEFAULT, csticData.getCsticStatus());
	}


	@Test
	public void testInvalidErrorFlag() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "abc").getCstics().get(0);
		csticData.setCsticStatus(CsticStatusType.DEFAULT);

		classUnderTest.validate(csticData, errors, csticData.getFormattedValue());
		assertEquals("CStic should have an error", CsticStatusType.ERROR, csticData.getCsticStatus());

	}

	@Test
	public void testInvalidOnlyLetters() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "abc").getCstics().get(0);

		classUnderTest.validate(csticData, errors, csticData.getFormattedValue());
		Mockito.verify(errors, Mockito.times(1)).rejectValue(Mockito.eq("formattedValue"), Mockito.anyString(),
				Mockito.any(Object[].class), Mockito.anyString());
	}


	@Test
	public void testInvalidSomeLetters() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "123abc").getCstics().get(0);
		csticData.setTypeLength(5);
		csticData.setNumberScale(0);

		classUnderTest.validate(csticData, errors, csticData.getFormattedValue());
		Mockito.verify(errors, Mockito.times(1)).rejectValue(Mockito.eq("formattedValue"), Mockito.anyString(),
				Mockito.any(Object[].class), Mockito.anyString());

	}

	@Test
	public void testValidateValue_DE() throws Exception
	{
		final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMAN);
		boolean isExpressionCorrect = classUnderTest.validateValue("123", symbols);
		assertTrue("Input is valid: true", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("123,95", symbols);
		assertTrue("Input is valid: true", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("12.395", symbols);
		assertTrue("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("1.423.895", symbols);
		assertTrue("Input is valid: true", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("+123,56", symbols);
		assertTrue("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("-123,56", symbols);
		assertTrue("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("1234565", symbols);
		assertTrue("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("123123456789123455", symbols);
		assertTrue("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("12348885,65", symbols);
		assertTrue("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("123abc", symbols);
		assertFalse("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("123,95abc", symbols);
		assertFalse("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("12.395abc", symbols);
		assertFalse("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("1a2b3c", symbols);
		assertFalse("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("1....222", symbols);
		assertFalse("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("1,,,,222", symbols);
		assertFalse("Input is valid: false", isExpressionCorrect);
	}

	@Test
	public void testValidateValue_EN() throws Exception
	{
		final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
		boolean isExpressionCorrect = classUnderTest.validateValue("123", symbols);
		assertTrue("Input is valid: true", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("123.95", symbols);
		assertTrue("Input is valid: true", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("12,395", symbols);
		assertTrue("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("1,423,895", symbols);
		assertTrue("Input is valid: true", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("+123.56", symbols);
		assertTrue("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("-123.56", symbols);
		assertTrue("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("1234565", symbols);
		assertTrue("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("12348885.65", symbols);
		assertTrue("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("123abc", symbols);
		assertFalse("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("123.95abc", symbols);
		assertFalse("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("12,395abc", symbols);
		assertFalse("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("1a2b3c", symbols);
		assertFalse("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("1....222", symbols);
		assertFalse("Input is valid: false", isExpressionCorrect);

		isExpressionCorrect = classUnderTest.validateValue("1,,,,222", symbols);
		assertFalse("Input is valid: false", isExpressionCorrect);
	}

	@Test
	public void testValidWithGrouping() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "12,000").getCstics().get(0);
		csticData.setTypeLength(5);
		csticData.setNumberScale(0);

		assertEquals("12,000", classUnderTest.validate(csticData, errors, csticData.getFormattedValue()));
		Mockito.verifyZeroInteractions(errors);
	}

	@Test
	public void testValidWithGroupingDE() throws Exception
	{
		Mockito.when(i18nService.getCurrentLocale()).thenReturn(Locale.GERMANY);

		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "12.000").getCstics().get(0);
		csticData.setTypeLength(5);
		csticData.setNumberScale(0);

		assertEquals("12.000", classUnderTest.validate(csticData, errors, csticData.getFormattedValue()));
		Mockito.verifyZeroInteractions(errors);
	}

	@Test
	public void testInvalidTooLong() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "1234567").getCstics().get(0);
		csticData.setTypeLength(6);

		classUnderTest.validate(csticData, errors, csticData.getFormattedValue());
		Mockito.verify(errors, Mockito.times(1)).rejectValue(Mockito.eq("formattedValue"), Mockito.anyString(),
				Mockito.any(Object[].class), Mockito.anyString());
	}



	@Test
	public void testNumericWithoutEntryMask() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "-123456").getCstics().get(0);
		csticData.setTypeLength(6);
		csticData.setNumberScale(0);
		csticData.setEntryFieldMask(null);

		classUnderTest.validate(csticData, errors, csticData.getFormattedValue());
		Mockito.verifyZeroInteractions(errors);
	}

	@Test
	public void testInvalidTooMuchFractions() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "12.345").getCstics().get(0);
		csticData.setTypeLength(6);
		csticData.setNumberScale(2);

		classUnderTest.validate(csticData, errors, csticData.getFormattedValue());
		Mockito.verify(errors, Mockito.times(1)).rejectValue(Mockito.eq("formattedValue"), Mockito.anyString(),
				Mockito.any(Object[].class), Mockito.anyString());
	}

	@Test
	public void testNumberFormatDecimals() throws Exception
	{
		final String string = classUnderTest.createExpectedFormatAsString(3, 0, Locale.ENGLISH);

		assertEquals("###", string);
	}

	@Test
	public void testNumberFormatDecimalsWithGrouping() throws Exception
	{
		final String string = classUnderTest.createExpectedFormatAsString(4, 0, Locale.ENGLISH);

		assertEquals("#,###", string);
	}

	@Test
	public void testNumberFormatDecimalsWithGroupingDE() throws Exception
	{
		final String string = classUnderTest.createExpectedFormatAsString(4, 0, Locale.GERMAN);

		assertEquals("#.###", string);
	}


	@Test
	public void testNumberFormatDecimalsWithFractions() throws Exception
	{
		final String string = classUnderTest.createExpectedFormatAsString(1, 2, Locale.ENGLISH);

		assertEquals("#.##", string);
	}

	@Test
	public void testLongNumber() throws Exception
	{
		final String string = classUnderTest.createExpectedFormatAsString(10, 5, Locale.ENGLISH);

		assertEquals("#,###,###,###.#####", string);
	}

	@Test
	public void testNegativeNumber() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "-123").getCstics().get(0);
		csticData.setTypeLength(3);
		csticData.setNumberScale(0);

		classUnderTest.validate(csticData, errors, csticData.getFormattedValue());
		Mockito.verify(errors, Mockito.times(0)).rejectValue(Mockito.eq("value"), Mockito.anyString(), Mockito.any(Object[].class),
				Mockito.anyString());
	}

	@Test
	public void testWrongNumberFormat() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "1,23").getCstics().get(0);
		csticData.setTypeLength(10);
		csticData.setNumberScale(4);

		classUnderTest.validate(csticData, errors, csticData.getFormattedValue());
		Mockito.verify(errors, Mockito.times(1)).rejectValue(Mockito.anyObject(),
				Mockito.eq("sapproductconfig.value.too.long.fraction"), Mockito.any(Object[].class), Mockito.anyString());
	}

	@Test
	public void testNegativeNumberNotAllowed() throws Exception
	{
		final CsticData csticData = ValidatorTestData.createGroupWithNumeric("xxx", "-123").getCstics().get(0);
		csticData.setTypeLength(3);
		csticData.setNumberScale(0);
		csticData.setEntryFieldMask("___");

		classUnderTest.validate(csticData, errors, csticData.getFormattedValue());
		Mockito.verify(errors, Mockito.times(1)).rejectValue(Mockito.eq("formattedValue"), Mockito.anyString(),
				Mockito.any(Object[].class), Mockito.anyString());
	}

	@Test
	public void testAppliesTo_true()
	{
		final CsticData csticData = new CsticData();
		csticData.setValidationType(UiValidationType.NUMERIC);
		assertTrue(classUnderTest.appliesTo(csticData));
	}

	@Test
	public void testAppliesTo_false()
	{
		final CsticData csticData = new CsticData();
		csticData.setValidationType(UiValidationType.NONE);
		assertFalse(classUnderTest.appliesTo(csticData));
	}

	@Test
	public void testAppliesToValues()
	{
		assertFalse(classUnderTest.appliesToValues());
	}

	@Test
	public void testAppliesToFormattedValues()
	{
		assertTrue(classUnderTest.appliesToFormattedValues());
	}

	@Test
	public void testAppliesToAdditionalValues()
	{
		assertTrue(classUnderTest.appliesToAdditionalValues());
	}
}
