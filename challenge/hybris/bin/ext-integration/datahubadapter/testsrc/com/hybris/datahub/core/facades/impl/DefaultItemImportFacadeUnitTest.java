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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.ImportService;

import com.hybris.datahub.core.config.ImportConfigStrategy;
import com.hybris.datahub.core.dto.ItemImportTaskData;
import com.hybris.datahub.core.facades.ImportError;
import com.hybris.datahub.core.facades.ItemImportResult;
import com.hybris.datahub.core.services.impl.DataHubFacade;
import de.hybris.platform.dataimportcommons.facades.impl.ImportResultConverter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;

/**
 * A unit test for <code>DefaultItemImportFacade</code>
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultItemImportFacadeUnitTest
{
	private static final ItemImportTaskData TASK_DATA = createTaskData();

	@InjectMocks
	private final DefaultItemImportFacade facade = new DefaultItemImportFacade();
	private ItemImportResult itemImportResult;

	@Mock
	private ImportService importService;
	@Mock
	private ImportConfigStrategy importConfigStrategy;
	@Mock
	private ImportResultConverter resultConverter;
	@Mock
	private EventService eventService;
	@Mock
	private DataHubFacade dataHubFacade;

	private static ItemImportTaskData createTaskData()
	{
		final ItemImportTaskData data = new ItemImportTaskData();
		data.setImpexMetaData("INSERT_UPDATE SomeProduct".getBytes());
		data.setPoolName("Test pool");
		data.setPublicationId(1L);
		data.setResultCallbackUrl("http://localhost");
		return data;
	}

	@Before
	public void setUp()
	{
		final ImportResult importResult = setUpImportService();
		setUpImportResultConverter(importResult);
	}

	private ImportResult setUpImportService()
	{
		final ImportResult res = Mockito.mock(ImportResult.class);
		doReturn(Boolean.TRUE).when(res).isSuccessful();
		doReturn(res).when(importService).importData(any(ImportConfig.class));
		return res;
	}

	private void setUpImportResultConverter(final ImportResult importRes)
	{
		itemImportResult = Mockito.mock(ItemImportResult.class);
		doReturn(true).when(itemImportResult).isSuccessful();
		doReturn(itemImportResult).when(resultConverter).convert(importRes);
	}

	@Test
	public void testImportResultIsReturnedSuccessfully()
	{
		facade.importItems(TASK_DATA);

		verify(dataHubFacade).returnImportResult(TASK_DATA.getResultCallbackUrl(), itemImportResult);
	}

	@Test
	public void testImportResultWithHeaderErrors()
	{
		itemImportResult = new ItemImportResult();
		when(resultConverter.convert(setUpImportService())).thenReturn(itemImportResult);
		TASK_DATA.setHeaderErrors(Lists.newArrayList(new ImportError()));
		final ItemImportResult result = facade.importItems(TASK_DATA);
		assertThat(result.getErrors()).hasSize(1);
	}

	@Test
	public void testImportResultWithOutHeaderErrors()
	{
		itemImportResult = new ItemImportResult();
		when(resultConverter.convert(setUpImportService())).thenReturn(itemImportResult);
		final ItemImportResult result = facade.importItems(TASK_DATA);
		assertThat(result.getErrors()).hasSize(1);
	}

	@Test
	public void testImportResultIsSuccessfulWhenImportCompletesSuccessfully()
	{
		final ItemImportResult res = facade.importItems(TASK_DATA);

		assertThat(res).isNotNull();
		assertThat(res.isSuccessful()).isTrue();
	}

	@Test
	public void testImportResultIsErrorWhenErrorIsReportedFromTheImportService()
	{
		simulateErrorResultFromTheImportService();

		final ItemImportResult res = facade.importItems(TASK_DATA);

		assertThat(res.isSuccessful()).isFalse();
	}

	@Test
	public void testImportResultAndHeaderErrorsCombined()
	{
		itemImportResult = new ItemImportResult();
		itemImportResult.addErrors(Lists.newArrayList(new ImportError()));
		when(resultConverter.convert(setUpImportService())).thenReturn(itemImportResult);
		TASK_DATA.setHeaderErrors(Lists.newArrayList(new ImportError()));
		final ItemImportResult result = facade.importItems(TASK_DATA);
		assertThat(result.getErrors()).hasSize(2);
		assertThat(result.isSuccessful()).isFalse();
	}

	private void simulateErrorResultFromTheImportService()
	{
		doReturn(Boolean.FALSE).when(itemImportResult).isSuccessful();
	}

	@Test
	public void testImportResultIsErrorWhenImportServiceCrashes()
	{
		simulateExceptionOnImport();

		final ItemImportResult res = facade.importItems(TASK_DATA);

		assertThat(res.isSuccessful()).isFalse();
	}

	private void simulateExceptionOnImport()
	{
		doThrow(new RuntimeException()).when(importService).importData(any(ImportConfig.class));
	}

	@Test
	public void testImportResultIsErrorWhenImpExScriptIsInvalid() throws ImpExException
	{
		simulateExceptionOnReadingImpExScript();

		final ItemImportResult res = facade.importItems(TASK_DATA);

		assertThat(res.isSuccessful()).isFalse();
	}

	private void simulateExceptionOnReadingImpExScript() throws ImpExException
	{
		doThrow(new ImpExException("Invalid script")).when(importConfigStrategy).createImportConfig(TASK_DATA);
	}

	@Test
	public void testReturnImportResultSuccess()
	{
		facade.importItems(TASK_DATA);

		verify(dataHubFacade).returnImportResult(TASK_DATA.getResultCallbackUrl(), itemImportResult);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testReturnImportResultFailure()
	{
		doThrow(new IllegalArgumentException()).when(dataHubFacade)
				.returnImportResult(TASK_DATA.getResultCallbackUrl(), itemImportResult);
		facade.importItems(TASK_DATA);
	}
}
