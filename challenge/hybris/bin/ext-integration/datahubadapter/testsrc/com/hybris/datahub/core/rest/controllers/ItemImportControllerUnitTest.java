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

package com.hybris.datahub.core.rest.controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;

import com.hybris.datahub.core.dto.ItemImportTaskData;
import com.hybris.datahub.core.facades.ItemImportTaskRunningFacade;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ItemImportControllerUnitTest
{
	private static final String POOL_NAME = "Test pool";
	private static final Long PUBLICATION_ID = 1L;
	private static final String RESULT_CALLBACK_URL = "http://localhost";

	@InjectMocks
	private final ItemImportController resource = new ItemImportController();
	private InputStream input;
	@Mock
	private ItemImportTaskRunningFacade facade;
	@Mock
	private SessionService sessionService;
	@Mock
	private Session session;

	@Before
	public void setUp()
	{
		input = new ByteArrayInputStream(new byte[0]);
		doReturn(session).when(sessionService).getCurrentSession();
	}

	@Test
	public void testScheduleImportTaskIsSuccessful() throws IOException
	{
		resource.importFromStream(POOL_NAME, PUBLICATION_ID, RESULT_CALLBACK_URL, input);

		verify(facade).scheduleImportTask(any(ItemImportTaskData.class));
	}

	@Test
	public void testResourceClosesTheInputStream() throws IOException
	{
		final InputStream in = Mockito.mock(InputStream.class);
		when(in.read(any(byte[].class))).thenReturn(-1);

		resource.importFromStream(POOL_NAME, PUBLICATION_ID, RESULT_CALLBACK_URL, in);

		verify(in).close();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPoolNameNotProvided() throws IOException
	{
		resource.importFromStream(null, PUBLICATION_ID, RESULT_CALLBACK_URL, input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPublicationIdNotProvided() throws IOException
	{
		resource.importFromStream(POOL_NAME, null, RESULT_CALLBACK_URL, input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCallbackUrlNotProvided() throws IOException
	{
		resource.importFromStream(POOL_NAME, PUBLICATION_ID, null, input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInputStreamNotProvided() throws IOException
	{
		resource.importFromStream(POOL_NAME, PUBLICATION_ID, RESULT_CALLBACK_URL, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResultFromImportFailure() throws IOException
	{
		doThrow(new IllegalArgumentException()).when(facade).scheduleImportTask(any(ItemImportTaskData.class));
		resource.importFromStream(POOL_NAME, PUBLICATION_ID, RESULT_CALLBACK_URL, input);
	}
}
