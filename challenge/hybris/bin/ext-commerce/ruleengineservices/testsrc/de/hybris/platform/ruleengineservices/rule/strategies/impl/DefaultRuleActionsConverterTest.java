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
import de.hybris.platform.ruleengineservices.rule.data.RuleActionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleActionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterDefinitionData;
import de.hybris.platform.ruleengineservices.rule.services.RuleActionsRegistry;
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
public class DefaultRuleActionsConverterTest
{
	private static final String JSON_EMPTY = "[]";
	private static final String JSON_SIMPLE = "[{\"definitionId\":\"action\",\"parameters\":{\"param\":{\"value\":\"testabcd\"}}}]";
	private static final String JSON_COMPLEX = "[{\"definitionId\":\"action1\",\"parameters\":{\"param1\":{\"value\":123}}},{\"definitionId\":\"action2\"}]";

	private static final String STRING_VALUE = "testabcd";
	private static final String STRING_VALUE_JSON = "\"testabcd\"";

	private static final Integer INTEGER_VALUE = Integer.valueOf(123);
	private static final String INTEGER_VALUE_JSON = "123";

	@Mock
	private RuleActionsRegistry ruleActionsRegistry;

	@Mock
	private RuleParameterValueConverter ruleParameterValueConverter;

	@Mock
	private RuleParameterUuidGenerator ruleParameterUuidGenerator;

	@Mock
	private RuleParameterValueNormalizerStrategy ruleParameterValueNormalizerStrategy;

	@InjectMocks
	private DefaultRuleActionsConverter ruleActionsConverter;

	@Before
	public void setUp() throws Exception
	{
		when(ruleParameterValueConverter.toString(STRING_VALUE)).thenReturn(STRING_VALUE_JSON);
		when(ruleParameterValueConverter.toString(INTEGER_VALUE)).thenReturn(INTEGER_VALUE_JSON);

		when(ruleParameterValueConverter.fromString(STRING_VALUE_JSON, String.class.getName())).thenReturn(STRING_VALUE);
		when(ruleParameterValueConverter.fromString(INTEGER_VALUE_JSON, Integer.class.getName())).thenReturn(INTEGER_VALUE);

		when(ruleParameterValueNormalizerStrategy.normalize(any(), anyString())).then(returnsFirstArg());

		ruleActionsConverter.setDebugMode(true);
		ruleActionsConverter.afterPropertiesSet();
	}

	protected List<RuleActionData> createRuleActionsSimple()
	{
		final RuleParameterDefinitionData ruleParameterDefinition = new RuleParameterDefinitionData();
		ruleParameterDefinition.setType(String.class.getName());

		final RuleActionDefinitionData ruleActionDefinition = new RuleActionDefinitionData();
		ruleActionDefinition.setId("action");
		ruleActionDefinition.setParameters(Collections.singletonMap("param", ruleParameterDefinition));

		when(ruleActionsRegistry.getAllActionDefinitionsAsMap()).thenReturn(
				Collections.singletonMap("action", ruleActionDefinition));

		final RuleParameterData ruleParameter = new RuleParameterData();
		ruleParameter.setValue(STRING_VALUE);

		final RuleActionData ruleAction = new RuleActionData();
		ruleAction.setDefinitionId("action");
		ruleAction.setParameters(Collections.singletonMap("param", ruleParameter));

		return Arrays.asList(ruleAction);
	}

