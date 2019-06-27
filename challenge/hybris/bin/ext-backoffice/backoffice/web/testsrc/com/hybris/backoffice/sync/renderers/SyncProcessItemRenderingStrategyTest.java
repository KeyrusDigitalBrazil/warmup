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
package com.hybris.backoffice.sync.renderers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobHistoryModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.sync.facades.SynchronizationFacade;
import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.cockpitng.core.user.CockpitUserService;


@RunWith(MockitoJUnitRunner.class)
public class SyncProcessItemRenderingStrategyTest
{
	private static final String ADMIN = "admin";
	private static final String CURRENT_USER = "john.kowalski";

	@InjectMocks
	private SyncProcessItemRenderingStrategy syncProcessItemRenderingStrategy;

	@Mock
	private CockpitUserService cockpitUserService;
	@Mock
	private SynchronizationFacade synchronizationFacade;
	@Mock
	private NotificationService notificationService;

	@Before
	public void setUp()
	{
		when(cockpitUserService.getCurrentUser()).thenReturn(CURRENT_USER);
		when(cockpitUserService.isAdmin(ADMIN)).thenReturn(Boolean.TRUE);
	}

	@Test
	public void isRerunApplicableFinishedTest()
	{
		final CronJobHistoryModel cjh = mock(CronJobHistoryModel.class);
		when(cjh.getUserUid()).thenReturn(ADMIN);
		when(cjh.getStatus()).thenReturn(CronJobStatus.FINISHED);
		when(cjh.getResult()).thenReturn(CronJobResult.ERROR);

		assertThat(syncProcessItemRenderingStrategy.isRerunApplicable(cjh)).isTrue();
	}

	@Test
	public void isRerunApplicableAbortedTest()
	{
		final CronJobHistoryModel cjh = mock(CronJobHistoryModel.class);
		when(cjh.getUserUid()).thenReturn(ADMIN);
		when(cjh.getStatus()).thenReturn(CronJobStatus.ABORTED);
		when(cjh.getResult()).thenReturn(CronJobResult.ERROR);

		assertThat(syncProcessItemRenderingStrategy.isRerunApplicable(cjh)).isFalse();
	}

	@Test
	public void isRerunApplicableUnknownTest()
	{
		final CronJobHistoryModel cjh = mock(CronJobHistoryModel.class);
		when(cjh.getUserUid()).thenReturn(ADMIN);
		when(cjh.getStatus()).thenReturn(CronJobStatus.UNKNOWN);
		when(cjh.getResult()).thenReturn(CronJobResult.ERROR);

		assertThat(syncProcessItemRenderingStrategy.isRerunApplicable(cjh)).isFalse();
	}

	@Test
	public void isRerunApplicablePausedTest()
	{
		final CronJobHistoryModel cjh = mock(CronJobHistoryModel.class);
		when(cjh.getUserUid()).thenReturn(ADMIN);
		when(cjh.getStatus()).thenReturn(CronJobStatus.PAUSED);
		when(cjh.getResult()).thenReturn(CronJobResult.ERROR);

		assertThat(syncProcessItemRenderingStrategy.isRerunApplicable(cjh)).isFalse();
	}


	@Test
	public void isRerunApplicableRunningTest()
	{
		final CronJobHistoryModel cjh = mock(CronJobHistoryModel.class);
		when(cjh.getUserUid()).thenReturn(ADMIN);
		when(cjh.getStatus()).thenReturn(CronJobStatus.RUNNING);
		when(cjh.getResult()).thenReturn(CronJobResult.ERROR);

		assertThat(syncProcessItemRenderingStrategy.isRerunApplicable(cjh)).isFalse();
	}

	@Test
	public void isRerunApplicableRerunningTest()
	{
		final CronJobHistoryModel cjh = mock(CronJobHistoryModel.class);
		when(cjh.getUserUid()).thenReturn(ADMIN);
		when(cjh.getStatus()).thenReturn(CronJobStatus.RUNNINGRESTART);
		when(cjh.getResult()).thenReturn(CronJobResult.ERROR);

		assertThat(syncProcessItemRenderingStrategy.isRerunApplicable(cjh)).isFalse();
	}

	@Test
	public void isRerunApplicableFinishedAnotherUser()
	{
		final CronJobHistoryModel cjh = mock(CronJobHistoryModel.class);
		when(cjh.getUserUid()).thenReturn("testUser");
		when(cjh.getStatus()).thenReturn(CronJobStatus.FINISHED);
		when(cjh.getResult()).thenReturn(CronJobResult.ERROR);

		assertThat(syncProcessItemRenderingStrategy.isRerunApplicable(cjh)).isFalse();
	}

	@Test
	public void isRerunApplicableFinishedTheSameNonAdminUser()
	{
		final CronJobHistoryModel cjh = mock(CronJobHistoryModel.class);
		when(cjh.getUserUid()).thenReturn(CURRENT_USER);
		when(cjh.getStatus()).thenReturn(CronJobStatus.FINISHED);
		when(cjh.getResult()).thenReturn(CronJobResult.FAILURE);

		assertThat(syncProcessItemRenderingStrategy.isRerunApplicable(cjh)).isTrue();
	}


	@Test
	public void isRerunApplicableFinishedSuccess()
	{
		final CronJobHistoryModel cjh = mock(CronJobHistoryModel.class);
		when(cjh.getUserUid()).thenReturn(CURRENT_USER);
		when(cjh.getStatus()).thenReturn(CronJobStatus.FINISHED);
		when(cjh.getResult()).thenReturn(CronJobResult.SUCCESS);

		assertThat(syncProcessItemRenderingStrategy.isRerunApplicable(cjh)).isFalse();
	}

	@Test
	public void isRerunApplicableFinishedUnknown()
	{
		final CronJobHistoryModel cjh = mock(CronJobHistoryModel.class);
		when(cjh.getUserUid()).thenReturn(CURRENT_USER);
		when(cjh.getStatus()).thenReturn(CronJobStatus.FINISHED);
		when(cjh.getResult()).thenReturn(CronJobResult.UNKNOWN);

		assertThat(syncProcessItemRenderingStrategy.isRerunApplicable(cjh)).isFalse();
	}
}
