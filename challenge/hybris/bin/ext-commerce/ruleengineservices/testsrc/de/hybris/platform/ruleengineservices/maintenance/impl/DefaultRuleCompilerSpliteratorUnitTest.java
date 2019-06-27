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
package de.hybris.platform.ruleengineservices.maintenance.impl;

import static com.google.common.collect.Lists.newCopyOnWriteArrayList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.ruleengine.concurrency.RuleEngineSpliteratorStrategy;
import de.hybris.platform.ruleengine.concurrency.SuspendResumeTaskManager;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerResult;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerService;
import de.hybris.platform.ruleengineservices.maintenance.RuleCompilationContext;
import de.hybris.platform.ruleengineservices.maintenance.RuleCompilerSpliterator;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;

import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRuleCompilerSpliteratorUnitTest
{
	private static final String MODULE_NAME = "MODULE_NAME";

	private RuleCompilerSpliterator<SourceRuleModel> ruleCompilerSpliterator;
	@Mock
	private RuleCompilationContext ruleCompilationContext;
	@Mock
	private RuleCompilerService ruleCompilerService;
	@Mock
	private RuleCompilerResult ruleCompilerResult;
	@Mock
	private Tenant tenant;
	@Mock
	private ThreadFactory threadFactory;
	@Mock
	private SuspendResumeTaskManager suspendResumeTaskManager;

	@Before
	public void setUp()
	{
		when(ruleCompilationContext.getThreadTimeout()).thenReturn(1000L);
		when(ruleCompilationContext.getNumberOfThreads()).thenReturn(10);
		when(ruleCompilationContext.getCurrentTenant()).thenReturn(tenant);
		when(ruleCompilationContext.getThreadFactory()).thenReturn(threadFactory);
		when(ruleCompilationContext.getRuleCompilerService()).thenReturn(ruleCompilerService);
		when(ruleCompilationContext.getSuspendResumeTaskManager()).thenReturn(suspendResumeTaskManager);
		when(ruleCompilerService.compile(eq(ruleCompilationContext), any(SourceRuleModel.class), eq(MODULE_NAME)))
					 .thenReturn(ruleCompilerResult);
		when(ruleCompilerResult.getResult()).thenReturn(RuleCompilerResult.Result.SUCCESS);
		ruleCompilerSpliterator = DefaultRuleCompilerSpliterator.withCompilationContext(ruleCompilationContext);
	}

	@Test
	public void testGetPartitionSize()
	{
		assertThat(getPartitionSize(0, 9)).isEqualTo(0);
		assertThat(getPartitionSize(1, 9)).isEqualTo(1);
		assertThat(getPartitionSize(9, 9)).isEqualTo(9);
		assertThat(getPartitionSize(81, 9)).isEqualTo(9);
		assertThat(getPartitionSize(100, 9)).isEqualTo(12);
	}

	@Test
	public void testCompileSingleRule()
	{
		final RuleCompilerResult result = ruleCompilerSpliterator.compileSingleRule(new SourceRuleModel(), MODULE_NAME);
		assertThat(result).isNotNull();
		assertThat(result.getResult()).isEqualByComparingTo(RuleCompilerResult.Result.SUCCESS);
	}

	@Test
	public void testCompileRuleAsync() throws InterruptedException
	{
		final List<SourceRuleModel> ruleModelList = Stream.generate(SourceRuleModel::new).limit(100).collect(toList());
		final List<RuleCompilerResult> ruleCompilerResults = newCopyOnWriteArrayList();
		final List<Supplier<Thread>> workerSuppliers = Stream.generate(() -> getWorkerSupplier(ruleCompilerResults)).limit(10)
				.collect(toList());

		OngoingStubbing<Thread> whenCreateThreadCall = when(
				tenant.createAndRegisterBackgroundThread(any(Runnable.class), eq(threadFactory)));

		for (Supplier<Thread> supplier : workerSuppliers)
		{
			whenCreateThreadCall = whenCreateThreadCall.thenReturn(supplier.get());
		}
		assertThat(ruleCompilerResults).hasSize(0);

		ruleCompilerSpliterator.compileRulesAsync(ruleModelList, MODULE_NAME).getTaskResult();

		assertThat(ruleCompilerResults).hasSize(10);
		assertThat(
				ruleCompilerResults.stream().filter(r -> !r.getResult().equals(RuleCompilerResult.Result.SUCCESS)).collect(toList()))
						.isEmpty();
	}

	private RuleCompilerResult getSuccessfulRuleCompilerResult()
	{
		final RuleCompilerResult result = mock(RuleCompilerResult.class);
		when(result.getResult()).thenReturn(RuleCompilerResult.Result.SUCCESS);
		return result;
	}

	private Supplier<Thread> getWorkerSupplier(final List<RuleCompilerResult> ruleCompilerResults)
	{
		return () -> new Thread(() -> ruleCompilerResults.add(getSuccessfulRuleCompilerResult()));
	}

	private int getPartitionSize(final int listSize, final int numOfPartitions)
	{
		return RuleEngineSpliteratorStrategy.getPartitionSize(listSize, numOfPartitions);
	}
}
