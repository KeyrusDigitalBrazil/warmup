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
import de.hybris.platform.ruleengineservices.compiler.RuleActionTranslator;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerException;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAction;
import de.hybris.platform.ruleengineservices.compiler.RuleIrNoOpAction;
import de.hybris.platform.ruleengineservices.rule.data.RuleActionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleActionDefinitionData;

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
public class DefaultRuleActionsTranslatorTest
{
	private static final String ACTION_DEFINITION_ID = "actionDefinition";
	private static final String ACTION_TRANSLATOR_ID = "actionTranslator";

	@Rule
	public ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@Mock
	private RuleCompilerContext ruleCompilerContext;

	@Mock
	private ApplicationContext applicationContext;

	@Mock
	private RuleActionTranslator ruleActionTranslator;

	private DefaultRuleActionsTranslator ruleActionsTranslator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		ruleActionsTranslator = new DefaultRuleActionsTranslator();
		ruleActionsTranslator.setApplicationContext(applicationContext);
	}

	@Test
	public void translateActions() throws Exception
	{
		// given
		final RuleActionData ruleAction = new RuleActionData();
		ruleAction.setDefinitionId(ACTION_DEFINITION_ID);

		final RuleActionDefinitionData ruleActionDefinition = new RuleActionDefinitionData();
		ruleActionDefinition.setTranslatorId(ACTION_TRANSLATOR_ID);

		final RuleIrAction ruleIrAction = new RuleIrNoOpAction();

		when(ruleCompilerContext.getActionDefinitions()).thenReturn(
				Collections.singletonMap(ACTION_DEFINITION_ID, ruleActionDefinition));
		when(applicationContext.getBean(ACTION_TRANSLATOR_ID, RuleActionTranslator.class)).thenReturn(ruleActionTranslator);

		when(ruleActionTranslator.translate(ruleCompilerContext, ruleAction, ruleActionDefinition)).thenReturn(ruleIrAction);

		// when
		final List<RuleIrAction> ruleIrActions = ruleActionsTranslator.translate(ruleCompilerContext, Arrays.asList(ruleAction));

		// then
		assertEquals(1, ruleIrActions.size());
		assertThat(ruleIrActions, hasItem(ruleIrAction));
	}

	@Test
	public void actionDefinitionNotFound() throws Exception
	{
		// given
		final RuleActionData ruleAction = new RuleActionData();

		// when
		final List<RuleIrAction> ruleIrActions = ruleActionsTranslator.translate(ruleCompilerContext, Arrays.asList(ruleAction));

		// then
		assertThat(ruleIrActions, is(Collections.<RuleIrAction> emptyList()));
	}

	@Test
	public void actionTranslatorNotFound() throws Exception
	{
		// given
		final RuleActionData ruleAction = new RuleActionData();
		ruleAction.setDefinitionId(ACTION_DEFINITION_ID);

		final RuleActionDefinitionData ruleActionDefinition = new RuleActionDefinitionData();
		ruleActionDefinition.setTranslatorId(ACTION_TRANSLATOR_ID);

		when(ruleCompilerContext.getActionDefinitions()).thenReturn(
				Collections.singletonMap(ACTION_DEFINITION_ID, ruleActionDefinition));
		when(applicationContext.getBean(ACTION_TRANSLATOR_ID, RuleActionTranslator.class)).thenThrow(
				new NoSuchBeanDefinitionException(ACTION_TRANSLATOR_ID));

		// expect
		expectedException.expect(RuleCompilerException.class);

		// when
		ruleActionsTranslator.translate(ruleCompilerContext, Arrays.asList(ruleAction));
	}
}
