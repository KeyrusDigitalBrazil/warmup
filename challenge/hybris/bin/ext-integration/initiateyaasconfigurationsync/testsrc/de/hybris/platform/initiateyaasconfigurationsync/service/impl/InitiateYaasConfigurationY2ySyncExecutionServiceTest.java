/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package de.hybris.platform.initiateyaasconfigurationsync.service.impl;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.google.common.collect.Sets;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.deltadetection.ChangeDetectionService;
import de.hybris.y2ysync.model.Y2YStreamConfigurationContainerModel;
import de.hybris.y2ysync.model.Y2YStreamConfigurationModel;
import de.hybris.y2ysync.model.Y2YSyncCronJobModel;
import de.hybris.y2ysync.model.Y2YSyncJobModel;
import de.hybris.y2ysync.services.SyncExecutionService;


@UnitTest
public class InitiateYaasConfigurationY2ySyncExecutionServiceTest
{
	@InjectMocks
	private InitiateYaasConfigurationY2ySyncExecutionService service;

	@Mock
	private ChangeDetectionService changeDetectionService;

	@Mock
	private Y2YSyncJobModel y2YSyncJobModel;
	@Mock
	private Y2YStreamConfigurationContainerModel configurationContainerModel;
	@Mock
	private Y2YStreamConfigurationModel streamConfigurationModel;
	@Mock
	private Consumer consumer;
	@Mock
	private Function function;

	@Test
	public void shouldResetJobStreams()
	{
		service = new InitiateYaasConfigurationY2ySyncExecutionService();
		initMocks(this);

		when(y2YSyncJobModel.getStreamConfigurationContainer()).thenReturn(configurationContainerModel);
		when(configurationContainerModel.getConfigurations()).thenReturn(Sets.newHashSet(streamConfigurationModel));
		when(streamConfigurationModel.getStreamId()).thenReturn("streamId");
		when(streamConfigurationModel.getResetStream()).thenReturn(true);

		service.resetJobSteams(y2YSyncJobModel);
		verify(changeDetectionService, times(1)).resetStream("streamId");
	}

	@Test
	public void shouldStartSyncWithResetStreams()
	{
		service = new InitiateYaasConfigurationY2ySyncExecutionService()
		{

			@Override
			protected void resetJobSteams(final Y2YSyncJobModel job)
			{
				consumer.accept(job);
			}

			@Override
			protected Y2YSyncCronJobModel startSyncInternal(final Y2YSyncJobModel job, final SyncExecutionService.ExecutionMode executionMode)
			{
				return (Y2YSyncCronJobModel) function.apply(job);
			}
		};
		initMocks(this);

		when(streamConfigurationModel.getResetStream()).thenReturn(true);

		service.startSync(y2YSyncJobModel, SyncExecutionService.ExecutionMode.ASYNC);

		verify(consumer, times(1)).accept(y2YSyncJobModel);
		verify(function, times(1)).apply(y2YSyncJobModel);
	}

	@Test
	public void shouldStartSyncWithNoResetStreams()
	{
		service = new InitiateYaasConfigurationY2ySyncExecutionService()
		{

			@Override
			protected Y2YSyncCronJobModel startSyncInternal(final Y2YSyncJobModel job, final SyncExecutionService.ExecutionMode executionMode)
			{
				return (Y2YSyncCronJobModel) function.apply(job);
			}
		};
		initMocks(this);

		when(streamConfigurationModel.getResetStream()).thenReturn(false);

		service.startSync(y2YSyncJobModel, SyncExecutionService.ExecutionMode.ASYNC);

		verify(changeDetectionService, never()).resetStream("streamId");
		verify(function, times(1)).apply(y2YSyncJobModel);
	}
}
