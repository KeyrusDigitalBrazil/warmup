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
package de.hybris.platform.ruleengineservices.jobs.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.jalo.CronJobProgressTracker;
import de.hybris.platform.ruleengineservices.jobs.RuleEngineJobExecutionSynchronizer;
import de.hybris.platform.ruleengineservices.maintenance.RuleCompilerPublisherResult;
import de.hybris.platform.ruleengineservices.maintenance.RuleMaintenanceService;
import de.hybris.platform.ruleengineservices.maintenance.impl.DefaultRuleCompilerPublisherResult;
import de.hybris.platform.ruleengineservices.model.RuleEngineCronJobModel;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleEngineModuleInitJobPerformableUnitTest
{
	private static final String MODULE_NAME = "test_module";

	@InjectMocks
	private RuleEngineModuleInitJobPerformable jobPerformable;
	@Mock
	private RuleMaintenanceService ruleMaintenanceService;
	@Mock
	private RuleEngineCronJobModel ruleEngineCronJob;
	@Mock
	private CronJobProgressTracker cronJobProgressTracker;
	@Mock
	private ModelService modelService;
	@Mock
	private RuleEngineJobExecutionSynchronizer ruleEngineJobExecutionSynchronizer;

	@Before
	public void setUp()
	{
		jobPerformable = spy(jobPerformable);
		doReturn(cronJobProgressTracker).when(jobPerformable).createCronJobProgressTracker(ruleEngineCronJob);
		doNothing().when(jobPerformable).setTrackerProgress(eq(cronJobProgressTracker), any(Double.class));

		when(ruleEngineCronJob.getTargetModuleName()).thenReturn(MODULE_NAME);
	}

	@Test
	public void testPerformOk()
	{
		final RuleCompilerPublisherResult result = new DefaultRuleCompilerPublisherResult(RuleCompilerPublisherResult.Result.SUCCESS, newArrayList(), newArrayList());
		when(ruleMaintenanceService.initializeModule(MODULE_NAME, true)).thenReturn(result);

		final PerformResult performResult = jobPerformable.perform(ruleEngineCronJob);
		assertThat(performResult.getResult()).isEqualTo(CronJobResult.SUCCESS);
	}

	@Test
	public void testPerformCompilingFailure()
	{
		final RuleCompilerPublisherResult result = new DefaultRuleCompilerPublisherResult(RuleCompilerPublisherResult.Result.COMPILER_ERROR, newArrayList(), newArrayList());
		when(ruleMaintenanceService.initializeModule(MODULE_NAME, true)).thenReturn(result);

		final PerformResult performResult = jobPerformable.perform(ruleEngineCronJob);
		assertThat(performResult.getResult()).isEqualTo(CronJobResult.FAILURE);
	}

	@Test
	public void testPerformPublisherFailure()
	{
		final RuleCompilerPublisherResult result = new DefaultRuleCompilerPublisherResult(RuleCompilerPublisherResult.Result.PUBLISHER_ERROR, newArrayList(), newArrayList());
		when(ruleMaintenanceService.initializeModule(MODULE_NAME, true)).thenReturn(result);

		final PerformResult performResult = jobPerformable.perform(ruleEngineCronJob);
		assertThat(performResult.getResult()).isEqualTo(CronJobResult.FAILURE);
	}

	@Test
	public void testPerformGenericFailure()
	{
		doThrow(Exception.class).when(jobPerformable).performInternal(ruleEngineCronJob, cronJobProgressTracker);

		final PerformResult performResult = jobPerformable.perform(ruleEngineCronJob);
		assertThat(performResult.getResult()).isEqualTo(CronJobResult.FAILURE);
	}

}
