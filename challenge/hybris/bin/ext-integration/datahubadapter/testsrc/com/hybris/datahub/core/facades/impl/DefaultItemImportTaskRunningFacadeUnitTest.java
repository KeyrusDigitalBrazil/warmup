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

package com.hybris.datahub.core.facades.impl;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import com.hybris.datahub.core.dto.ItemImportTaskData;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultItemImportTaskRunningFacadeUnitTest
{
	private final DefaultItemImportTaskRunningFacade facade = new DefaultItemImportTaskRunningFacade();
	@Mock
	private ModelService modelService;
	@Mock
	private TaskService taskService;
	@Mock
	private TimeService timeService;

	@Before
	public void setUp()
	{
		facade.setModelService(modelService);
		facade.setTaskService(taskService);
		facade.setTimeService(timeService);
	}

	@Test
	public void testScheduleImportTask()
	{
		final ItemImportTaskData data = Mockito.mock(ItemImportTaskData.class);

		final TaskModel task = Mockito.mock(TaskModel.class);
		doReturn(task).when(modelService).create(TaskModel.class);
		doReturn(new Date()).when(timeService).getCurrentTime();

		facade.scheduleImportTask(data);

		verify(modelService).create(TaskModel.class);
		verify(taskService).scheduleTask(task);
		verify(timeService).getCurrentTime();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testScheduleImportTaskWithNullItemImportTaskData()
	{
		facade.scheduleImportTask(null);
	}
}
