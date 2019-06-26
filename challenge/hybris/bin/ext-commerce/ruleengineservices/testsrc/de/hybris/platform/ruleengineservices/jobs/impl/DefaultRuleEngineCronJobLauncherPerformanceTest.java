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

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.PerformanceTest;
import de.hybris.platform.core.TenantAwareThreadFactory;
import de.hybris.platform.ruleengineservices.init.AbstractSourceRulesAwareIT;
import de.hybris.platform.ruleengineservices.jobs.RuleEngineCronJobLauncher;
import de.hybris.platform.ruleengineservices.model.RuleEngineCronJobModel;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@PerformanceTest
public class DefaultRuleEngineCronJobLauncherPerformanceTest extends AbstractSourceRulesAwareIT
{
	private static final String TEST_TOUT = "ruleengineservices.test.jobs.launcher.timeout";

	@Resource
	private RuleEngineCronJobLauncher ruleEngineCronJobLauncher;
	@Resource
	private TenantAwareThreadFactory tenantAwareThreadFactory;
	@Resource
	private CronJobService cronJobService;
	@Resource
	private ModelService modelService;

	private Long testTimeout;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		testTimeout = getConfigurationService().getConfiguration().getLong(TEST_TOUT);
	}

	@Test
	public void testCompileAndDeployRulesAsync() throws Exception
	{
		final List<SourceRuleModel> rules = getRuleDao().findAllActiveRules().stream().map(r -> (SourceRuleModel) r)
				.collect(toList());
		stopwatch.start();
		final Supplier<RuleEngineCronJobModel> ruleEngineCronJobSupplier = () -> ruleEngineCronJobLauncher
				.triggerCompileAndPublish(rules, testKieModuleName, false);

		final CountDownLatch lock = new CountDownLatch(1);
		tenantAwareThreadFactory.newThread(new CronJobPollingMonitor(cronJobService, modelService, ruleEngineCronJobSupplier)
		{
			@Override
			public void onCronJobFinished()
			{
				lock.countDown();
			}
		}).start();
		assertThat(lock.await(testTimeout, TimeUnit.SECONDS)).isTrue();
	}
}
