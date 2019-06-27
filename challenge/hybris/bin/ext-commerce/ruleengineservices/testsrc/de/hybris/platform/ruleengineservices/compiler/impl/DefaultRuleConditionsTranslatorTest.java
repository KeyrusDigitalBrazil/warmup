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
package de.hybris.platform.ruleengineservices.compiler.impl;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerException;
import de.hybris.platform.ruleengineservices.compiler.RuleConditionTranslator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;


@UnitTest
public class DefaultRuleConditionsTranslatorTest
{
	private static final String CONDITION_DEFINITION_ID = "conditionDefinition";
	private static final String CONDITION_TRANSLATOR_ID = "conditionTranslator";

	@Rule
	public ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@Mock
	private RuleCompilerContext ruleCompilerContext;

	@Mock
	private ApplicationContext applicationContext;

	@Mock
	private RuleConditionTranslator ruleConditionTranslator;

	private DefaultRuleConditionsTranslator ruleConditionsTranslator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		ruleConditionsTranslator = new DefaultRuleConditionsTranslator();
		ruleConditionsTranslator.setApplicationContext(applicationContext);
	}

	@Test
	public void translateConditions() throws Exception
	{
		// given
		final RuleConditionData ruleCondition = new RuleConditionData();
		ruleCondition.setDefinitionId(CONDITION_DEFINITION_ID);

		final RuleConditionDefinitionData ruleConditionDefinition = new RuleConditionDefinitionData();
		ruleConditionDefinition.setTranslatorId(CONDITION_TRANSLATOR_ID);

		final RuleIrCondition ruleIrCondition = new RuleIrFalseCondition();

		when(ruleCompilerContext.getConditionDefinitions()).thenReturn(
				Collections.singletonMap(CONDITION_DEFINITION_ID, ruleConditionDefinition));
		when(applicationContext.getBean(CONDITION_TRANSLATOR_ID, RuleConditionTranslator.class))
				.thenReturn(ruleConditionTranslator);

		when(ruleConditionTranslator.translate(ruleCompilerContext, ruleCondition, ruleConditionDefinition)).thenReturn(
				ruleIrCondition);

		// when
		final List<RuleIrCondition> ruleIrConditions = ruleConditionsTranslator.translate(ruleCompilerContext,
				Arrays.asList(ruleCondition));

		// then
		assertEquals(1, ruleIrConditions.size());
		assertThat(ruleIrConditions, hasItem(ruleIrCondition));
	}

	@Test
	public void conditionDefinitionNotFound() throws Exception
	{
		// given
		final RuleConditionData ruleCondition = new RuleConditionData();

		// when
		final List<RuleIrCondition> ruleIrConditions = ruleConditionsTranslator.translate(ruleCompilerContext,
				Arrays.asList(ruleCondition));

		// then
		assertThat(ruleIrConditions, is(Collections.<RuleIrCondition> emptyList()));
	}

	@Test
	public void conditionTranslatorNotFound() throws Exception
	{
		// given
		final RuleConditionData ruleCondition = new RuleConditionData();
		ruleCondition.setDefinitionId(CONDITION_DEFINITION_ID);

		final RuleConditionDefinitionData ruleConditionDefinition = new RuleConditionDefinitionData();
		ruleConditionDefinition.setTranslatorId(CONDITION_TRANSLATOR_ID);

		when(ruleCompilerContext.getConditionDefinitions()).thenReturn(
				Collections.singletonMap(CONDITION_DEFINITION_ID, ruleConditionDefinition));
		when(applicationContext.getBean(CONDITION_TRANSLATOR_ID, RuleConditionTranslator.class)).thenThrow(
				new NoSuchBeanDefinitionException(CONDITION_TRANSLATOR_ID));

		// expect
		expectedException.expect(RuleCompilerException.class);

		// when
		ruleConditionsTranslator.translate(ruleCompilerContext, Arrays.asList(ruleCondition));
	}
}
