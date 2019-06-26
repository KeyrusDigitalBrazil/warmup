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
package de.hybris.platform.ruleengine.drools.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.RuleEvaluationContext;
import de.hybris.platform.ruleengine.RuleExecutionCountListener;
import de.hybris.platform.ruleengine.enums.DroolsSessionType;
import de.hybris.platform.ruleengine.exception.RuleEngineRuntimeException;
import de.hybris.platform.ruleengine.impl.RuleMatchCountListener;
import de.hybris.platform.ruleengine.model.DroolsKIESessionModel;
import de.hybris.platform.ruleengine.model.DroolsRuleEngineContextModel;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultKieSessionHelperUnitTest
{

	private static final String TEST_SESSION_NAME = "TEST_SESSION_NAME";

	private DefaultKieSessionHelper kieSessionHelper;
	@Mock
	private RuleEvaluationContext context;
	@Mock
	private DroolsRuleEngineContextModel ruleEngineContext;
	@Mock
	private KieContainer kieContainer;
	@Mock
	private DroolsKIESessionModel droolsKieSession;
	@Mock
	private KieSession kieSession;
	@Mock
	private StatelessKieSession statelessKieSession;

	@Before
	public void setUp()
	{
		kieSessionHelper = new DefaultKieSessionHelper();
		when(context.getRuleEngineContext()).thenReturn(ruleEngineContext);
		when(ruleEngineContext.getKieSession()).thenReturn(droolsKieSession);
		when(kieContainer.newKieSession(anyString())).thenReturn(kieSession);
		when(kieContainer.newStatelessKieSession(anyString())).thenReturn(statelessKieSession);
		when(droolsKieSession.getName()).thenReturn(TEST_SESSION_NAME);
	}

	@Test
	public void testCreateRuleExecutionCounterListener()
	{
		kieSessionHelper.setRuleExecutionCounterClass(RuleExecutionCountListener.class);
		try
		{
			kieSessionHelper.createRuleExecutionCounterListener();
			fail("Exception expected");
		}
		catch (final RuleEngineRuntimeException e)
		{
			assertThat(e.getMessage(), is(not(nullValue())));
		}

		kieSessionHelper.setRuleExecutionCounterClass(RuleMatchCountListener.class);
		final RuleExecutionCountListener createRuleExecutionCounterListener = kieSessionHelper
				.createRuleExecutionCounterListener();
		assertThat(createRuleExecutionCounterListener, is(instanceOf(RuleExecutionCountListener.class)));
	}

	@Test
	public void testInitializeKieSessionInternalWrongType()
	{
		// given
		when(droolsKieSession.getSessionType()).thenReturn(DroolsSessionType.STATELESS);

		// execute and check
		Assertions.assertThatThrownBy(() -> kieSessionHelper.initializeKieSessionInternal(context, ruleEngineContext, kieContainer))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testInitializeStatelessKieSessionInternalWrongType()
	{
		// given
		when(droolsKieSession.getSessionType()).thenReturn(DroolsSessionType.STATEFUL);

		// execute and check
		Assertions.assertThatThrownBy(() -> kieSessionHelper.initializeStatelessKieSessionInternal(context, ruleEngineContext, kieContainer))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testInitializeKieSessionInternalOk()
	{
		// given
		when(droolsKieSession.getSessionType()).thenReturn(DroolsSessionType.STATEFUL);

		// execute
		final Object session = kieSessionHelper.initializeKieSessionInternal(context, ruleEngineContext, kieContainer);

		// verify
		assertThat(session).isInstanceOf(KieSession.class);
		verify(kieContainer).newKieSession(TEST_SESSION_NAME);
	}

	@Test
	public void testInitializeStatelessKieSessionInternalOk()
	{
		// given
		when(droolsKieSession.getSessionType()).thenReturn(DroolsSessionType.STATELESS);

		// execute and check
		final Object session = kieSessionHelper.initializeStatelessKieSessionInternal(context, ruleEngineContext, kieContainer);

		// verify
		assertThat(session).isInstanceOf(StatelessKieSession.class);
		verify(kieContainer).newStatelessKieSession(TEST_SESSION_NAME);
	}

	@Test
	public void testInitializeKieSessionStatefulOk()
	{
		// given
		when(droolsKieSession.getSessionType()).thenReturn(DroolsSessionType.STATEFUL);

		// execute and check
		final Object session = kieSessionHelper.initializeSession(KieSession.class, context, kieContainer);

		// verify
		assertThat(session).isInstanceOf(KieSession.class);
		verify(kieContainer).newKieSession(TEST_SESSION_NAME);
	}

	@Test
	public void testInitializeKieSessionStatelessOk()
	{
		// given
		when(droolsKieSession.getSessionType()).thenReturn(DroolsSessionType.STATELESS);

		// execute and check
		final Object session = kieSessionHelper.initializeSession(StatelessKieSession.class, context, kieContainer);

		// verify
		assertThat(session).isInstanceOf(StatelessKieSession.class);
		verify(kieContainer).newStatelessKieSession(TEST_SESSION_NAME);
	}
}
