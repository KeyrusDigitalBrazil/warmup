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
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterDefinitionData;
import de.hybris.platform.ruleengineservices.rule.services.RuleConditionsRegistry;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterUuidGenerator;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueConverter;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueNormalizerStrategy;
import org.apache.commons.collections.MapUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRuleConditionsConverterTest
{
	private static final String JSON_EMPTY = "[]";
	private static final String JSON_SIMPLE = "[{\"definitionId\":\"condition\",\"parameters\":{\"param\":{\"value\":\"testabcd\"}}}]";
	private static final String JSON_COMPLEX = "[{\"children\":[{\"definitionId\":\"condition3\",\"parameters\":{\"param2\":{\"value\":\"testabcd\"}}}],\"definitionId\":\"condition1\",\"parameters\":{\"param1\":{\"value\":123}}},{\"definitionId\":\"condition2\"}]";

	private static final String STRING_VALUE = "testabcd";
	private static final String STRING_VALUE_JSON = "\"testabcd\"";

	private static final Integer INTEGER_VALUE = Integer.valueOf(123);
	private static final String INTEGER_VALUE_JSON = "123";

	@Mock
	private RuleConditionsRegistry ruleConditionsRegistry;

	@Mock
	private RuleParameterValueConverter ruleParameterValueConverter;

	@Mock
	private RuleParameterUuidGenerator ruleParameterUuidGenerator;

	@Mock
	private RuleParameterValueNormalizerStrategy ruleParameterValueNormalizerStrategy;

	@InjectMocks
	private DefaultRuleConditionsConverter ruleConditionsConverter;

	@Before
	public void setUp() throws Exception
	{

		when(ruleParameterValueConverter.toString(STRING_VALUE)).thenReturn(STRING_VALUE_JSON);
		when(ruleParameterValueConverter.toString(INTEGER_VALUE)).thenReturn(INTEGER_VALUE_JSON);

		when(ruleParameterValueConverter.fromString(STRING_VALUE_JSON, String.class.getName())).thenReturn(STRING_VALUE);
		when(ruleParameterValueConverter.fromString(INTEGER_VALUE_JSON, Integer.class.getName())).thenReturn(INTEGER_VALUE);

		when(ruleParameterValueNormalizerStrategy.normalize(any(), anyString())).then(returnsFirstArg());

		ruleConditionsConverter.setDebugMode(true);
		ruleConditionsConverter.afterPropertiesSet();
	}

	protected List<RuleConditionData> createRuleConditionsSimple()
	{
		final RuleParameterDefinitionData ruleParameterDefinition = new RuleParameterDefinitionData();
		ruleParameterDefinition.setType(String.class.getName());

		final RuleConditionDefinitionData ruleConditionDefinition = new RuleConditionDefinitionData();
		ruleConditionDefinition.setId("condition");
		ruleConditionDefinition.setParameters(Collections.singletonMap("param", ruleParameterDefinition));

		when(ruleConditionsRegistry.getAllConditionDefinitionsAsMap()).thenReturn(
				Collections.singletonMap("condition", ruleConditionDefinition));

		final RuleParameterData ruleParameter = new RuleParameterData();
		ruleParameter.setValue(STRING_VALUE);

		final RuleConditionData ruleCondition = new RuleConditionData();
		ruleCondition.setDefinitionId("condition");
		ruleCondition.setParameters(Collections.singletonMap("param", ruleParameter));

		return Arrays.asList(ruleCondition);
	}

	protected List<RuleConditionData> createRuleConditionsComplex()
	{
		final RuleParameterDefinitionData ruleParameterDefinition1 = new RuleParameterDefinitionData();
		ruleParameterDefinition1.setType(Integer.class.getName());

		final RuleParameterDefinitionData ruleParameterDefinition2 = new RuleParameterDefinitionData();
		ruleParameterDefinition2.setType(String.class.getName());

		final RuleConditionDefinitionData ruleConditionDefinition1 = new RuleConditionDefinitionData();
		ruleConditionDefinition1.setId("condition1");
		ruleConditionDefinition1.setParameters(Collections.singletonMap("param1", ruleParameterDefinition1));

		final RuleConditionDefinitionData ruleConditionDefinition2 = new RuleConditionDefinitionData();
		ruleConditionDefinition2.setId("condition2");

		final RuleConditionDefinitionData ruleConditionDefinition3 = new RuleConditionDefinitionData();
		ruleConditionDefinition3.setId("condition3");
		ruleConditionDefinition3.setParameters(Collections.singletonMap("param2", ruleParameterDefinition2));

		final Map<String, RuleConditionDefinitionData> ruleConditionDefinitions = new HashMap<>();
		ruleConditionDefinitions.put("condition1", ruleConditionDefinition1);
		ruleConditionDefinitions.put("condition2", ruleConditionDefinition2);
		ruleConditionDefinitions.put("condition3", ruleConditionDefinition3);

		when(ruleConditionsRegistry.getAllConditionDefinitionsAsMap()).thenReturn(ruleConditionDefinitions);

		final RuleParameterData ruleParameter1 = new RuleParameterData();
		ruleParameter1.setValue(INTEGER_VALUE);

		final RuleParameterData ruleParameter2 = new RuleParameterData();
		ruleParameter2.setValue(STRING_VALUE);

		final RuleConditionData ruleCondition1 = new RuleConditionData();
		ruleCondition1.setDefinitionId("condition1");
		ruleCondition1.setParameters(Collections.singletonMap("param1", ruleParameter1));

		final RuleConditionData ruleCondition2 = new RuleConditionData();
		ruleCondition2.setDefinitionId("condition2");

		final RuleConditionData ruleCondition3 = new RuleConditionData();
		ruleCondition3.setParameters(Collections.singletonMap("param2", ruleParameter2));
		ruleCondition3.setDefinitionId("condition3");

		ruleCondition1.setChildren(Arrays.asList(ruleCondition3));

		return Arrays.asList(ruleCondition1, ruleCondition2);
	}

	@Test
	public void convertToStringEmpty() throws Exception
	{
		// given
		final List<RuleConditionData> ruleConditions = new ArrayList<>();

		// when
		final String value = ruleConditionsConverter.toString(ruleConditions, MapUtils.EMPTY_MAP);

		// then
		assertEquals(JSON_EMPTY, value);
	}

	@Test
	public void convertToStringSimple() throws Exception
	{
		// given
		final List<RuleConditionData> ruleConditions = createRuleConditionsSimple();

		// when
		final String value = ruleConditionsConverter.toString(ruleConditions,
				ruleConditionsRegistry.getAllConditionDefinitionsAsMap());

		// then
		assertEquals(JSON_SIMPLE, value);
	}

	@Test
	public void convertToStringComplex() throws Exception
	{
		// given
		final List<RuleConditionData> ruleConditions = createRuleConditionsComplex();

		// when
		final String value = ruleConditionsConverter.toString(ruleConditions,
				ruleConditionsRegistry.getAllConditionDefinitionsAsMap());

		// then
		assertEquals(JSON_COMPLEX, value);
	}

	@Test
	public void convertFromStringEmpty() throws Exception
	{
		// given
		final List<RuleConditionData> expectedValue = new ArrayList<>();

		// when
		final List<RuleConditionData> value = ruleConditionsConverter.fromString(JSON_EMPTY,
				ruleConditionsRegistry.getAllConditionDefinitionsAsMap());

		// then
		assertEquals(expectedValue, value);
	}

	@Test
	public void convertFromStringSimple() throws Exception
	{
		// given
		final List<RuleConditionData> expectedRuleConditions = createRuleConditionsSimple();

		// when
		final List<RuleConditionData> ruleConditions = ruleConditionsConverter.fromString(JSON_SIMPLE,
				ruleConditionsRegistry.getAllConditionDefinitionsAsMap());

		// then
		assertTrue(isSameConditions(expectedRuleConditions, ruleConditions));
	}

	@Test
	public void convertFromStringComplex() throws Exception
	{
		// given
		final List<RuleConditionData> expectedRuleConditions = createRuleConditionsComplex();

		// when
		final List<RuleConditionData> ruleConditions = ruleConditionsConverter.fromString(JSON_COMPLEX,
				ruleConditionsRegistry.getAllConditionDefinitionsAsMap());

		// then
		assertTrue(isSameConditions(expectedRuleConditions, ruleConditions));
	}

	protected boolean isSameConditions(final List<RuleConditionData> ruleConditions1, final List<RuleConditionData> ruleConditions2)
	{
		if (ruleConditions1 == ruleConditions2) // NOPMD
		{
			return true;
		}

		if (ruleConditions1.size() != ruleConditions2.size())
		{
			return false;
		}

		final int size = ruleConditions1.size();

		for (int index = 0; index < size; index++)
		{
			final RuleConditionData ruleCondition1 = ruleConditions1.get(index);
			final RuleConditionData ruleCondition2 = ruleConditions2.get(index);

			if (!isSameCondition(ruleCondition1, ruleCondition2))
			{
				return false;
			}
		}

		return true;
	}

	protected boolean isSameCondition(final RuleConditionData ruleCondition1, final RuleConditionData ruleCondition2)
	{
		return Objects.equals(ruleCondition1.getDefinitionId(), ruleCondition2.getDefinitionId())
				&& isSameConditions(ruleCondition1.getChildren(), ruleCondition2.getChildren())
				&& isSameParameters(ruleCondition1.getParameters(), ruleCondition2.getParameters());
	}


	protected boolean isSameParameters(final Map<String, RuleParameterData> parameters1,
			final Map<String, RuleParameterData> parameters2)
	{
		if (Objects.equals(parameters1, parameters2))
		{
			return true;
		}

		if (MapUtils.isEmpty(parameters1) && MapUtils.isEmpty(parameters2))
		{
			return true;
		}

		if (parameters1.size() != parameters2.size())
		{
			return false;
		}

		for (final Entry<String, RuleParameterData> entry : parameters1.entrySet())
		{
			final RuleParameterData ruleParameter1 = entry.getValue();
			final RuleParameterData ruleParameter2 = parameters2.get(entry.getKey());

			if (!Objects.equals(ruleParameter1.getValue(), ruleParameter2.getValue()))
			{
				return false;
			}
		}

		return true;
	}
}
