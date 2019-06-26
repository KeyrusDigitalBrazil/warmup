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
package de.hybris.platform.inboundservices.retention;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelDoesNotExist;
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelExists;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.inboundservices.model.InboundRequestErrorModel;
import de.hybris.platform.inboundservices.model.InboundRequestMediaModel;
import de.hybris.platform.inboundservices.model.InboundRequestModel;
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
public class InboundRequestRetentionCleanupIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = LoggerFactory.getLogger(InboundRequestRetentionCleanupIntegrationTest.class);
	private static final String INBOUND_REQUEST_CLEANUP_CRON_JOB_NAME = "inboundRequestCleanupCronJob";
	private static final String INBOUND_REQUEST_MEDIA_CLEANUP_CRON_JOB_NAME = "inboundRequestMediaCleanupCronJob";
	private static final String INTEGRATION_API_MEDIA_CLEANUP_CRON_JOB_NAME = "integrationApiMediaCleanupCronJob";

	@Resource
	private CronJobService cronJobService;

	@Before
	public void setUp() throws ImpExException
	{
		importData("/impex/essentialdata-inbound-item-cleanup-jobs.impex", "UTF-8");
	}

	@Test
	public void testCleanupRuleCleansUpInboundRequestOlderThanRetentionPeriod() throws ImpExException
	{
		final String oneWeekAgo = LocalDateTime.now().minusDays(7).minusHours(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		final String[] oldInboundRequest = {
				"INSERT_UPDATE InboundRequest; type; status(code); integrationKey[unique=true]; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"; Product; SUCCESS; old; " + oneWeekAgo
		};
		IntegrationTestUtil.importImpEx(oldInboundRequest);
		assertModelExists(inboundRequest("old"));

		executeCronJob(INBOUND_REQUEST_CLEANUP_CRON_JOB_NAME);

		assertModelDoesNotExist(inboundRequest("old"));
	}

	@Test
	public void testCleanupRuleDoesNotCleanNewInboundRequest() throws ImpExException
	{
		final String sixDaysAgo = LocalDateTime.now().minusDays(6).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		final String[] newInboundRequest = {
				"INSERT_UPDATE InboundRequest; type; status(code); integrationKey[unique=true]; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"; Category; ERROR; new; " + sixDaysAgo
		};
		IntegrationTestUtil.importImpEx(newInboundRequest);
		assertModelExists(inboundRequest("new"));

		executeCronJob(INBOUND_REQUEST_CLEANUP_CRON_JOB_NAME);

		assertModelExists(inboundRequest("new"));
	}

	@Test
	public void testInboundRequestCleanupAlsoCleansInboundRequestErrors() throws ImpExException
	{
		final String olderThanAWeek = LocalDateTime.now().minusDays(7).minusHours(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		final String[] oldInboundRequest = {
				"INSERT_UPDATE InboundRequest; type; status(code); integrationKey[unique=true]; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"; B2BUnit; ERROR; old_with_error; " + olderThanAWeek,
				"INSERT_UPDATE InboundRequestError; code[unique=true]; message; inboundRequest(integrationKey)",
				"; some_error; A detailed error message; old_with_error"
		};
		IntegrationTestUtil.importImpEx(oldInboundRequest);
		assertModelExists(inboundRequest("old_with_error"));
		assertModelExists(requestError("some_error"));

		executeCronJob(INBOUND_REQUEST_CLEANUP_CRON_JOB_NAME);

		assertModelDoesNotExist(inboundRequest("old_with_error"));
		assertModelDoesNotExist(requestError("some_error"));
	}

	@Test
	public void testCleanupRuleCleansUpInboundRequestMediaOlderThanRetentionPeriod() throws ImpExException
	{
		final String oneWeekAgo = LocalDateTime.now().minusDays(7).minusHours(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		final String[] oldInboundRequestMedia = {
				"INSERT_UPDATE InboundRequestMedia; code[unique=true]; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"; inboundRequestMedia1; " + oneWeekAgo
		};
		IntegrationTestUtil.importImpEx(oldInboundRequestMedia);

		assertModelExists(inboundRequestMedia("inboundRequestMedia1"));

		executeCronJob(INBOUND_REQUEST_MEDIA_CLEANUP_CRON_JOB_NAME);

		assertModelDoesNotExist(inboundRequestMedia("inboundRequestMedia1"));
	}

	@Test
	public void testCleanupRuleCleansUpIntegrationApiMediaOlderThanRetentionPeriod_notIncludingInboundRequestMedia() throws ImpExException
	{
		// for IntegrationApiMedia
		importData("/impex/essentialdata-item-cleanup-jobs.impex", "UTF-8");

		final String oneWeekAgo = LocalDateTime.now().minusDays(7).minusHours(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		final String[] oldInboundRequestMedia = {
				"INSERT_UPDATE InboundRequestMedia; code[unique=true]; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"; inboundRequestMedia1; " + oneWeekAgo
		};
		final String[] oldIntegrationApiMedia = {
				"INSERT_UPDATE IntegrationApiMedia; code[unique=true]; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"; integrationApiMedia1; " + oneWeekAgo
		};
		IntegrationTestUtil.importImpEx(oldInboundRequestMedia);
		IntegrationTestUtil.importImpEx(oldIntegrationApiMedia);

		assertModelExists(inboundRequestMedia("inboundRequestMedia1"));
		assertModelExists(integrationApiMedia("integrationApiMedia1"));

		executeCronJob(INTEGRATION_API_MEDIA_CLEANUP_CRON_JOB_NAME);

		assertModelExists(inboundRequestMedia("inboundRequestMedia1"));
		assertModelDoesNotExist(integrationApiMedia("integrationApiMedia1"));
	}

	private void executeCronJob(final String cronJobName)
	{
		final CronJobModel cronJob = cronJobService.getCronJob(cronJobName);
		LOG.info("Performing cronJob {} synchronously", cronJob.getCode());
		cronJobService.performCronJob(cronJob, true);
		LOG.info("CronJob completed with status {}", cronJob.getStatus());
	}

	private InboundRequestModel inboundRequest(final String integrationKey)
	{
		final InboundRequestModel inboundRequest = new InboundRequestModel();
		inboundRequest.setIntegrationKey(integrationKey);
		return inboundRequest;
	}

	private InboundRequestMediaModel inboundRequestMedia(final String code)
	{
		final InboundRequestMediaModel media = new InboundRequestMediaModel();
		media.setCode(code);
		return media;
	}

	private IntegrationApiMediaModel integrationApiMedia(final String code)
	{
		final IntegrationApiMediaModel media = new IntegrationApiMediaModel();
		media.setCode(code);
		return media;
	}

	private InboundRequestErrorModel requestError(final String errorCode)
	{
		final InboundRequestErrorModel requestError = new InboundRequestErrorModel();
		requestError.setCode(errorCode);
		return requestError;
	}
}