	protected List<RuleActionData> createRuleActionsComplex()
	{
		final RuleParameterDefinitionData ruleParameterDefinition1 = new RuleParameterDefinitionData();
		ruleParameterDefinition1.setType(Integer.class.getName());

		final RuleActionDefinitionData ruleActionDefinition1 = new RuleActionDefinitionData();
		ruleActionDefinition1.setId("action1");
		ruleActionDefinition1.setParameters(Collections.singletonMap("param1", ruleParameterDefinition1));

		final RuleActionDefinitionData ruleActionDefinition2 = new RuleActionDefinitionData();
		ruleActionDefinition2.setId("action2");

		final Map<String, RuleActionDefinitionData> ruleActionDefinitions = new HashMap<>();
		ruleActionDefinitions.put("action1", ruleActionDefinition1);
		ruleActionDefinitions.put("action2", ruleActionDefinition2);

		when(ruleActionsRegistry.getAllActionDefinitionsAsMap()).thenReturn(ruleActionDefinitions);

		final RuleParameterData ruleParameter1 = new RuleParameterData();
		ruleParameter1.setValue(INTEGER_VALUE);

		final RuleActionData ruleAction1 = new RuleActionData();
		ruleAction1.setDefinitionId("action1");
		ruleAction1.setParameters(Collections.singletonMap("param1", ruleParameter1));

		final RuleActionData ruleAction2 = new RuleActionData();
		ruleAction2.setDefinitionId("action2");

		return Arrays.asList(ruleAction1, ruleAction2);
	}

	@Test
	public void convertToStringEmpty() throws Exception
	{
		// given
		final List<RuleActionData> actions = new ArrayList<>();

		// when
		final String value = ruleActionsConverter.toString(actions, ruleActionsRegistry.getAllActionDefinitionsAsMap());

		// then
		assertEquals(JSON_EMPTY, value);
	}

	@Test
	public void convertToStringSimple() throws Exception
	{
		// given
		final List<RuleActionData> ruleActions = createRuleActionsSimple();

		// when
		final String value = ruleActionsConverter.toString(ruleActions, ruleActionsRegistry.getAllActionDefinitionsAsMap());

		// then
		assertEquals(JSON_SIMPLE, value);
	}

	@Test
	public void convertToStringComplex() throws Exception
	{
		// given
		final List<RuleActionData> ruleActions = createRuleActionsComplex();

		// when
		final String value = ruleActionsConverter.toString(ruleActions, ruleActionsRegistry.getAllActionDefinitionsAsMap());

		// then
		assertEquals(JSON_COMPLEX, value);
	}

	@Test
	public void convertFromStringEmpty() throws Exception
	{
		// given
		final List<RuleActionData> expectedRuleActions = new ArrayList<>();

		// when
		final List<RuleActionData> ruleActions = ruleActionsConverter.fromString(JSON_EMPTY,
				ruleActionsRegistry.getAllActionDefinitionsAsMap());

		// then
		assertEquals(expectedRuleActions, ruleActions);
	}

	@Test
	public void convertFromStringSimple() throws Exception
	{
		// given
		final List<RuleActionData> expectedRuleActions = createRuleActionsSimple();

		// when
		final List<RuleActionData> ruleActions = ruleActionsConverter.fromString(JSON_SIMPLE,
				ruleActionsRegistry.getAllActionDefinitionsAsMap());

		// then
		assertTrue(isSameActions(expectedRuleActions, ruleActions));
	}

	@Test
	public void convertFromStringComplex() throws Exception
	{
		// given
		final List<RuleActionData> expectedRuleActions = createRuleActionsComplex();

		// when
		final List<RuleActionData> ruleActions = ruleActionsConverter.fromString(JSON_COMPLEX,
				ruleActionsRegistry.getAllActionDefinitionsAsMap());

		// then
		assertTrue(isSameActions(expectedRuleActions, ruleActions));
	}

	protected boolean isSameActions(final List<RuleActionData> ruleActions1, final List<RuleActionData> ruleActions2)
	{
		if (ruleActions1 == ruleActions2) // NOPMD
		{
			return true;
		}

		if (ruleActions1.size() != ruleActions2.size())
		{
			return false;
		}

		final int size = ruleActions1.size();

		for (int index = 0; index < size; index++)
		{
			final RuleActionData ruleAction1 = ruleActions1.get(index);
			final RuleActionData ruleAction2 = ruleActions2.get(index);

			if (!isSameAction(ruleAction1, ruleAction2))
			{
				return false;
			}
		}

		return true;
	}

	protected boolean isSameAction(final RuleActionData ruleAction1, final RuleActionData ruleAction2)
	{
		return Objects.equals(ruleAction1.getDefinitionId(), ruleAction2.getDefinitionId())
				&& isSameParameters(ruleAction1.getParameters(), ruleAction2.getParameters());
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
