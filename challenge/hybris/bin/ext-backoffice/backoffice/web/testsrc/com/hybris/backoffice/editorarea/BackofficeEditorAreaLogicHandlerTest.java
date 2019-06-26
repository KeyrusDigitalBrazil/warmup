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
package com.hybris.backoffice.editorarea;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.Button;

import com.hybris.backoffice.sync.facades.SynchronizationFacade;
import com.hybris.cockpitng.core.Executable;
import com.hybris.cockpitng.core.util.impl.TypedSettingsMap;
import com.hybris.cockpitng.engine.WidgetInstanceManager;


@RunWith(MockitoJUnitRunner.class)
public class BackofficeEditorAreaLogicHandlerTest
{

	@Mock
	private SynchronizationFacade synchronizationFacade;
	@InjectMocks
	private final BackofficeEditorAreaLogicHandler handler = spy(new BackofficeEditorAreaLogicHandler());
	@Mock
	private WidgetInstanceManager wim;
	@Mock
	private Executable save;
	@Mock
	private ItemModel item;

	@Before
	public void setUp()
	{
		when(wim.getWidgetSettings()).thenReturn(new TypedSettingsMap());
	}

	@Test
	public void testSyncNotRunning()
	{
		//given
		doReturn(Boolean.FALSE).when(synchronizationFacade).isSyncInProgress(item);

		//when
		handler.executeSaveWithConfirmation(wim, save, item);

		//then
		verify(save).execute();
	}

	@Test
	public void testSyncRunningIgnoreAndSaveClicked() throws Exception
	{
		//given
		doReturn(Boolean.TRUE).when(synchronizationFacade).isSyncInProgress(item);
		wim.getWidgetSettings().put(BackofficeEditorAreaLogicHandler.SETTING_DISABLE_SAVE_ON_SYNC, Boolean.FALSE, Boolean.class);

		final ArgumentCaptor<Button[]> buttonsCaptor = ArgumentCaptor.forClass(Button[].class);
		final ArgumentCaptor<EventListener> onClickCaptor = ArgumentCaptor.forClass(EventListener.class);

		doNothing().when(handler).showMessageBox(anyString(), anyString(), buttonsCaptor.capture(), anyObject(),
				onClickCaptor.capture());

		//when
		handler.executeSaveWithConfirmation(wim, save, item);
		//simulate user clicked ignore and save
		onClickCaptor.getValue().onEvent(new Messagebox.ClickEvent(Events.ON_CLICK, null, Button.IGNORE));

		//then
		assertThat(buttonsCaptor.getValue()).containsOnly(Button.CANCEL, Button.IGNORE);
		verify(save).execute();
	}

	@Test
	public void testSyncRunningIgnoreCancelClicked() throws Exception
	{
		//given
		doReturn(Boolean.TRUE).when(synchronizationFacade).isSyncInProgress(item);
		wim.getWidgetSettings().put(BackofficeEditorAreaLogicHandler.SETTING_DISABLE_SAVE_ON_SYNC, Boolean.FALSE, Boolean.class);

		final ArgumentCaptor<Button[]> buttonsCaptor = ArgumentCaptor.forClass(Button[].class);
		final ArgumentCaptor<EventListener> onClickCaptor = ArgumentCaptor.forClass(EventListener.class);

		doNothing().when(handler).showMessageBox(anyString(), anyString(), buttonsCaptor.capture(), anyObject(),
				onClickCaptor.capture());

		//when
		handler.executeSaveWithConfirmation(wim, save, item);
		//simulate user clicked ignore and save
		onClickCaptor.getValue().onEvent(new Messagebox.ClickEvent(Events.ON_CLICK, null, Button.CANCEL));

		//then
		assertThat(buttonsCaptor.getValue()).containsOnly(Button.CANCEL, Button.IGNORE);
		verify(save, never()).execute();
	}

	@Test
	public void testSyncRunningIgnoreDisabled() throws Exception
	{
		//given
		doReturn(Boolean.TRUE).when(synchronizationFacade).isSyncInProgress(item);
		wim.getWidgetSettings().put(BackofficeEditorAreaLogicHandler.SETTING_DISABLE_SAVE_ON_SYNC, Boolean.TRUE, Boolean.class);

		final ArgumentCaptor<Button[]> buttonsCaptor = ArgumentCaptor.forClass(Button[].class);
		final ArgumentCaptor<EventListener> onClickCaptor = ArgumentCaptor.forClass(EventListener.class);

		doNothing().when(handler).showMessageBox(anyString(), anyString(), buttonsCaptor.capture(), anyObject(),
				onClickCaptor.capture());

		//when
		handler.executeSaveWithConfirmation(wim, save, item);
		//simulate user clicked cancel
		onClickCaptor.getValue().onEvent(new Messagebox.ClickEvent(Events.ON_CLICK, null, Button.CANCEL));

		//then
		assertThat(buttonsCaptor.getValue()).containsOnly(Button.CANCEL);
		verify(save, never()).execute();
	}

}
