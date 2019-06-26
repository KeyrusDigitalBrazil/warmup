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
package com.hybris.backoffice.cronjob.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.catalog.model.synchronization.CatalogVersionSyncCronJobModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobHistoryModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.JobLogModel;
import de.hybris.platform.cronjob.model.JobModel;
import de.hybris.platform.cronjob.model.LogFileModel;
import de.hybris.platform.servicelayer.cronjob.CronJobHistoryInclude;
import de.hybris.platform.servicelayer.cronjob.CronJobHistoryService;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hybris.backoffice.cronjob.CronJobHistoryDataQuery;
import com.hybris.backoffice.cronjob.DefaultCronJobHistoryFacade;
import com.hybris.backoffice.sync.facades.SynchronizationFacade;
import com.hybris.cockpitng.core.user.CockpitUserService;
import com.hybris.cockpitng.labels.LabelService;



@RunWith(MockitoJUnitRunner.class)
public class DefaultCronJobHistoryFacadeTest
{
	@Mock
	private CronJobHistoryService cronJobHistoryService;
	@Mock
	private CockpitUserService cockpitUserService;
	@Mock
	private TimeService timeService;
	@Mock
	private LabelService labelService;
	@Mock
	private CronJobService cronJobService;
	@Mock
	private SynchronizationFacade synchronizationFacade;
	@Mock
	private CronJobHistoryInclude cronJobHistoryInclude;
	@Mock
	private UserService userService;

	@InjectMocks
	private DefaultCronJobHistoryFacade facade;

	private static final Duration TIME_RANGE = Duration.ofMinutes(30);

	private static final Date CURRENT_TIME = new Date();
	private static final Date START_DATE = Date.from(CURRENT_TIME.toInstant().minus(TIME_RANGE));
	private static final String CRON_JOB_CODE = "myCronJob";
	private static final Date EXPECTED_START_DATE = START_DATE;
	private static final Date EXPECTED_END_DATE = null;
	private final String expectedUser = "john.kowalski";
	private CronJobStatus expectedCronJobStatus = null;

	private final List<CronJobHistoryModel> jobs = new ArrayList<>();

	@Before
	public void setUp()
	{
		when(timeService.getCurrentTime()).thenReturn(CURRENT_TIME);
		when(cockpitUserService.getCurrentUser()).thenReturn(expectedUser);
		when(cronJobHistoryService.getCronJobHistoryBy(anySet(),any(UserModel.class), any(),any(), any(CronJobStatus.class)))
				.thenReturn(jobs);
		when(cronJobHistoryService.getCronJobHistoryBy(anyString())).thenReturn(jobs);
		when(cronJobService.getJob(anyString())).thenAnswer(invocationOnMock -> {

			final String code = (String) invocationOnMock.getArguments()[0];
			final JobModel jobModel = mock(JobModel.class);
			when(jobModel.getCode()).thenReturn(code);
			return jobModel;
		});
		when(labelService.getObjectLabel(any(JobModel.class))).thenAnswer(
				invocationOnMock -> getMockedLabelServiceValue(((JobModel) invocationOnMock.getArguments()[0]).getCode()));
		when(cronJobHistoryInclude.getJobCodes()).thenReturn(Sets.newHashSet("solrIndexerJob"));
		final Map<String, CronJobHistoryInclude> cronJobHistoryIncludeMap = ImmutableMap.of("solrIndexerProcesses",
				cronJobHistoryInclude);
		facade.setCronJobHistoryIncludes(cronJobHistoryIncludeMap);
		final UserModel currentUser = mock(UserModel.class);
		when(currentUser.getUid()).thenReturn(expectedUser);
		when(userService.getUserForUID(expectedUser)).thenReturn(currentUser);
	}

