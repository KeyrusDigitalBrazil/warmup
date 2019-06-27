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

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.init.tasks.PostRulesModuleSwappingTask;

import java.util.List;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPostRulesModuleSwappingTasksProviderUnitTest
{
	private DefaultPostRulesModuleSwappingTasksProvider tasksProvider;
	@Mock
	private RuleEngineActionResult ruleEngineActionResult;
	@Mock
	private PostRulesModuleSwappingTask task1;
	@Mock
	private PostRulesModuleSwappingTask task2;

	@Before
	public void setUp()
	{
		tasksProvider = new DefaultPostRulesModuleSwappingTasksProvider();
		tasksProvider.setPostRulesModuleSwappingTasks(newArrayList(task1, task2));

		when(task1.execute(ruleEngineActionResult)).thenReturn(Boolean.TRUE);
		when(task2.execute(ruleEngineActionResult)).thenReturn(Boolean.FALSE);
	}

	@Test
	public void testGetTasks()
	{
		final List<Supplier<Object>> supplierList = tasksProvider.getTasks(ruleEngineActionResult);
		final List<Object> taskResults = supplierList.stream().map(Supplier::get).collect(toList());
		verify(task1).execute(ruleEngineActionResult);
		verify(task2).execute(ruleEngineActionResult);
		assertThat(taskResults).containsAll(Lists.newArrayList(Boolean.TRUE, Boolean.FALSE));
	}

}
