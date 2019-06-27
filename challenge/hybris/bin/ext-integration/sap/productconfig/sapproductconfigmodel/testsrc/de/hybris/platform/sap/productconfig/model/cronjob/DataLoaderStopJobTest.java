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
package de.hybris.platform.sap.productconfig.model.cronjob;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.sap.productconfig.model.impl.DataLoaderManagerContainerImpl;
import de.hybris.platform.sap.productconfig.model.intf.DataLoaderManagerContainer;
import de.hybris.platform.sap.productconfig.model.model.DataLoaderCronJobModel;
import de.hybris.platform.sap.productconfig.model.model.DataLoaderStopCronJobModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderFailureException;
import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderManager;


@SuppressWarnings("javadoc")
@UnitTest
public class DataLoaderStopJobTest
{
	private static final Integer NODE_ID = Integer.valueOf(123);

	private DataLoaderStopJob classUnderTest;
	private DataLoaderCronJobModel dataLoaderCronJob;
	private DataLoaderStopCronJobModel dataLoaderStopCronJob;

	@Mock
	private CronJobService mokedCronJobService;
	@Mock
	private DataloaderManager mockedDataloaderManager;

	@Rule
	public ExpectedException thrown = ExpectedException.none();


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new DataLoaderStopJob();
		classUnderTest.setCronJobService(mokedCronJobService);

		dataLoaderCronJob = new DataLoaderCronJobModel();
		dataLoaderCronJob.setRunningOnClusterNode(NODE_ID);

		dataLoaderStopCronJob = new DataLoaderStopCronJobModel();
		dataLoaderStopCronJob.setRunningOnClusterNode(NODE_ID);
		given(mokedCronJobService.getRunningOrRestartedCronJobs()).willReturn(Collections.singletonList(dataLoaderCronJob));

		final DataLoaderManagerContainer container = new DataLoaderManagerContainerImpl();
		classUnderTest.setDataLoaderManagerContainer(container);
		container.setDataLoaderManager(mockedDataloaderManager);
	}

	@Test
	public void testRetrieveDataloadStartJobNodeId_NoRunningJob()
	{
		given(mokedCronJobService.getRunningOrRestartedCronJobs()).willReturn(Collections.emptyList());
		assertNull(classUnderTest.retrieveDataloadStartJobNodeId());
	}

	@Test
	public void testRetrieveDataloadStartJobNodeId_DLJob()
	{
		assertEquals(NODE_ID, classUnderTest.retrieveDataloadStartJobNodeId());
	}

	@Test
	public void testRetrieveDataloadStartJobNodeId_SeveralJobs()
	{

		final List<CronJobModel> jobList = new ArrayList<>();
		jobList.add(new CronJobModel());
		jobList.add(dataLoaderCronJob);
		jobList.add(new CronJobModel());
		given(mokedCronJobService.getRunningOrRestartedCronJobs()).willReturn(jobList);
		assertEquals(NODE_ID, classUnderTest.retrieveDataloadStartJobNodeId());
	}

	@Test
	public void testPerformNoRunningJob()
	{
		given(mokedCronJobService.getRunningOrRestartedCronJobs()).willReturn(Collections.emptyList());
		final PerformResult result = classUnderTest.perform(dataLoaderStopCronJob);
		assertEquals(CronJobResult.SUCCESS, result.getResult());
		assertEquals(CronJobStatus.FINISHED, result.getStatus());
	}

	@Test
	public void testPerformNotOnSameClusterNode()
	{
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage(NODE_ID.toString());
		thrown.expectMessage("567");

		dataLoaderStopCronJob.setRunningOnClusterNode(Integer.valueOf(567));
		classUnderTest.perform(dataLoaderStopCronJob);
	}

	@Test
	public void testPerformDaloadManagerNull()
	{
		classUnderTest.getDataLoaderManagerContainer().setDataLoaderManager(null);
		final PerformResult result = classUnderTest.perform(dataLoaderStopCronJob);
		assertEquals(CronJobResult.SUCCESS, result.getResult());
		assertEquals(CronJobStatus.FINISHED, result.getStatus());
	}

	@Test
	public void testPerformDalaodManagerSaysNotRunning() throws DataloaderFailureException
	{

		given(mockedDataloaderManager.isDownloadRunning()).willReturn(false);

		final PerformResult result = classUnderTest.perform(dataLoaderStopCronJob);
		assertEquals(CronJobResult.SUCCESS, result.getResult());
		assertEquals(CronJobStatus.FINISHED, result.getStatus());
		verify(mockedDataloaderManager, never()).stopDownload();

	}

	@Test
	public void testPerformStopDataload() throws DataloaderFailureException
	{

		given(mockedDataloaderManager.isDownloadRunning()).willReturn(true);

		final PerformResult result = classUnderTest.perform(dataLoaderStopCronJob);
		assertEquals(CronJobResult.SUCCESS, result.getResult());
		assertEquals(CronJobStatus.FINISHED, result.getStatus());
		verify(mockedDataloaderManager).stopDownload();

	}

	@Test
	public void testPerformStopDataloadException() throws DataloaderFailureException
	{
		thrown.expect(IllegalStateException.class);
		thrown.expectCause(Matchers.isA(DataloaderFailureException.class));
		given(mockedDataloaderManager.isDownloadRunning()).willReturn(true);
		willThrow(DataloaderFailureException.class).given(mockedDataloaderManager).stopDownload();

		classUnderTest.perform(dataLoaderStopCronJob);

	}
}