	@Test
	public void shouldQueryForRequestedTimeRange()
	{
		// given
		final boolean showExecutedByOtherUsers = true;
		final boolean showFinishedJobs = true;
		final CronJobHistoryDataQuery query = new CronJobHistoryDataQuery(TIME_RANGE, showExecutedByOtherUsers, showFinishedJobs);

		// when
		final List<CronJobHistoryModel> result = facade.getCronJobHistory(query);

		// then
		verify(cronJobHistoryService).getCronJobHistoryBy(anySet(), eq(null), eq(EXPECTED_START_DATE), eq(EXPECTED_END_DATE),
				eq(expectedCronJobStatus));
		assertThat(result).isSameAs(jobs);
	}

	@Test
	public void shouldQueryForCurrentUserJobsOnly()
	{
		// given
		final boolean showFinishedJobs = true;
		final boolean showExecutedByOtherUsers = false;
		final CronJobHistoryDataQuery query = new CronJobHistoryDataQuery(TIME_RANGE, showExecutedByOtherUsers, showFinishedJobs,
				Sets.newHashSet(SyncItemJobModel._TYPECODE));

		// when
		facade.getCronJobHistory(query);

		// then
		verify(cronJobHistoryService).getCronJobHistoryBy(anySet(), argThat(getUsernameMatcher(expectedUser)),
				eq(EXPECTED_START_DATE), eq(EXPECTED_END_DATE), eq(expectedCronJobStatus));
	}

	@Test
	public void shouldQueryForRunningJobsOnly()
	{
		// given
		final boolean showExecutedByOtherUsers = true;
		final boolean showFinishedJobs = false;
		final CronJobHistoryDataQuery query = new CronJobHistoryDataQuery(TIME_RANGE, showExecutedByOtherUsers, showFinishedJobs);

		// when
		final List<CronJobHistoryModel> result = facade.getCronJobHistory(query);

		// then
		expectedCronJobStatus = CronJobStatus.RUNNING;
		verify(cronJobHistoryService).getCronJobHistoryBy(anySet(), eq(null), eq(EXPECTED_START_DATE), eq(EXPECTED_END_DATE),
				eq(expectedCronJobStatus));
		assertThat(result).isSameAs(jobs);
	}

	@Test
	public void shouldQueryForSingleCronJob()
	{
		// when
		final List<CronJobHistoryModel> result = facade.getCronJobHistory(CRON_JOB_CODE);

		// then
		verify(cronJobHistoryService).getCronJobHistoryBy(CRON_JOB_CODE);
		assertThat(result).isSameAs(jobs);
	}

	@Test
	public void shouldReturnJobName()
	{
		// given
		final String jobCode = "jobCode";
		final CronJobHistoryModel cronJobHistoryModel = new CronJobHistoryModel();
		cronJobHistoryModel.setJobCode(jobCode);

		// when
		final String result = facade.getJobName(cronJobHistoryModel);

		// then
		verify(cronJobService).getJob(jobCode);
		assertThat(result).isEqualTo(getMockedLabelServiceValue(cronJobHistoryModel.getJobCode()));
	}

	protected String getMockedLabelServiceValue(final String code)
	{
		return "LABEL_SERVICE_VALUE:".concat(code);
	}

	@Test
	public void testFindLogFilesLoggingEnabled()
	{
		//given
		final CronJobHistoryModel cjh = createCronJobHistory(150, 100, 200, 300);
		when(cjh.getCronJob().getLogToFile()).thenReturn((Boolean.TRUE));

		//when
		final Optional<? extends ItemModel> log = facade.findLog(cjh);

		//then
		assertThat(log.isPresent()).isTrue();
		assertThat(log.get().getCreationtime()).isEqualTo(new Date(200));
	}

	@Test
	public void testFindLogFilesLoggingDisabled()
	{
		//given
		final CronJobHistoryModel cjh = createCronJobHistory(150, 100, 200, 300);
		when(cjh.getCronJob().getLogToFile()).thenReturn((Boolean.FALSE));

		//when
		final Optional<? extends ItemModel> log = facade.findLog(cjh);

		//then
		assertThat(log.isPresent()).isFalse();
	}

	@Test
	public void testFindDbLogLoggingEnabled()
	{
		//given
		final CronJobHistoryModel cjh = createCronJobHistory(150, 100, 200, 300);
		when(cjh.getCronJob().getLogToDatabase()).thenReturn(Boolean.TRUE);

		//when
		final Optional<? extends ItemModel> log = facade.findLog(cjh);

		//then
		assertThat(log.isPresent()).isTrue();
		assertThat(log.get().getCreationtime()).isEqualTo(new Date(200));
	}

