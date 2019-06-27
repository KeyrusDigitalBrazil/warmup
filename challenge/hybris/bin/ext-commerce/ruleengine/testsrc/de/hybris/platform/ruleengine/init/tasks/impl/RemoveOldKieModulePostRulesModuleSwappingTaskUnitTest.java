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
package de.hybris.platform.ruleengine.init.tasks.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.init.RuleEngineKieModuleSwapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RemoveOldKieModulePostRulesModuleSwappingTaskUnitTest
{

	@InjectMocks
	private RemoveOldKieModulePostRulesModuleSwappingTask swappingTask;
	@Mock
	private RuleEngineKieModuleSwapper ruleEngineKieModuleSwapper;
	@Mock
	private RuleEngineActionResult ruleEngineActionResult;

	@Test
	public void testExecuteActionSucceeded()
	{
		when(ruleEngineActionResult.isActionFailed()).thenReturn(Boolean.FALSE);
		when(ruleEngineKieModuleSwapper.removeOldKieModuleIfPresent(ruleEngineActionResult)).thenReturn(Boolean.TRUE);
		final boolean success = swappingTask.execute(ruleEngineActionResult);
		assertThat(success).isTrue();
	}

	@Test
	public void testExecuteActionFailed()
	{
		when(ruleEngineActionResult.isActionFailed()).thenReturn(Boolean.TRUE);
		final boolean success = swappingTask.execute(ruleEngineActionResult);
		verify(ruleEngineKieModuleSwapper, times(0)).removeOldKieModuleIfPresent(ruleEngineActionResult);
		assertThat(success).isFalse();
	}

}
