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

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.ruleengineservices.jobs.RuleEngineCronJobDAO;
import de.hybris.platform.ruleengineservices.model.RuleEngineJobModel;
import de.hybris.platform.servicelayer.ServicelayerTest;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultRuleEngineCronJobDAOTest extends ServicelayerTest
{
	private static final String JOB_CODE = "ruleEngineJob1";
	

	@Resource
	private RuleEngineCronJobDAO ruleEngineCronJobDAO;
	
	@Before
	public void setUp() throws Exception
	{
		importCsv("/ruleengineservices/test/jobs/test-ruleengineservices-cronjobs.impex", "UTF-8");
	}

	@Test
	public void testCountCronJobsByJobOnlyRunning()
	{
		final int numRunning = ruleEngineCronJobDAO.countCronJobsByJob(JOB_CODE, CronJobStatus.RUNNING);
		assertThat(numRunning).isEqualTo(1);
	}

	@Test
	public void testCountCronJobsByJobAll()
	{
		final int numRunning = ruleEngineCronJobDAO.countCronJobsByJob(JOB_CODE);
		assertThat(numRunning).isEqualTo(4);
	}

	@Test
	public void testGetOrCreateRuleEngineJobGetExisting()
	{
		final RuleEngineJobModel ruleEngineJob = ruleEngineCronJobDAO.findRuleEngineJobByCode(JOB_CODE);
		assertThat(ruleEngineJob).isNotNull();
		assertThat(ruleEngineJob.getCronJobs()).hasSize(4);
	}

}
