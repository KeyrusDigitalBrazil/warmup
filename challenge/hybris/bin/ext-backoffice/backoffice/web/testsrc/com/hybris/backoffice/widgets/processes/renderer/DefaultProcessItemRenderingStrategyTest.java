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
package com.hybris.backoffice.widgets.processes.renderer;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobHistoryModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.security.permissions.PermissionCheckResult;
import de.hybris.platform.servicelayer.security.permissions.PermissionCheckingService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultProcessItemRenderingStrategyTest
{
	@InjectMocks
	@Spy
	private DefaultProcessItemRenderingStrategy processItemRenderingStrategy;

	@Mock
	private PermissionCheckingService permissionCheckingService;

	@Mock
	private CronJobHistoryModel cronJobHistoryModel;

	@Test
	public void shouldNotProcessFail()
	{
		//given
		when(cronJobHistoryModel.getStatus()).thenReturn(CronJobStatus.FINISHED);
		when(cronJobHistoryModel.getResult()).thenReturn(CronJobResult.SUCCESS);

		//when
		final boolean result = processItemRenderingStrategy.isFailed(cronJobHistoryModel);

		//then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldProcessFailIfNotFinished()
	{
		//given
		when(cronJobHistoryModel.getStatus()).thenReturn(CronJobStatus.ABORTED);
		when(cronJobHistoryModel.getResult()).thenReturn(CronJobResult.SUCCESS);

		//when
		final boolean result = processItemRenderingStrategy.isFailed(cronJobHistoryModel);

		//then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldProcessFailIfErrorResult()
	{
		//given
		when(cronJobHistoryModel.getStatus()).thenReturn(CronJobStatus.FINISHED);
		when(cronJobHistoryModel.getResult()).thenReturn(CronJobResult.ERROR);

		//when
		final boolean result = processItemRenderingStrategy.isFailed(cronJobHistoryModel);

		//then
		assertThat(result).isTrue();
	}

	@Test
	public void shouldProcessFailIfFailureResult()
	{
		//given
		when(cronJobHistoryModel.getStatus()).thenReturn(CronJobStatus.FINISHED);
		when(cronJobHistoryModel.getResult()).thenReturn(CronJobResult.FAILURE);

		//when
		final boolean result = processItemRenderingStrategy.isFailed(cronJobHistoryModel);

		//then
		assertThat(result).isTrue();
	}

	@Test
	public void shouldReRunIfCronJobFailed()
	{
		//given
		doReturn(Boolean.TRUE).when(processItemRenderingStrategy).isFailed(any(CronJobHistoryModel.class));
		when(cronJobHistoryModel.getStatus()).thenReturn(CronJobStatus.UNKNOWN);
		final PermissionCheckResult permissionCheckResult = mock(PermissionCheckResult.class);
		when(permissionCheckResult.isGranted()).thenReturn(Boolean.TRUE);
		when(permissionCheckingService.checkItemPermission(any(CronJobModel.class), any())).thenReturn(permissionCheckResult);

		//when
		final boolean result = processItemRenderingStrategy.isRerunApplicable(cronJobHistoryModel);

		//then
		assertThat(result).isTrue();
	}


	@Test
	public void shouldReRunIfCronJobStatusIsAborted()
	{
		//given
		doReturn(Boolean.FALSE).when(processItemRenderingStrategy).isFailed(any(CronJobHistoryModel.class));
		when(cronJobHistoryModel.getStatus()).thenReturn(CronJobStatus.ABORTED);
		final PermissionCheckResult permissionCheckResult = mock(PermissionCheckResult.class);
		when(permissionCheckResult.isGranted()).thenReturn(Boolean.TRUE);
		when(permissionCheckingService.checkItemPermission(any(CronJobModel.class), any())).thenReturn(permissionCheckResult);

		//when
		final boolean result = processItemRenderingStrategy.isRerunApplicable(cronJobHistoryModel);

		//then
		assertThat(result).isTrue();
	}

	@Test
	public void shouldNotReRunIfCronJobIsNotAbortedAndPassed()
	{
		//given
		doReturn(Boolean.FALSE).when(processItemRenderingStrategy).isFailed(any(CronJobHistoryModel.class));
		when(cronJobHistoryModel.getStatus()).thenReturn(CronJobStatus.UNKNOWN);
		final PermissionCheckResult permissionCheckResult = mock(PermissionCheckResult.class);
		when(permissionCheckResult.isGranted()).thenReturn(Boolean.TRUE);
		when(permissionCheckingService.checkItemPermission(any(CronJobModel.class), any())).thenReturn(permissionCheckResult);

		//when
		final boolean result = processItemRenderingStrategy.isRerunApplicable(cronJobHistoryModel);

		//then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldNotReRunIfPermissionNotGranted()
	{
		//given
		doReturn(Boolean.TRUE).when(processItemRenderingStrategy).isFailed(any(CronJobHistoryModel.class));
		when(cronJobHistoryModel.getStatus()).thenReturn(CronJobStatus.ABORTED);
		final PermissionCheckResult permissionCheckResult = mock(PermissionCheckResult.class);
		when(permissionCheckResult.isGranted()).thenReturn(Boolean.FALSE);
		when(permissionCheckingService.checkItemPermission(any(CronJobModel.class), any())).thenReturn(permissionCheckResult);

		//when
		final boolean result = processItemRenderingStrategy.isRerunApplicable(cronJobHistoryModel);

		//then
		assertThat(result).isFalse();
	}
}
