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

package com.hybris.datahub.core.tasks;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import com.hybris.datahub.core.dto.ItemImportTaskData;
import com.hybris.datahub.core.facades.ItemImportFacade;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ItemImportTaskRunnerUnitTest
{
	private static final String POOL_NAME = "testpool";
	private static final Long PUBLICATION_ID = 1L;
	private static final String CALLBACK_URL = "http://localhost/callback";
	private static final byte[] IMPEX_CONTENT = "INSERT_UPDATE value, value, value".getBytes();
	private static final String USER = "user name";
	private static final String LANGUAGE = "ja";

	private final ItemImportTaskRunner taskRunner = new ItemImportTaskRunner();
	@Mock
	private ItemImportFacade importFacade;
	@Mock
	private SessionService sessionService;
	@Mock
	private TaskModel taskModel;
	@Mock
	private TaskService taskService;

	@Before
	public void setup()
	{
		final Map<String, Serializable> sessionAttrs = new HashMap<>();
		sessionAttrs.put("user", USER);
		sessionAttrs.put("language", LANGUAGE);

		final ItemImportTaskData taskData =
				new ItemImportTaskData(POOL_NAME, PUBLICATION_ID, CALLBACK_URL, IMPEX_CONTENT, sessionAttrs);
		doReturn(taskData).when(taskModel).getContext();

		taskRunner.setImportFacade(importFacade);
		taskRunner.setSessionService(sessionService);
	}

	@Test
	public void testRunInitializesSessionUserBeforeTheItemImport() throws Exception
	{
		final InOrder callSeq = Mockito.inOrder(sessionService, importFacade);

		taskRunner.run(taskService, taskModel);

		callSeq.verify(sessionService).setAttribute("user", USER);
		callSeq.verify(importFacade).importItems((ItemImportTaskData) taskModel.getContext());
	}

	@Test
	public void testRunInitializesSessionLanguageBeforeTheItemImport() throws Exception
	{
		final InOrder callSeq = Mockito.inOrder(sessionService, importFacade);

		taskRunner.run(taskService, taskModel);

		callSeq.verify(sessionService).setAttribute("language", LANGUAGE);
		callSeq.verify(importFacade).importItems((ItemImportTaskData) taskModel.getContext());
	}

	@Test
	public void testRunClosesSessionAfterTheItemImport() throws Exception
	{
		final InOrder callSeq = Mockito.inOrder(sessionService, importFacade);

		taskRunner.run(taskService, taskModel);

		callSeq.verify(importFacade).importItems((ItemImportTaskData) taskModel.getContext());
		callSeq.verify(sessionService).closeCurrentSession();
	}

	@Test
	public void testRunHandlesImportItemsException() throws Exception
	{
		final IOException ioEx = new IOException();
		doThrow(ioEx).when(importFacade).importItems(any(ItemImportTaskData.class));

		assertThatThrownBy(() -> taskRunner.run(taskService, taskModel))
				.isInstanceOf(RuntimeException.class)
				.hasCause(ioEx);
	}
}
