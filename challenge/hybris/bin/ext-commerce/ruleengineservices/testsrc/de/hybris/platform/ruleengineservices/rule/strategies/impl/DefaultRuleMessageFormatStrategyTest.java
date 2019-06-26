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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleMessageParameterDecorator;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueNormalizerStrategy;
import de.hybris.platform.servicelayer.i18n.L10NService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRuleMessageFormatStrategyTest
{
	@InjectMocks
	private DefaultRuleMessageFormatStrategy ruleMessageFormatStrategy;

	@Rule
	public final ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@Mock
	private RuleParameterValueNormalizerStrategy ruleParameterValueNormalizerStrategy;

	@Mock
	private L10NService l10NService;

	@Mock
	private EnumerationService enumerationService;

	@Before
	public void setUp()
	{
		when(ruleParameterValueNormalizerStrategy.normalize(any(), anyString())).then(returnsFirstArg());
	}

	@Test
	public void formatMessageWithoutParameters()
	{
		// given
		final String message = "message without parameters";

		final Map<String, RuleParameterData> parameters = new HashMap<>();

		// when
		final String formattedMessage = ruleMessageFormatStrategy.format(message, parameters, Locale.ENGLISH);

		// then
		assertEquals(message, formattedMessage);
	}

	@Test
	public void formatMessageWithNullParameter()
	{
		// given
		final String message = "single {param1} message";
		final String expectedMessage = "single ? message";

		final Map<String, RuleParameterData> parameters = new HashMap<>();

		final RuleParameterData param1 = new RuleParameterData();
		param1.setUuid("param1");
		param1.setType(String.class.getName());
		param1.setValue(null);

		parameters.put(param1.getUuid(), param1);

		// when
		final String formattedMessage = ruleMessageFormatStrategy.format(message, parameters, Locale.ENGLISH);

		// then
		assertEquals(expectedMessage, formattedMessage);
	}

	@Test
	public void formatMessageWithNullListParameter()
	{
		// given
		final String message = "single {param1} message";
		final String expectedMessage = "single [] message";

		final Map<String, RuleParameterData> parameters = new HashMap<>();

		final RuleParameterData param1 = new RuleParameterData();
		param1.setUuid("param1");
		param1.setType("List(java.lang.String)");
		param1.setValue(null);

		parameters.put(param1.getUuid(), param1);

		// when
		final String formattedMessage = ruleMessageFormatStrategy.format(message, parameters, Locale.ENGLISH);

		// then
		assertEquals(expectedMessage, formattedMessage);
	}

	@Test
	public void formatMessageWithMapParameter()
	{
		// given
		final String message = "single {param1} message";
		final String expectedMessage = "single {} message";

		final Map<String, RuleParameterData> parameters = new HashMap<>();

		final RuleParameterData param1 = new RuleParameterData();
		param1.setUuid("param1");
		param1.setType("Map(java.lang.String,java.lang.String)");
		param1.setValue(null);

		parameters.put(param1.getUuid(), param1);

		// when
		final String formattedMessage = ruleMessageFormatStrategy.format(message, parameters, Locale.ENGLISH);

		// then
		assertEquals(expectedMessage, formattedMessage);
	}

	@Test
	public void formatMessageWithNotFoundParameter()
	{
		// given
		final String message = "not found {param1} message";
		final String expectedMessage = "not found ? message";

		final Map<String, RuleParameterData> parameters = new HashMap<>();

		// when
		final String formattedMessage = ruleMessageFormatStrategy.format(message, parameters, Locale.ENGLISH);

		// then
		assertEquals(expectedMessage, formattedMessage);
	}

	@Test
	public void formatMessageWithSingleParameter()
	{
		// given
		final String message = "single {param1} message";
		final String expectedMessage = "single parameter message";

		final Map<String, RuleParameterData> parameters = new HashMap<>();

		final RuleParameterData param1 = new RuleParameterData();
		param1.setUuid("param1");
		param1.setType(String.class.getName());
		param1.setValue("parameter");

		parameters.put(param1.getUuid(), param1);

		// when
		final String formattedMessage = ruleMessageFormatStrategy.format(message, parameters, Locale.ENGLISH);

		// then
		assertEquals(expectedMessage, formattedMessage);
	}

	@Test
	public void formatMessageWithMultipleParameters()
	{
		// given
		final String message = "message {param1} multiple {param2}";
		final String expectedMessage = "message with multiple parameters";

		final Map<String, RuleParameterData> parameters = new HashMap<>();

		final RuleParameterData param1 = new RuleParameterData();
		param1.setUuid("param1");
		param1.setType(String.class.getName());
		param1.setValue("with");

		final RuleParameterData param2 = new RuleParameterData();
		param2.setUuid("param2");
		param2.setType(String.class.getName());
		param2.setValue("parameters");

		parameters.put(param1.getUuid(), param1);
		parameters.put(param2.getUuid(), param2);

		// when
		final String formattedMessage = ruleMessageFormatStrategy.format(message, parameters, Locale.ENGLISH);

		// then
		assertEquals(expectedMessage, formattedMessage);
	}

	@Test
	public void formatMessageWithNumberParameterForEnglishLocale()
	{
		// given
		final String message = "message with english number {param1,number,0.000}";
		final String expectedMessage = "message with english number 1.230";

		final Map<String, RuleParameterData> parameters = new HashMap<>();

		final RuleParameterData param1 = new RuleParameterData();
		param1.setUuid("param1");
		param1.setType(Float.class.getName());
		param1.setValue(Float.valueOf(1.23f));

		parameters.put(param1.getUuid(), param1);

		// when
		final String formattedMessage = ruleMessageFormatStrategy.format(message, parameters, Locale.ENGLISH);

		// then
		assertEquals(expectedMessage, formattedMessage);
	}

	@Test
	public void formatMessageWithNumberParameterForGermanLocale()
	{
		// given
		final String message = "message with german number {param1,number,0.000}";
		final String expectedMessage = "message with german number 1,420";

		final Map<String, RuleParameterData> parameters = new HashMap<>();

		final RuleParameterData param1 = new RuleParameterData();
		param1.setUuid("param1");
		param1.setType(Float.class.getName());
		param1.setValue(Float.valueOf(1.42f));

		parameters.put(param1.getUuid(), param1);

		// when
		final String formattedMessage = ruleMessageFormatStrategy.format(message, parameters, Locale.GERMAN);

		// then
		assertEquals(expectedMessage, formattedMessage);
	}

	@Test
	public void formatMessageWithPercentageAndMultiplier()
	{
		// given
		final String message = "message with percentage {param1,number,percent*1}";
		final String expectedMessage = "message with percentage 12%";

		final Map<String, RuleParameterData> parameters = new HashMap<>();

		final RuleParameterData param1 = new RuleParameterData();
		param1.setUuid("param1");
		param1.setType(Float.class.getName());
		param1.setValue(Float.valueOf(12f));

		parameters.put(param1.getUuid(), param1);

		// when
		final String formattedMessage = ruleMessageFormatStrategy.format(message, parameters, Locale.GERMAN);

		// then
		assertEquals(expectedMessage, formattedMessage);
	}

	@Test
	public void formatMessageWithCommas()
	{
		// given
		final String message = "message with, {param1,number,#.##} , {param1,number,#.##} commas";
		final String expectedMessage = "message with, 2 , 2 commas";

		final Map<String, RuleParameterData> parameters = new HashMap<>();

		final RuleParameterData param1 = new RuleParameterData();
		param1.setUuid("param1");
		param1.setType(Integer.class.getName());
		param1.setValue(Integer.valueOf(2));

		parameters.put(param1.getUuid(), param1);

		// when
		final String formattedMessage = ruleMessageFormatStrategy.format(message, parameters, Locale.ENGLISH);

		// then
		assertEquals(expectedMessage, formattedMessage);
	}

	@Test
	public void formatMessageWithDecorator()
	{
		// given
		final String message = "message with {param1}";
		final String expectedMessage = "message with <span>decorator</span>";

		final Map<String, RuleParameterData> parameters = new HashMap<>();

		final RuleParameterData param1 = new RuleParameterData();
		param1.setUuid("param1");
		param1.setType(String.class.getName());
		param1.setValue("decorator");

		parameters.put(param1.getUuid(), param1);

		final RuleMessageParameterDecorator parameterDecorator = (formattedValue, parameter) -> "<span>" + formattedValue
				+ "</span>";

		// when
		final String formattedMessage = ruleMessageFormatStrategy.format(message, parameters, Locale.ENGLISH, parameterDecorator);

		// then
		assertEquals(expectedMessage, formattedMessage);

	}
}
