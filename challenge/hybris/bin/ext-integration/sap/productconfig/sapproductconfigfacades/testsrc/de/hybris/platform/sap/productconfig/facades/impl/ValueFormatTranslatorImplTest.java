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
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ValueFormatTranslatorImplTest
{

	private ValueFormatTranslatorImpl cut;

	@Mock
	private I18NService i18nService;

	@Mock
	private CsticModel csticModel;

	@Before
	public void setup()
	{
		cut = new ValueFormatTranslatorImpl();
		MockitoAnnotations.initMocks(this);
		cut.setI18NService(i18nService);
		Mockito.when(i18nService.getCurrentLocale()).thenReturn(Locale.ENGLISH);
	}

	@Test
	public void testGetLocale()
	{
		cut.setI18NService(null);
		final Locale defaultLocale = cut.getLocale();
		assertEquals(Locale.ENGLISH, defaultLocale);
	}

	@Test
	public void testStringStaysTheSame() throws Exception
	{
		final String value = "value";
		final String formattedValue = cut.format(csticModel, value);
		final String parsedValue = cut.parse(UiType.STRING, formattedValue);

		assertEquals("Must be the same", value, parsedValue);

	}

	@Test
	public void testParseNumeric() throws Exception
	{
		final String parsedString = cut.parse(UiType.NUMERIC, "123,999.123");

		assertEquals("Must be in simple format", "123999.123", parsedString);
	}

	@Test
	public void testParseNumeric_empty() throws Exception
	{
		final String parsedString = cut.parse(UiType.NUMERIC, "");

		assertTrue("empty string should remain as empty", parsedString.isEmpty());
	}

	@Test
	public void testParseNumeric_null() throws Exception
	{
		final String parsedString = cut.parse(UiType.NUMERIC, null);

		assertTrue("null value should be parsed as empty string", parsedString.isEmpty());
	}


	@Test
	public void testParseNumeric_invalid() throws Exception
	{
		final String parsedString = cut.parse(UiType.NUMERIC, "abc");

		assertTrue("invalid value should be parsed as empty string", parsedString.isEmpty());
	}


	@Test
	public void testParseNumericDE() throws Exception
	{
		Mockito.when(i18nService.getCurrentLocale()).thenReturn(Locale.GERMAN);
		final String parsedString = cut.parse(UiType.NUMERIC, "123.999,123");

		assertEquals("Must be in simple and english format", "123999.123", parsedString);
	}

	@Test
	public void testParseNumericDotZero() throws Exception
	{
		Mockito.when(i18nService.getCurrentLocale()).thenReturn(Locale.GERMAN);
		final String parsedString = cut.parse(UiType.NUMERIC, "123.999");

		assertEquals("Must be in simple and english format", "123999.0", parsedString);
	}

	@Test
	public void testFormatNumeric() throws Exception
	{
		Mockito.when(Integer.valueOf(csticModel.getValueType())).thenReturn(Integer.valueOf(CsticModel.TYPE_FLOAT));
		final String formattedString = cut.format(csticModel, "123999.123");

		assertEquals("Must be in nice format", "123,999.123", formattedString);
	}

	@Test
	public void testFormatNumericExponent() throws Exception
	{
		Mockito.when(Integer.valueOf(csticModel.getValueType())).thenReturn(Integer.valueOf(CsticModel.TYPE_FLOAT));
		final String formattedString = cut.format(csticModel, "1.23999123E05");

		assertEquals("Must be in nice format", "123,999.123", formattedString);
	}

	@Test
	public void testFormatNumericDE() throws Exception
	{
		Mockito.when(i18nService.getCurrentLocale()).thenReturn(Locale.GERMAN);
		Mockito.when(Integer.valueOf(csticModel.getValueType())).thenReturn(Integer.valueOf(CsticModel.TYPE_FLOAT));
		final String formattedString = cut.format(csticModel, "123999.123");

		assertEquals("Must be in nice format", "123.999,123", formattedString);
	}

	@Test
	public void testFormatNumericExponentDE() throws Exception
	{
		Mockito.when(i18nService.getCurrentLocale()).thenReturn(Locale.GERMAN);
		Mockito.when(Integer.valueOf(csticModel.getValueType())).thenReturn(Integer.valueOf(CsticModel.TYPE_FLOAT));
		final String formattedString = cut.format(csticModel, "1.23999123E05");

		assertEquals("Must be in nice format", "123.999,123", formattedString);
	}

	@Test
	public void testFormatNumericNull() throws Exception
	{
		Mockito.when(Integer.valueOf(csticModel.getValueType())).thenReturn(Integer.valueOf(CsticModel.TYPE_INTEGER));
		final String formattedString = cut.format(csticModel, null);

		assertEquals("Must be in nice format", "", formattedString);
	}

	@Test
	public void testFormatNumericEmpty() throws Exception
	{
		Mockito.when(Integer.valueOf(csticModel.getValueType())).thenReturn(Integer.valueOf(CsticModel.TYPE_INTEGER));
		final String formattedString = cut.format(csticModel, "");

		assertEquals("Must be in nice format", "", formattedString);
	}

	@Test
	public void testFormatNumericLargeNumber() throws Exception
	{
		Mockito.when(Integer.valueOf(csticModel.getValueType())).thenReturn(Integer.valueOf(CsticModel.TYPE_FLOAT));
		final String formattedString = cut.format(csticModel, "9999999999.99999");

		assertEquals("Must be unchanged", "9,999,999,999.99999", formattedString);
	}

	@Test
	public void testFormatDate() throws Exception
	{
		String formattedDateString = cut.formatDate(getDate("2018-10-05"));
		assertEquals("10/5/18", formattedDateString);

		when(i18nService.getCurrentLocale()).thenReturn(Locale.GERMAN);
		formattedDateString = cut.formatDate(getDate("2018-10-05"));
		assertEquals("05.10.18", formattedDateString);

		formattedDateString = cut.formatDate(null);
		assertEquals("", formattedDateString);
	}

	private Date getDate(final String dateString) throws Exception
	{
		final SimpleDateFormat textFormat = new SimpleDateFormat("yyyy-MM-dd");
		return textFormat.parse(dateString);
	}
}
