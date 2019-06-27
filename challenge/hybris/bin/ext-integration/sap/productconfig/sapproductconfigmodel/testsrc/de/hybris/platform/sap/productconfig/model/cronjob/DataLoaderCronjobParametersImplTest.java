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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.JobModel;
import de.hybris.platform.sap.productconfig.model.impl.DataLoaderCronjobParametersImpl;
import de.hybris.platform.sap.productconfig.model.model.DataLoaderCronJobModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DataLoaderCronjobParametersImplTest
{

	private static final Integer ANOTHER_START_NODE = Integer.valueOf("789");
	private static final Integer START_NODE_ID = Integer.valueOf(123);
	private static final Integer STOP_NODE_ID = Integer.valueOf(456);

	private DataLoaderCronjobParametersImpl classUnderTest;

	@Mock
	private CronJobService mockedCronJobService;

	private CronJobModel startCronJobModel;
	private DataLoaderCronJobModel stopCronJobModel;
	@Mock
	private JobModel mockedStartJobModel;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new DataLoaderCronjobParametersImpl();
		classUnderTest.setCronJobService(mockedCronJobService);
		classUnderTest.setDataloadStartJobBeanId("startBean");
		classUnderTest.setDataloadStopJobBeanId("stopBean");

		startCronJobModel = new CronJobModel();
		given(mockedStartJobModel.getCronJobs()).willReturn(Collections.singletonList(startCronJobModel));
		given(mockedCronJobService.getJob("startBean")).willReturn(mockedStartJobModel);
		startCronJobModel.setNodeID(START_NODE_ID);

		stopCronJobModel = new DataLoaderCronJobModel();
		stopCronJobModel.setRunningOnClusterNode(STOP_NODE_ID);
		given(mockedCronJobService.getRunningOrRestartedCronJobs()).willReturn(Collections.singletonList(stopCronJobModel));
	}

	@Test
	public void testRetrieveNodeIdForStartJobNoJob()
	{
		given(mockedStartJobModel.getCronJobs()).willReturn(Collections.emptyList());
		final Integer id = classUnderTest.retrieveNodeIdForStartJob();
		assertNull(id);
	}

	@Test
	public void testRetrieveNodeIdForStartMulti()
	{
		final List<CronJobModel> list = new ArrayList<>();
		startCronJobModel.setCreationtime(new Date(100l));
		startCronJobModel.setNodeID(ANOTHER_START_NODE);
		list.add(startCronJobModel);
		final CronJobModel anotherStartCronJob = new CronJobModel();
		anotherStartCronJob.setCreationtime(new Date(200l));
		list.add(anotherStartCronJob);
		given(mockedStartJobModel.getCronJobs()).willReturn(list);
		final Integer id = classUnderTest.retrieveNodeIdForStartJob();
		assertEquals(ANOTHER_START_NODE, id);
	}

	@Test
	public void testRetrieveNodeIdForStartMulti2()
	{
		final List<CronJobModel> list = new ArrayList<>();
		final CronJobModel anotherStartCronJob = new CronJobModel();
		anotherStartCronJob.setCreationtime(new Date(200l));
		list.add(anotherStartCronJob);
		startCronJobModel.setCreationtime(new Date(100l));
		startCronJobModel.setNodeID(ANOTHER_START_NODE);
		list.add(startCronJobModel);
		given(mockedStartJobModel.getCronJobs()).willReturn(list);
		final Integer id = classUnderTest.retrieveNodeIdForStartJob();
		assertEquals(ANOTHER_START_NODE, id);
	}

	@Test
	public void testRetrieveNodeIdForStartJob()
	{
		final Integer id = classUnderTest.retrieveNodeIdForStartJob();
		assertEquals(START_NODE_ID, id);
	}

	@Test
	public void testRetrieveNodeIdForStopJob()
	{
		final Integer id = classUnderTest.retrieveNodeIdForStopJob();
		assertEquals(STOP_NODE_ID, id);
	}

	@Test
	public void testRetrieveNodeIdForStopJobMulti()
	{

		final List<CronJobModel> list = new ArrayList<>();
		list.add(startCronJobModel);
		list.add(stopCronJobModel);
		given(mockedCronJobService.getRunningOrRestartedCronJobs()).willReturn(list);
		final Integer id = classUnderTest.retrieveNodeIdForStopJob();
		assertEquals(STOP_NODE_ID, id);
	}

	@Test
	public void testRetrieveNodeIdForStopJobNoJob()
	{
		given(mockedCronJobService.getRunningOrRestartedCronJobs()).willReturn(Collections.emptyList());
		final Integer id = classUnderTest.retrieveNodeIdForStopJob();
		assertNull(id);
	}
}
