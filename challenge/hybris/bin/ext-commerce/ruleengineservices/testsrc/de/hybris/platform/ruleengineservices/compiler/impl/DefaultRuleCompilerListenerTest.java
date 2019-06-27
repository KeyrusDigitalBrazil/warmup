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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRuleCompilerListenerTest
{
	@Mock
	private EngineRuleDao engineRuleDao;

	@Mock
	private RuleCompilerContext ruleCompilerContext;

	@Mock
	private AbstractRuleEngineRuleModel engineRule;

	@Mock
	private AbstractRuleModel ruleModel;

	@InjectMocks
	private DefaultRuleCompilerListener listener;

	@Test
	public void shouldSetRuleVersionFromEngineRuleToContext()
	{
		when(ruleCompilerContext.getRule()).thenReturn(ruleModel);
		when(ruleModel.getCode()).thenReturn("ruleCode");
		when(ruleCompilerContext.getModuleName()).thenReturn("moduleName");
		when(engineRule.getVersion()).thenReturn(10L);
		when(engineRuleDao.getRuleByCode(ruleCompilerContext.getRule().getCode(), ruleCompilerContext.getModuleName()))
				.thenReturn(engineRule);

		listener.afterCompile(ruleCompilerContext);

		verify(ruleCompilerContext).setRuleVersion(engineRule.getVersion());
	}
}
