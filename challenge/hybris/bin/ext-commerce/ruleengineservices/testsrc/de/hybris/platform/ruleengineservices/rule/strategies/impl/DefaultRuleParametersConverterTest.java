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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultRuleParametersConverterTest
{
	private static final String JSON_EMPTY = "[]";
	private static final String JSON_SIMPLE = "[{\"type\":\"java.lang.Integer\",\"uuid\":\"98b94d5f-7c33-434f-a1e0-024d304a7285\",\"value\":123}]";
	private static final String JSON_COMPLEX = "[{\"type\":\"java.lang.Integer\",\"uuid\":\"98b94d5f-7c33-434f-a1e0-024d304a7285\",\"value\":123},{\"type\":\"java.lang.String\",\"uuid\":\"f19e8f23-f3e7-4d01-aa50-3c3ecc941d97\",\"value\":\"testabcd\"}]";

	private static final String STRING_VALUE = "testabcd";
	private static final String STRING_VALUE_JSON = "\"testabcd\"";
	private static final String STRING_TYPE = String.class.getName();

	private static final Integer INTEGER_VALUE = Integer.valueOf(123);
	private static final String INTEGER_VALUE_JSON = "123";
	private static final String INTEGER_TYPE = Integer.class.getName();

	private static final String UUID1 = "98b94d5f-7c33-434f-a1e0-024d304a7285";
	private static final String UUID2 = "f19e8f23-f3e7-4d01-aa50-3c3ecc941d97";

	@Mock
	private RuleParameterValueConverter ruleParameterValueConverter;

	private DefaultRuleParametersConverter ruleParametersConverter;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		when(ruleParameterValueConverter.toString(STRING_VALUE)).thenReturn(STRING_VALUE_JSON);
		when(ruleParameterValueConverter.toString(INTEGER_VALUE)).thenReturn(INTEGER_VALUE_JSON);

		when(ruleParameterValueConverter.fromString(STRING_VALUE_JSON, STRING_TYPE)).thenReturn(STRING_VALUE);
		when(ruleParameterValueConverter.fromString(INTEGER_VALUE_JSON, INTEGER_TYPE)).thenReturn(INTEGER_VALUE);

		ruleParametersConverter = new DefaultRuleParametersConverter();
		ruleParametersConverter.setRuleParameterValueConverter(ruleParameterValueConverter);
		ruleParametersConverter.setDebugMode(true);
		ruleParametersConverter.afterPropertiesSet();
	}

	protected List<RuleParameterData> createParametersSimple()
	{
		final RuleParameterData parameter = new RuleParameterData();
		parameter.setUuid(UUID1);
		parameter.setType(INTEGER_TYPE);
		parameter.setValue(INTEGER_VALUE);

		return Arrays.asList(parameter);
	}

	protected List<RuleParameterData> createParametersComplex()
	{
		final RuleParameterData parameter1 = new RuleParameterData();
		parameter1.setUuid(UUID1);
		parameter1.setType(INTEGER_TYPE);
		parameter1.setValue(INTEGER_VALUE);

		final RuleParameterData parameter2 = new RuleParameterData();
		parameter2.setUuid(UUID2);
		parameter2.setType(STRING_TYPE);
		parameter2.setValue(STRING_VALUE);

		return Arrays.asList(parameter1, parameter2);
	}

	@Test
	public void convertToStringEmpty() throws Exception
	{
		// given
		final List<RuleParameterData> parameters = new ArrayList<>();

		// when
		final String value = ruleParametersConverter.toString(parameters);

		// then
		assertEquals(JSON_EMPTY, value);
	}

	@Test
	public void convertToStringSimple() throws Exception
	{
		// given
		final List<RuleParameterData> parameters = createParametersSimple();

		// when
		final String value = ruleParametersConverter.toString(parameters);

		// then
		assertEquals(JSON_SIMPLE, value);
	}

	@Test
	public void convertToStringComplex() throws Exception
	{
		// given
		final List<RuleParameterData> parameters = createParametersComplex();

		// when
		final String value = ruleParametersConverter.toString(parameters);

		// then
		assertEquals(JSON_COMPLEX, value);
	}

	@Test
	public void convertFromStringEmpty() throws Exception
	{
		// given
		final List<RuleParameterData> expectedParameters = new ArrayList<>();

		// when
		final List<RuleParameterData> parameters = ruleParametersConverter.fromString(JSON_EMPTY);

		// then
		assertEquals(expectedParameters, parameters);
	}

	@Test
	public void convertFromStringSimple() throws Exception
	{
		// given
		final List<RuleParameterData> expectedParameters = createParametersSimple();

		// when
		final List<RuleParameterData> parameters = ruleParametersConverter.fromString(JSON_SIMPLE);

		// then
		assertTrue(isSameParameters(expectedParameters, parameters));
	}

	@Test
	public void convertFromStringComplex() throws Exception
	{
		// given
		final List<RuleParameterData> expectedParameters = createParametersComplex();

		// when
		final List<RuleParameterData> parameters = ruleParametersConverter.fromString(JSON_COMPLEX);

		// then
		assertTrue(isSameParameters(expectedParameters, parameters));
	}

	protected boolean isSameParameters(final List<RuleParameterData> parameters1, final List<RuleParameterData> parameters2)
	{
		if (parameters1 == parameters2) // NOPMD
		{
			return true;
		}

		if (parameters1.size() != parameters2.size())
		{
			return false;
		}

		final int size = parameters1.size();

		for (int index = 0; index < size; index++)
		{
			final RuleParameterData parameter1 = parameters1.get(index);
			final RuleParameterData parameter2 = parameters2.get(index);

			if (!isSameParameter(parameter1, parameter2))
			{
				return false;
			}
		}

		return true;
	}

	protected boolean isSameParameter(final RuleParameterData parameter1, final RuleParameterData parameter2)
	{
		return Objects.equals(parameter1.getUuid(), parameter2.getUuid())
				&& Objects.equals(parameter1.getType(), parameter2.getType())
				&& Objects.equals(parameter1.getValue(), parameter2.getValue());
	}
}
