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
package com.sap.hybris.sapimpeximportadapter.tasks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.model.ImpExMediaModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.sapimpeximportadapter.services.ImpexImportService;


/**
 * JUnit test suite for {@link SapImpexImportTaskRunnerTest}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapImpexImportTaskRunnerTest
{
	@Mock
	private SessionService sessionService;
	@Mock
	private ImpexImportService impexImportService;


	@InjectMocks
	private SapImpexImportTaskRunner sapImpexImportTaskRunner;

	private TaskService taskService;
	private TaskModel taskModel;


	@Before
	public void setUp()
	{
		taskService = mock(TaskService.class);
		taskModel = new TaskModel();

	}

	@Test(expected = RuntimeException.class)
	public void ThrowExceptionOfInvaidPayload()
	{
		sapImpexImportTaskRunner.run(taskService, taskModel);
	}

	@Test(expected = RuntimeException.class)
	public void ThrowExceptionOnExecutionFailure()
	{
		final ImpExMediaModel imm = new ImpExMediaModel();
		imm.setRealFileName("dummy");
		taskModel.setContext(imm);
		when(impexImportService.importMedia(imm)).thenReturn(null);
		sapImpexImportTaskRunner.run(taskService, taskModel);
	}





}