	@Test
	public void testFindDBLogLoggingDisabled()
	{
		//given
		final CronJobHistoryModel cjh = createCronJobHistory(150, 100, 200, 300);
		when(cjh.getCronJob().getLogToDatabase()).thenReturn(Boolean.FALSE);

		//when
		final Optional<? extends ItemModel> log = facade.findLog(cjh);

		//then
		assertThat(log.isPresent()).isFalse();
	}

	@Test
	public void testDbLogIsMoreImportantThanFileLog()
	{
		//given
		final CronJobHistoryModel cjh = createCronJobHistory(150, 100, 200, 300);
		when(cjh.getCronJob().getLogToDatabase()).thenReturn(Boolean.TRUE);
		when(cjh.getCronJob().getLogToDatabase()).thenReturn(Boolean.TRUE);

		//when
		final Optional<? extends ItemModel> log = facade.findLog(cjh);

		//then
		assertThat(log.isPresent()).isTrue();
		assertThat(log.get()).isInstanceOf(JobLogModel.class);
	}

	@Test
	public void testGetCronJobHistoryForCollection()
	{
		//given
		final List<String> cronJobCodes = Lists.newArrayList("A", "B");
		when(cronJobHistoryService.getCronJobHistoryBy(cronJobCodes)).thenReturn(jobs);
		//when
		final List<CronJobHistoryModel> result = facade.getCronJobHistory(cronJobCodes);

		//then
		assertThat(result).isEqualTo(jobs);

	}

	protected CronJobHistoryModel createCronJobHistory(final long historyCreationTime, final long... logsCreationTimes)
	{
		final List<LogFileModel> logs = new ArrayList<>();
		for (final long logsCreationTime : logsCreationTimes)
		{
			final LogFileModel log = new LogFileModel();
			log.setCreationtime(new Date(logsCreationTime));
			logs.add(log);
		}


		final List<JobLogModel> dbLogs = new ArrayList<>();
		for (final long logsCreationTime : logsCreationTimes)
		{
			final JobLogModel log = new JobLogModel();
			log.setCreationtime(new Date(logsCreationTime));
			dbLogs.add(log);
		}

		final CronJobModel cj = mock(CronJobModel.class);
		when(cj.getLogFiles()).thenReturn(logs);
		when(cj.getLogs()).thenReturn(dbLogs);


		final CronJobHistoryModel cjh = mock(CronJobHistoryModel.class);
		when(cjh.getCronJob()).thenReturn(cj);
		when(cjh.getStartTime()).thenReturn(new Date(historyCreationTime));
		return cjh;
	}

	@Test
	public void testRerunSyncCronJob()
	{
		//given
		final CronJobHistoryModel cronJobHistory = new CronJobHistoryModel();
		final CatalogVersionSyncCronJobModel syncCronJob = new CatalogVersionSyncCronJobModel();
		cronJobHistory.setCronJob(syncCronJob);
		//when
		facade.reRunCronJob(cronJobHistory);
		//then
		verify(synchronizationFacade).reRunCronJob(syncCronJob);
	}

	@Test
	public void testRerunRegularCronJob()
	{
		//given
		final CronJobHistoryModel cronJobHistory = new CronJobHistoryModel();
		final CronJobModel cronJob = new CronJobModel();
		cronJobHistory.setCronJob(cronJob);
		//when
		facade.reRunCronJob(cronJobHistory);
		//then
		verify(cronJobService).performCronJob(cronJob);
		verify(synchronizationFacade, never()).reRunCronJob(any());
	}

	private ArgumentMatcher<UserModel> getUsernameMatcher(final String expectedUser)
	{
		return new ArgumentMatcher<UserModel>()
		{
			@Override
			public boolean matches(final Object o)
			{
				return StringUtils.equals(((UserModel) o).getUid(),expectedUser);
			}
		};
	}

}
