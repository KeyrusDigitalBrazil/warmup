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
package de.hybris.platform.ruleengineservices.rule.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterTypeFormatter;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultRuleParameterValueConverterTest
{
	private static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	@Mock
	private RuleParameterTypeFormatter ruleParameterTypeFormatter;

	private DefaultRuleParameterValueConverter ruleParameterValueConverter;

	@Before
	public void init() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		final Set<String> supportedTypes = new HashSet<>(
				Arrays.asList("java.util.Map", "java.util.List", "java.math.BigDecimal", "java.util.Date"));

		ruleParameterValueConverter = new DefaultRuleParameterValueConverter();
		ruleParameterValueConverter.setSupportedTypes(supportedTypes);
		ruleParameterValueConverter.setRuleParameterTypeFormatter(ruleParameterTypeFormatter);
		ruleParameterValueConverter.afterPropertiesSet();
	}

	@Test
	public void testConvertBigDecimalToString()
	{
		//when
		final String jasonString = ruleParameterValueConverter.toString(BigDecimal.TEN);

		//then
		assertNotNull(jasonString);
		assertEquals(jasonString, "10");
	}

	@Test
	public void testConvertBigDecimalFromString()
	{
		//given
		final String type = BigDecimal.class.getName();
		final String jsonString = "10";

		when(ruleParameterTypeFormatter.formatParameterType(type)).thenReturn(type);

		//when
		final BigDecimal convertedValue = (BigDecimal) ruleParameterValueConverter.fromString(jsonString, type);

		//then
		assertNotNull(convertedValue);
		assertEquals(BigDecimal.TEN, convertedValue);
	}

	@Test
	public void testConvertDateToString() throws ParseException
	{
		//given
		final String expectedDate = "2015-11-20T10:51:59.535+0000";
		final SimpleDateFormat dateFormat = new SimpleDateFormat(ISO_8601, Locale.GERMANY);
		final Date date = dateFormat.parse(expectedDate);

		//when
		final String jasonString = ruleParameterValueConverter.toString(date);

		//then
		assertNotNull(jasonString);
		assertEquals(jasonString.replace("\"", ""), expectedDate);
	}

	@Test
	public void testConvertDateFromString() throws ParseException
	{
		//given
		final String type = Date.class.getName();
		final String jsonString = "\"2015-11-20T10:51:59.535+0000\"";
		final SimpleDateFormat dateFormat = new SimpleDateFormat(ISO_8601, Locale.GERMANY);
		final Date date = dateFormat.parse(jsonString.replace("\"", ""));

		when(ruleParameterTypeFormatter.formatParameterType(type)).thenReturn(type);

		//when
		final Date convertedValue = (Date) ruleParameterValueConverter.fromString(jsonString, type);

		//then
		assertNotNull(convertedValue);
		assertEquals(convertedValue, date);
	}

	@Test
	public void testConvertEnumToString()
	{
		//when
		final String jasonString = ruleParameterValueConverter.toString(TestEnum.VALUE1);

		//then
		assertNotNull(jasonString);
		assertEquals(jasonString, "\"" + TestEnum.VALUE1.name() + "\"");
	}

	@Test
	public void testConvertEnumFromString()
	{
		//given
		final String type = "Enum(" + TestEnum.class.getName() + ")";
		final String jsonString = "\"" + TestEnum.VALUE1.name() + "\"";

		when(ruleParameterTypeFormatter.formatParameterType(type)).thenReturn(type);

		//when
		final TestEnum convertedValue = (TestEnum) ruleParameterValueConverter.fromString(jsonString, type);

		//then
		assertNotNull(convertedValue);
		assertEquals(TestEnum.VALUE1, convertedValue);
	}

	@Test
	public void testConvertListToString() throws ParseException
	{
		//given
		final List<BigDecimal> objects = Arrays.asList(BigDecimal.ONE, BigDecimal.TEN);

		//when
		final String jasonString = ruleParameterValueConverter.toString(objects);

		//then
		assertNotNull(jasonString);
		assertEquals(jasonString, "[1,10]");
	}

	@Test
	public void testConvertListFromString() throws ParseException
	{
		//given
		final String type = "List(" + BigDecimal.class.getName() + ")";
		final String jsonString = "[1,10]";

		when(ruleParameterTypeFormatter.formatParameterType(type)).thenReturn(type);

		//when
		final List<BigDecimal> convertedValue = (List<BigDecimal>) ruleParameterValueConverter.fromString(jsonString, type);

		//then
		final List<BigDecimal> expectedValue = Arrays.asList(BigDecimal.ONE, BigDecimal.TEN);

		assertNotNull(convertedValue);
		assertEquals(convertedValue, expectedValue);
	}

	@Test
	public void testConvertMapToString() throws ParseException
	{
		//given
		final Map<String, BigDecimal> objects = new HashMap<>();
		objects.put("1", BigDecimal.ONE);
		objects.put("10", BigDecimal.TEN);

		//when
		final String jsonString = ruleParameterValueConverter.toString(objects);

		//then
		assertNotNull(jsonString);
		assertEquals(jsonString, "{\"1\":1,\"10\":10}");
	}

	@Test
	public void testConvertMapFromString() throws ParseException
	{
		//given
		final String type = "Map(" + String.class.getName() + ", " + BigDecimal.class.getName() + ")";
		final String jsonString = "{\"1\":1,\"10\":10}";

		when(ruleParameterTypeFormatter.formatParameterType(type)).thenReturn(type);

		//when
		final Map<String, BigDecimal> convertedValue = (Map<String, BigDecimal>) ruleParameterValueConverter.fromString(jsonString,
				type);

		//then
		final Map<String, BigDecimal> expectedValue = new HashMap<>();
		expectedValue.put("1", BigDecimal.ONE);
		expectedValue.put("10", BigDecimal.TEN);

		assertNotNull(convertedValue);
		assertEquals(convertedValue, expectedValue);
	}

	public static enum TestEnum
	{
		VALUE1, VALUE2
	}
}
