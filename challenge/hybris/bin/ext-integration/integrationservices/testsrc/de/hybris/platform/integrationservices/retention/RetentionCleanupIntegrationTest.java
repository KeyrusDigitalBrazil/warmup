/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.integrationservices.retention;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelDoesNotExist;
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelExists;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.model.IntegrationApiMediaModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@IntegrationTest
public class RetentionCleanupIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = LoggerFactory.getLogger(RetentionCleanupIntegrationTest.class);
	private static final String INTEGRATION_API_MEDIA_CLEANUP_CRON_JOB_NAME = "integrationApiMediaCleanupCronJob";

	@Resource
	private CronJobService cronJobService;

	@Before
	public void setUp() throws ImpExException
	{
		importData("/impex/essentialdata-item-cleanup-jobs.impex", "UTF-8");
	}

	@Test
	public void testCleanupRuleCleansUpIntegrationApiMediaOlderThanRetentionPeriod() throws ImpExException
	{
		// for IntegrationApiMedia
		importData("/impex/essentialdata-item-cleanup-jobs.impex", "UTF-8");

		final String oneWeekAgo = LocalDateTime.now().minusDays(7).minusHours(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		final String[] oldIntegrationApiMedia = {
				"INSERT_UPDATE IntegrationApiMedia; code[unique=true]; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"; integrationApiMedia1; " + oneWeekAgo
		};
		IntegrationTestUtil.importImpEx(oldIntegrationApiMedia);

		assertModelExists(integrationApiMedia("integrationApiMedia1"));

		executeCronJob(INTEGRATION_API_MEDIA_CLEANUP_CRON_JOB_NAME);

		assertModelDoesNotExist(integrationApiMedia("integrationApiMedia1"));
	}

	private void executeCronJob(final String cronJobName)
	{
		final CronJobModel cronJob = cronJobService.getCronJob(cronJobName);
		LOG.info("Performing cronJob {} synchronously", cronJob.getCode());
		cronJobService.performCronJob(cronJob, true);
		LOG.info("CronJob completed with status {}", cronJob.getStatus());
	}

	private IntegrationApiMediaModel integrationApiMedia(final String code)
	{
		final IntegrationApiMediaModel media = new IntegrationApiMediaModel();
		media.setCode(code);
		return media;
	}
}
