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
package com.hybris.backoffice.excel.imp.wizard;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import de.hybris.platform.servicelayer.cronjob.CronJobService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.ExcelConstants;
import com.hybris.backoffice.excel.jobs.ExcelCronJobService;
import com.hybris.backoffice.excel.jobs.FileContent;
import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.core.events.CockpitEventQueue;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;


@RunWith(MockitoJUnitRunner.class)
public class ExcelWithoutValidationImportHandlerTest
{

	@Mock
	private ExcelCronJobService excelCronJobService;
	@Mock
	private CronJobService cronJobService;
	@Mock
	private CockpitEventQueue cockpitEventQueue;
	@Mock
	private NotificationService notificationService;
	@Spy
	@InjectMocks
	private ExcelWithoutValidationImportHandler handler = new ExcelWithoutValidationImportHandler();

	@Test
	public void shouldErrorNotificationBeSentWhenFormIsNull()
	{
		// given
		final FlowActionHandlerAdapter adapter = mock(FlowActionHandlerAdapter.class);
		final WidgetInstanceManager wim = mock(WidgetInstanceManager.class);
		final WidgetModel widgetModel = mock(WidgetModel.class);
		given(adapter.getWidgetInstanceManager()).willReturn(wim);
		given(wim.getModel()).willReturn(widgetModel);

		// when
		handler.perform(null, adapter, null);

		// then
		then(notificationService).should().notifyUser(ExcelConstants.NOTIFICATION_SOURCE_EXCEL_IMPORT,
				ExcelConstants.NOTIFICATION_EVENT_TYPE_EXCEL_FORM_IN_MODEL, NotificationEvent.Level.FAILURE);
	}

	@Test
	public void shouldErrorNotificationBeSentWhenExcelFileIsNull()
	{
		// given
		final FlowActionHandlerAdapter adapter = mock(FlowActionHandlerAdapter.class);
		final WidgetInstanceManager wim = mock(WidgetInstanceManager.class);
		final WidgetModel widgetModel = mock(WidgetModel.class);
		given(adapter.getWidgetInstanceManager()).willReturn(wim);
		given(wim.getModel()).willReturn(widgetModel);
		given(widgetModel.getValue(ExcelConstants.EXCEL_FORM_PROPERTY, ExcelImportWizardForm.class))
				.willReturn(new ExcelImportWizardForm());

		// when
		handler.perform(null, adapter, null);

		// then
		then(notificationService).should().notifyUser(ExcelConstants.NOTIFICATION_SOURCE_EXCEL_IMPORT,
				ExcelConstants.NOTIFICATION_EVENT_TYPE_MISSING_EXCEL_FILE, NotificationEvent.Level.FAILURE);
	}

	@Test
	public void shouldImportBeInvokedWhenExcelFileIsNotNull()
	{
		// given
		final FlowActionHandlerAdapter adapter = mock(FlowActionHandlerAdapter.class);
		final WidgetInstanceManager wim = mock(WidgetInstanceManager.class);
		final WidgetModel widgetModel = mock(WidgetModel.class);
		given(adapter.getWidgetInstanceManager()).willReturn(wim);
		given(wim.getModel()).willReturn(widgetModel);
		final ExcelImportWizardForm form = new ExcelImportWizardForm();
		given(widgetModel.getValue(ExcelConstants.EXCEL_FORM_PROPERTY, ExcelImportWizardForm.class)).willReturn(form);
		doReturn(mock(FileContent.class)).when(handler).toFileContent(any());
		doNothing().when(handler).importExcel(any(), any());

		// when
		handler.perform(null, adapter, null);

		// then
		then(handler).should().importExcel(any(), any());
	}

}
