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
package de.hybris.platform.outboundservices.retention;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelDoesNotExist;
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelExists;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.model.IntegrationApiMediaModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.outboundservices.model.OutboundRequestMediaModel;
import de.hybris.platform.outboundservices.model.OutboundRequestModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;

@IntegrationTest
public class OutboundRequestRetentionCleanupIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = LoggerFactory.getLogger(OutboundRequestRetentionCleanupIntegrationTest.class);
	private static final String OUTBOUND_REQUEST_CLEANUP_CRON_JOB_NAME = "outboundRequestCleanupCronJob";
	private static final String OUTBOUND_REQUEST_MEDIA_CLEANUP_CRON_JOB_NAME = "outboundRequestMediaCleanupCronJob";
	private static final String INTEGRATION_API_MEDIA_CLEANUP_CRON_JOB_NAME = "integrationApiMediaCleanupCronJob";

	@Resource
	private CronJobService cronJobService;

	@Before
	public void setUp() throws ImpExException
	{
		importData("/impex/essentialdata-outbound-item-cleanup-jobs.impex", "UTF-8");
	}

	@Test
	public void testCleanupRuleCleansUpOutboundRequestOlderThanRetentionPeriod() throws ImpExException
	{
		final String oneWeekAgo = LocalDateTime.now().minusDays(7).minusHours(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		final String[] oldOutboundRequest = {
				"INSERT_UPDATE OutboundRequest; type; status(code); integrationKey[unique=true]; destination; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"; Product; SUCCESS; old; adestination;" + oneWeekAgo
		};
		IntegrationTestUtil.importImpEx(oldOutboundRequest);
		assertModelExists(outboundRequest("old"));

		executeCronJob(OUTBOUND_REQUEST_CLEANUP_CRON_JOB_NAME);

		assertModelDoesNotExist(outboundRequest("old"));
	}

	@Test
	public void testCleanupRuleDoesNotCleanNewOutboundRequest() throws ImpExException
	{
		final String sixDaysAgo = LocalDateTime.now().minusDays(6).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		final String[] newOutboundRequest = {
				"INSERT_UPDATE OutboundRequest; type; status(code); integrationKey[unique=true]; destination; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"; Category; ERROR; new; adestination; " + sixDaysAgo
		};
		IntegrationTestUtil.importImpEx(newOutboundRequest);
		assertModelExists(outboundRequest("new"));

		executeCronJob(OUTBOUND_REQUEST_CLEANUP_CRON_JOB_NAME);

		assertModelExists(outboundRequest("new"));
	}

	@Test
	public void testCleanupRuleCleansUpOutboundRequestMediaOlderThanRetentionPeriod() throws ImpExException
	{
		final String oneWeekAgo = LocalDateTime.now().minusDays(7).minusHours(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		final String[] oldOutboundRequestMedia = {
				"INSERT_UPDATE OutboundRequestMedia; code[unique=true]; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"; outboundRequestMedia1; " + oneWeekAgo
		};
		IntegrationTestUtil.importImpEx(oldOutboundRequestMedia);

		assertModelExists(outboundRequestMedia("outboundRequestMedia1"));

		executeCronJob(OUTBOUND_REQUEST_MEDIA_CLEANUP_CRON_JOB_NAME);

		assertModelDoesNotExist(outboundRequestMedia("outboundRequestMedia1"));
	}

	@Test
	public void testCleanupRuleCleansUpIntegrationApiMediaOlderThanRetentionPeriod_notIncludingOutboundRequestMedia() throws ImpExException
	{
		// for IntegrationApiMedia
		importData("/impex/essentialdata-item-cleanup-jobs.impex", "UTF-8");

		final String oneWeekAgo = LocalDateTime.now().minusDays(7).minusHours(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		final String[] oldOutboundRequestMedia = {
				"INSERT_UPDATE OutboundRequestMedia; code[unique=true]; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"; outboundRequestMedia1; " + oneWeekAgo
		};
		final String[] oldIntegrationApiMedia = {
				"INSERT_UPDATE IntegrationApiMedia; code[unique=true]; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"; integrationApiMedia1; " + oneWeekAgo
		};
		IntegrationTestUtil.importImpEx(oldOutboundRequestMedia);
		IntegrationTestUtil.importImpEx(oldIntegrationApiMedia);

		assertModelExists(outboundRequestMedia("outboundRequestMedia1"));
		assertModelExists(integrationApiMedia("integrationApiMedia1"));

		executeCronJob(INTEGRATION_API_MEDIA_CLEANUP_CRON_JOB_NAME);

		assertModelExists(outboundRequestMedia("outboundRequestMedia1"));
		assertModelDoesNotExist(integrationApiMedia("integrationApiMedia1"));
	}

	private void executeCronJob(final String cronJobName)
	{
		final CronJobModel cronJob = cronJobService.getCronJob(cronJobName);
		LOG.info("Performing cronJob {} synchronously", cronJob.getCode());
		cronJobService.performCronJob(cronJob, true);
		LOG.info("CronJob completed with status {}", cronJob.getStatus());
	}

	private OutboundRequestModel outboundRequest(final String integrationKey)
	{
		final OutboundRequestModel outboundRequest = new OutboundRequestModel();
		outboundRequest.setIntegrationKey(integrationKey);
		return outboundRequest;
	}

	private IntegrationApiMediaModel integrationApiMedia(final String code)
	{
		final IntegrationApiMediaModel media = new IntegrationApiMediaModel();
		media.setCode(code);
		return media;
	}

	private OutboundRequestMediaModel outboundRequestMedia(final String code)
	{
		final OutboundRequestMediaModel media = new OutboundRequestMediaModel();
		media.setCode(code);
		return media;
	}
}
