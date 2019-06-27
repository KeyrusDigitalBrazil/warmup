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
package com.sap.hybris.sapimpeximportadapter.facade.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.model.ImpExMediaModel;
import de.hybris.platform.servicelayer.impex.ImportService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.sapimpeximportadapter.facades.impl.DefaultSapImpexImportFacade;
import com.sap.hybris.sapimpeximportadapter.services.ImpexImportService;


/**
 * JUnit test suite for {@link DefaultSapImpexImportFacadeTest}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapImpexImportFacadeTest
{
	@Mock
	private ImportService importService;
	@Mock
	private ImpexImportService impexImportService;
	@Mock
	private ModelService modelService;
	@Mock
	private TimeService timeService;
	@Mock
	private TaskService taskService;

	private ImpExMediaModel impexMediaModel;

	private InputStream inputStream;

	private Date date;



	@InjectMocks
	private DefaultSapImpexImportFacade defaultSapImpexImportFacade;

	@Before
	public void setUp()
	{
		inputStream = new ByteArrayInputStream("dummy".getBytes());
		impexMediaModel = new ImpExMediaModel();
		when(impexImportService.createImpexMedia(any(InputStream.class))).thenReturn(impexMediaModel);
		doNothing().when(modelService).save(any(InputStream.class));
		date = new Date();
		when(timeService.getCurrentTime()).thenReturn(date);

	}

	@Test
	public void testcreateAndImportImpexMedia()
	{
		final DefaultSapImpexImportFacade defaultSapImpexImportFacadeSpy = spy(defaultSapImpexImportFacade);
		doNothing().when(defaultSapImpexImportFacadeSpy).scheduleImportTask(impexMediaModel);
		defaultSapImpexImportFacadeSpy.createAndImportImpexMedia(inputStream);
		assertTrue(impexMediaModel.isRemoveOnSuccess());
	}

	@Test
	public void testScheduleImportTask()
	{

		final TaskModel tm = new TaskModel();
		when(modelService.create(any(Class.class))).thenReturn(tm);
		doNothing().when(taskService).scheduleTask(any(TaskModel.class));

		defaultSapImpexImportFacade.scheduleImportTask(impexMediaModel);
		assertEquals(tm.getRunnerBean(), "sapImpexImportTaskRunner");
		assertEquals(tm.getContext(), impexMediaModel);
		assertEquals(tm.getExecutionTimeMillis().longValue(), date.getTime());
	}

}
