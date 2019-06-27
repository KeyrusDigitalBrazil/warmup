/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.y2ysync.task.runner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import de.hybris.y2ysync.model.Y2YStreamConfigurationContainerModel;
import de.hybris.y2ysync.model.Y2YSyncCronJobModel;
import de.hybris.y2ysync.model.Y2YSyncJobModel;
import de.hybris.y2ysync.task.dao.Y2YSyncDAO;
import de.hybris.y2ysync.task.internal.SyncTaskFactory;
import de.hybris.y2ysync.task.runner.internal.DataHubRequestCreator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableMap;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DataHubRequestTaskRunnerTest
{
	public static final String TEST_EXECUTION_ID = "testExecutionID";
	public static final String TEST_DATAHUB_URL = "http://localhost:8080/some/feed";
	public static final String DEFAULT_DATAHUB_URL = "http://localhost:5050/default/feed";


	private static final String FEED_NAME = "Y2YSYNC_FEED";
	private static final String POOL_NAME = "Y2YSYNC_POOL";
	private static final String TARGET_SYSTEM = "Y2YSYNC_POOL";


	@InjectMocks
	private final DataHubRequestTaskRunner taskRunner = new DataHubRequestTaskRunner()
	{
		@Override
		String getDataHubUrlFromProperties()
		{
			return DEFAULT_DATAHUB_URL;
		}
	};

	@InjectMocks
	private final DataHubRequestTaskRunner wronglyConfiguredRunner = new DataHubRequestTaskRunner()
	{
		@Override
		String getDataHubUrlFromProperties()
		{
			return null;
		}
	};
	@Mock
	private Y2YSyncDAO syncDAO;
	@Mock
	private DataHubRequestCreator requestCreator;
	@Mock
	private ModelService modelService;
	@Mock
	private TaskService taskService;
	@Mock
	private TaskModel task;
	@Mock
	private Y2YSyncCronJobModel cronJob;
	@Mock
	private Y2YSyncJobModel job;
	@Mock
	private Y2YStreamConfigurationContainerModel configurationContainer;
	private Y2YSyncContext ctx;

	@Before
	public void setUp() throws Exception
	{
		given(task.getContext())
				.willReturn(ImmutableMap.builder().put(SyncTaskFactory.SYNC_EXECUTION_ID_KEY, TEST_EXECUTION_ID).build());
		given(syncDAO.findSyncCronJobByCode(TEST_EXECUTION_ID)).willReturn(cronJob);
		given(cronJob.getJob()).willReturn(job);
		given(job.getStreamConfigurationContainer()).willReturn(configurationContainer);
		given(configurationContainer.getFeed()).willReturn(FEED_NAME);
		given(configurationContainer.getPool()).willReturn(POOL_NAME);
		given(configurationContainer.getTargetSystem()).willReturn(TARGET_SYSTEM);
		ctx = Y2YSyncContext.builder().withSyncExecutionId(TEST_EXECUTION_ID).withUri(TEST_DATAHUB_URL).withFeed(FEED_NAME)
				.withPool(POOL_NAME).withAutoPublishTargetSystems(TARGET_SYSTEM).build();
	}

	@Test
	public void shouldUseDataHubUrlFromY2YSyncJobIfPresent() throws Exception
	{
		// given
		given(job.getDataHubUrl()).willReturn(TEST_DATAHUB_URL);

		// when
		taskRunner.run(taskService, task);

		// then
		verify(requestCreator).sendRequest(refEq(ctx));
	}

	@Test
	public void shouldUseDataHubUrlFromPropertiesIfY2YSyncJobHasntConfiguredItDirectly() throws Exception
	{
		// given
		given(job.getDataHubUrl()).willReturn(null);

		// when
		taskRunner.run(taskService, task);

		// then
        final Y2YSyncContext ctx = Y2YSyncContext.builder().withSyncExecutionId(TEST_EXECUTION_ID).withUri(DEFAULT_DATAHUB_URL).withFeed(FEED_NAME)
                .withPool(POOL_NAME).withAutoPublishTargetSystems(TARGET_SYSTEM).build();
		verify(requestCreator).sendRequest(refEq(ctx));
	}

	@Test
	public void shouldSaveRelatedCronJobWithResultErrorWhenDataHubUrlCannotBeDetermined() throws Exception
	{
		// when
		wronglyConfiguredRunner.run(taskService, task);

		// then
		verify(cronJob).setStatus(CronJobStatus.FINISHED);
		verify(cronJob).setResult(CronJobResult.ERROR);
		verify(modelService).save(cronJob);
	}
}
