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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.TenantAwareThreadFactory;
import de.hybris.platform.ruleengineservices.jobs.RuleEngineJobService;
import de.hybris.platform.ruleengineservices.model.RuleEngineCronJobModel;
import de.hybris.platform.ruleengineservices.model.RuleEngineJobModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@IntegrationTest
public class DefaultRuleEngineJobServiceTest extends ServicelayerTest
{
	private static final String JOB_CODE = "ruleEngineJob1";
	private static final String JOB_PERFORMABLE_BEAN_NAME = "ruleEngineCompilePublishJobPerformable";
	private static final String TEST_MODULE_NAME = "test_module";
	private static final long ASYNC_TIMEOUT = 10;


	@Resource
	private RuleEngineJobService ruleEngineJobService;
	@Resource
	private CronJobService cronJobService;
	@Resource
	private TenantAwareThreadFactory tenantAwareThreadFactory;
	@Resource
	private ModelService modelService;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/ruleengineservices/test/jobs/test-ruleengineservices-cronjobs.impex", "UTF-8");
	}

	@Test
	public void testGetRuleEngineJob()
	{
		final RuleEngineJobModel ruleEngineJob = ruleEngineJobService.getRuleEngineJob(JOB_CODE, JOB_PERFORMABLE_BEAN_NAME);
		assertThat(ruleEngineJob).isNotNull();
		assertThat(ruleEngineJob.getCronJobs()).hasSize(4);
	}

	@Test
	public void testIsRunningTrue()
	{
		assertThat(ruleEngineJobService.isRunning(JOB_CODE)).isTrue();
	}

	@Test
	public void testIsRunningFalse() throws Exception
	{
		importCsv("/ruleengineservices/test/jobs/test-ruleengineservices-cronjobs-allfinished.impex", "UTF-8");
		assertThat(ruleEngineJobService.isRunning(JOB_CODE)).isFalse();
	}
	

	@Test
	public void testRunCronJobIfAllowedNoSupplier()
	{
		assertThatThrownBy(() -> ruleEngineJobService.triggerCronJob(JOB_CODE, JOB_PERFORMABLE_BEAN_NAME, null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Cron Job supplier for RuleEngineCronJobModel should be provided");
	}

	@Test
	public void testRunCronJobIfAllowedOk() throws Exception
	{
		importCsv("/ruleengineservices/test/jobs/test-ruleengineservices-cronjobs-allfinished.impex", "UTF-8");
		final CountDownLatch lock = new CountDownLatch(1);
		final Supplier<RuleEngineCronJobModel> ruleEngineCronJobSupplier = () -> ruleEngineJobService
				.triggerCronJob(JOB_CODE, JOB_PERFORMABLE_BEAN_NAME, () -> {

					final RuleEngineCronJobModel compilePublishCronJob = new RuleEngineCronJobModel();
					compilePublishCronJob.setSourceRules(Collections.emptyList());
					compilePublishCronJob.setTargetModuleName(TEST_MODULE_NAME);
					compilePublishCronJob.setEnableIncrementalUpdate(false);
					return compilePublishCronJob;
				});

		final Thread cronJobMonitorThread = tenantAwareThreadFactory.newThread(new CronJobPollingMonitor(cronJobService, modelService, ruleEngineCronJobSupplier)
		{
			@Override
			public void onCronJobFinished()
			{
				lock.countDown();
			}
		});
		cronJobMonitorThread.start();
		assertThat(lock.await(ASYNC_TIMEOUT, TimeUnit.SECONDS)).isTrue();
	}

}
