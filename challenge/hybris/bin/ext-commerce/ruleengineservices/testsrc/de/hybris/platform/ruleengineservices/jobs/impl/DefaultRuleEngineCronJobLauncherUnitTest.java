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

import com.google.common.collect.Lists;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.dao.RulesModuleDao;
import de.hybris.platform.ruleengine.model.AbstractRulesModuleModel;
import de.hybris.platform.ruleengineservices.jobs.RuleEngineCronJobSupplierFactory;
import de.hybris.platform.ruleengineservices.jobs.RuleEngineJobService;
import de.hybris.platform.ruleengineservices.model.RuleEngineCronJobModel;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.function.Supplier;

import static de.hybris.platform.ruleengineservices.jobs.impl.DefaultRuleEngineCronJobLauncher.ALL_MODULES_INIT_JOB_CODE;
import static de.hybris.platform.ruleengineservices.jobs.impl.DefaultRuleEngineCronJobLauncher.ALL_MODULES_INIT_PERFORMABLE_BEAN_NAME;
import static de.hybris.platform.ruleengineservices.jobs.impl.DefaultRuleEngineCronJobLauncher.ARCHIVE_JOB_CODE;
import static de.hybris.platform.ruleengineservices.jobs.impl.DefaultRuleEngineCronJobLauncher.ARCHIVE_JOB_PERFORMABLE_BEAN_NAME;
import static de.hybris.platform.ruleengineservices.jobs.impl.DefaultRuleEngineCronJobLauncher.COMPILE_PUBLISH_JOB_CODE_TEMPLATE;
import static de.hybris.platform.ruleengineservices.jobs.impl.DefaultRuleEngineCronJobLauncher.COMPILE_PUBLISH_PERFORMABLE_BEAN_NAME;
import static de.hybris.platform.ruleengineservices.jobs.impl.DefaultRuleEngineCronJobLauncher.MODULES_SYNCH_JOB_CODE_TEMPLATE;
import static de.hybris.platform.ruleengineservices.jobs.impl.DefaultRuleEngineCronJobLauncher.MODULES_SYNCH_PERFORMABLE_BEAN_NAME;
import static de.hybris.platform.ruleengineservices.jobs.impl.DefaultRuleEngineCronJobLauncher.MODULE_INIT_JOB_CODE_TEMPLATE;
import static de.hybris.platform.ruleengineservices.jobs.impl.DefaultRuleEngineCronJobLauncher.MODULE_INIT_PERFORMABLE_BEAN_NAME;
import static de.hybris.platform.ruleengineservices.jobs.impl.DefaultRuleEngineCronJobLauncher.UNDEPLOY_JOB_CODE_TEMPLATE;
import static de.hybris.platform.ruleengineservices.jobs.impl.DefaultRuleEngineCronJobLauncher.UNDEPLOY_PERFORMABLE_BEAN_NAME;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRuleEngineCronJobLauncherUnitTest
{

	private static final String SRC_MODULE_NAME = "test_src__module";
	private static final String MODULE_NAME = "test_module";

	@Mock
	private AbstractRulesModuleModel srcRulesModule;
	@Mock
	private SourceRuleModel sourceRule;
	@Mock
	private AbstractRulesModuleModel rulesModule;
	@Mock
	private RulesModuleDao rulesModuleDao;
	@Mock
	private RuleEngineJobService ruleEngineJobService;
	@Mock(answer = Answers.CALLS_REAL_METHODS)
	private RuleEngineCronJobSupplierFactory ruleEngineCronJobSupplierFactory;
	@Mock
	private L10NService l10NService;
	@InjectMocks
	private DefaultRuleEngineCronJobLauncher ruleEngineCronJobLauncher;

	@Before
	public void setUp()
	{
		when(rulesModule.getActive()).thenReturn(true);
		when(srcRulesModule.getActive()).thenReturn(true);
		when(rulesModule.getName()).thenReturn(MODULE_NAME);
		when(srcRulesModule.getName()).thenReturn(SRC_MODULE_NAME);
		when(rulesModuleDao.findAll()).thenReturn(Lists.newArrayList(srcRulesModule, rulesModule));
		when(l10NService.getLocalizedString(eq("rule.cronjob.launcher.limit.error"), any()))
				.thenReturn("Another instance of the rule engine job is currently executing, retry later");
		ruleEngineCronJobLauncher.setMaximumNumberOfParallelCronJobs(1);
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenTriggerCompileAndPublishForNullModuleName()
	{
		assertThatThrownBy(() -> ruleEngineCronJobLauncher
				.triggerCompileAndPublish(Collections.emptyList(), null, true)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Parameter moduleName can not be null");
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenTriggerCompileAndPublishForNullRules()
	{
		assertThatThrownBy(() -> ruleEngineCronJobLauncher
				.triggerCompileAndPublish(null, MODULE_NAME, true)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Parameter rules can not be null");
	}

	@Test
	public void testTriggerCompileAndPublish()
	{
		final RuleEngineCronJobModel ruleEngineCronJob = new RuleEngineCronJobModel();
		final String jobCode = format(COMPILE_PUBLISH_JOB_CODE_TEMPLATE, MODULE_NAME);
		when(ruleEngineJobService
				.triggerCronJob(eq(jobCode), eq(COMPILE_PUBLISH_PERFORMABLE_BEAN_NAME),
						any(Supplier.class))).thenReturn(ruleEngineCronJob);
		final RuleEngineCronJobModel triggeredRuleEngineCronJob = ruleEngineCronJobLauncher
				.triggerCompileAndPublish(Collections.emptyList(), MODULE_NAME, true);
		assertThat(triggeredRuleEngineCronJob).isEqualTo(ruleEngineCronJob);
	}

	@Test
	public void testTriggerCompileAndPublishJobsRunning()
	{
		when(ruleEngineJobService.countRunningJobs(format(UNDEPLOY_JOB_CODE_TEMPLATE, MODULE_NAME))).thenReturn(1);
		assertThatThrownBy(() -> ruleEngineCronJobLauncher
				.triggerCompileAndPublish(Collections.emptyList(), MODULE_NAME, true)).isInstanceOf(IllegalStateException.class)
				.hasMessage("Another instance of the rule engine job is currently executing, retry later");
	}

	@Test
	public void testTriggerUndeployRules()
	{
		final RuleEngineCronJobModel ruleEngineCronJob = new RuleEngineCronJobModel();
		final String jobCode = format(UNDEPLOY_JOB_CODE_TEMPLATE, MODULE_NAME);
		when(ruleEngineJobService
				.triggerCronJob(eq(jobCode), eq(UNDEPLOY_PERFORMABLE_BEAN_NAME),
						any(Supplier.class))).thenReturn(ruleEngineCronJob);
		final RuleEngineCronJobModel triggeredRuleEngineCronJob = ruleEngineCronJobLauncher
				.triggerUndeployRules(Collections.emptyList(), MODULE_NAME);
		assertThat(triggeredRuleEngineCronJob).isEqualTo(ruleEngineCronJob);
	}

	@Test
	public void testTriggerUndeployRulesJobsRunning()
	{
		when(ruleEngineJobService.countRunningJobs(format(COMPILE_PUBLISH_JOB_CODE_TEMPLATE, MODULE_NAME))).thenReturn(1);
		assertThatThrownBy(() -> ruleEngineCronJobLauncher
				.triggerUndeployRules(Collections.emptyList(), MODULE_NAME))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(
						"Another instance of the rule engine job is currently executing, retry later");
	}

	@Test
	public void testTriggerSyncronizeModules()
	{
		final RuleEngineCronJobModel ruleEngineCronJob = new RuleEngineCronJobModel();
		final String jobCode = format(MODULES_SYNCH_JOB_CODE_TEMPLATE, SRC_MODULE_NAME, MODULE_NAME);
		when(ruleEngineJobService
				.triggerCronJob(eq(jobCode), eq(MODULES_SYNCH_PERFORMABLE_BEAN_NAME),
						any(Supplier.class))).thenReturn(ruleEngineCronJob);
		final RuleEngineCronJobModel triggeredRuleEngineCronJob = ruleEngineCronJobLauncher
				.triggerSynchronizeModules(SRC_MODULE_NAME, MODULE_NAME);
		assertThat(triggeredRuleEngineCronJob).isEqualTo(ruleEngineCronJob);
	}

	@Test
	public void testTriggerSyncronizeModulesJobsRunning()
	{
		when(ruleEngineJobService.countRunningJobs(format(COMPILE_PUBLISH_JOB_CODE_TEMPLATE, MODULE_NAME))).thenReturn(1);
		assertThatThrownBy(() -> ruleEngineCronJobLauncher
				.triggerSynchronizeModules(SRC_MODULE_NAME, MODULE_NAME))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(
						"Another instance of the rule engine job is currently executing, retry later");
	}

	@Test
	public void testTriggerModuleInitialization()
	{
		final RuleEngineCronJobModel ruleEngineCronJob = new RuleEngineCronJobModel();
		final String jobCode = format(MODULE_INIT_JOB_CODE_TEMPLATE, MODULE_NAME);
		when(ruleEngineJobService
				.triggerCronJob(eq(jobCode), eq(MODULE_INIT_PERFORMABLE_BEAN_NAME),
						any(Supplier.class))).thenReturn(ruleEngineCronJob);
		final RuleEngineCronJobModel triggeredRuleEngineCronJob = ruleEngineCronJobLauncher
				.triggerModuleInitialization(MODULE_NAME);
		assertThat(triggeredRuleEngineCronJob).isEqualTo(ruleEngineCronJob);
	}

	@Test
	public void testTriggerModuleInitializationJobsRunning()
	{
		when(ruleEngineJobService.countRunningJobs(format(COMPILE_PUBLISH_JOB_CODE_TEMPLATE, MODULE_NAME))).thenReturn(1);
		assertThatThrownBy(() -> ruleEngineCronJobLauncher
				.triggerModuleInitialization(MODULE_NAME))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(
						"Another instance of the rule engine job is currently executing, retry later");
	}

	@Test
	public void testTriggerAllModulesInitialization()
	{
		final RuleEngineCronJobModel ruleEngineCronJob = new RuleEngineCronJobModel();
		when(ruleEngineJobService
				.triggerCronJob(eq(ALL_MODULES_INIT_JOB_CODE), eq(ALL_MODULES_INIT_PERFORMABLE_BEAN_NAME),
						any(Supplier.class))).thenReturn(ruleEngineCronJob);
		final RuleEngineCronJobModel triggeredRuleEngineCronJob = ruleEngineCronJobLauncher
				.triggerAllModulesInitialization();
		assertThat(triggeredRuleEngineCronJob).isEqualTo(ruleEngineCronJob);
	}

	@Test
	public void testTriggerAllModulesInitializationJobsRunning()
	{
		when(ruleEngineJobService.countRunningJobs(format(COMPILE_PUBLISH_JOB_CODE_TEMPLATE, MODULE_NAME))).thenReturn(1);
		assertThatThrownBy(() -> ruleEngineCronJobLauncher
				.triggerAllModulesInitialization())
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(
						"Another instance of the rule engine job is currently executing, retry later");
	}

	@Test
	public void testTriggerArchiveRule()
	{
		final RuleEngineCronJobModel ruleEngineCronJob = new RuleEngineCronJobModel();

		when(ruleEngineJobService
				.triggerCronJob(eq(ARCHIVE_JOB_CODE), eq(ARCHIVE_JOB_PERFORMABLE_BEAN_NAME),
						any(Supplier.class))).thenReturn(ruleEngineCronJob);

		final RuleEngineCronJobModel triggeredRuleEngineCronJob = ruleEngineCronJobLauncher
				.triggerArchiveRule(sourceRule);

		assertThat(triggeredRuleEngineCronJob).isEqualTo(ruleEngineCronJob);
	}

	@Test
	public void testTriggerArchiveRuleJobsRunning()
	{
		when(ruleEngineJobService.countRunningJobs(ARCHIVE_JOB_CODE)).thenReturn(1);

		assertThatThrownBy(() -> ruleEngineCronJobLauncher
				.triggerArchiveRule(sourceRule)).isInstanceOf(IllegalStateException.class)
				.hasMessage("Another instance of the rule engine job is currently executing, retry later");
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenTriggerArchiveRuleForNullRule()
	{
		assertThatThrownBy(() -> ruleEngineCronJobLauncher
				.triggerArchiveRule(null)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Parameter rule can not be null");
	}

}
