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
package com.hybris.backoffice.widgets.selectivesync;

import static com.hybris.backoffice.widgets.selectivesync.SelectiveSyncController.COMP_ID_CANCEL_BTN;
import static com.hybris.backoffice.widgets.selectivesync.SelectiveSyncController.COMP_ID_SAVE_BTN;
import static com.hybris.backoffice.widgets.selectivesync.SelectiveSyncController.MODEL_VALUE_CHANGED;
import static com.hybris.backoffice.widgets.selectivesync.SelectiveSyncController.SOCKET_INPUT_OBJECT;
import static com.hybris.backoffice.widgets.selectivesync.SelectiveSyncController.SOCKET_OUTPUT_CANCEL;
import static com.hybris.backoffice.widgets.selectivesync.SelectiveSyncController.SOCKET_OUTPUT_OBJECT_SAVED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.synchronization.CatalogVersionSyncJobModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;

import com.hybris.backoffice.widgets.selectivesync.renderer.SelectiveSyncRenderer;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectNotFoundException;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectSavingException;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;


@DeclaredInput(value = SOCKET_INPUT_OBJECT, socketType = CatalogVersionSyncJobModel.class)
@DeclaredViewEvent(eventName = Events.ON_CLICK, componentID = COMP_ID_SAVE_BTN)
@DeclaredViewEvent(eventName = Events.ON_CLICK, componentID = COMP_ID_CANCEL_BTN)
@ExtensibleWidget(level = ExtensibleWidget.ALL)
public class SelectiveSyncControllerTest extends AbstractWidgetUnitTest<SelectiveSyncController>
{
	@Spy
	@InjectMocks
	private SelectiveSyncController selectiveSyncController;

	@Mock
	private Div contentDiv;
	@Mock
	private Div legendDiv;
	@Mock
	private Button saveButton;
	@Mock
	private ObjectFacade objectFacade;
	@Mock
	private SelectiveSyncRenderer selectiveSyncRenderer;

	@Before
	public void setUp()
	{
		selectiveSyncController.initialize(new Div());
	}

	@Test
	public void testGetCurrentObject() throws ObjectNotFoundException
	{
		// given
		final CatalogVersionSyncJobModel object = mock(CatalogVersionSyncJobModel.class);
		when(objectFacade.reload(object)).thenReturn(object);
		selectiveSyncController.handleInputObject(object);

		// when
		final CatalogVersionSyncJobModel currentObject = selectiveSyncController.getCurrentObject();

		// then
		assertThat(object).isEqualTo(currentObject);
	}

	@Test
	public void testIsCurrentObjectAvailable() throws ObjectNotFoundException
	{
		// given
		final CatalogVersionSyncJobModel object = mock(CatalogVersionSyncJobModel.class);
		when(objectFacade.reload(object)).thenReturn(object);
		selectiveSyncController.handleInputObject(object);

		// when
		final boolean currentObjectAvailable = selectiveSyncController.isCurrentObjectAvailable();

		// then
		assertThat(currentObjectAvailable).isTrue();
	}

	@Test
	public void testIsCurrentObjectNotAvailable()
	{
		// given

		// when
		final boolean currentObjectAvailable = selectiveSyncController.isCurrentObjectAvailable();

		// then
		assertThat(currentObjectAvailable).isFalse();
	}

	@Test
	public void testSetCurrentObject()
	{
		// given
		final CatalogVersionSyncJobModel object = mock(CatalogVersionSyncJobModel.class);

		// when
		selectiveSyncController.setCurrentObject(object);

		// then
		final CatalogVersionSyncJobModel currentObject = selectiveSyncController.getCurrentObject();
		assertThat(object).isEqualTo(currentObject);
	}

	@Test
	public void testCreateTreeLegend()
	{
		// given
		when(selectiveSyncController.getLegendDiv().appendChild(any())).thenReturn(true);

		// when
		selectiveSyncController.createTreeLegend();

		// then
		verify(legendDiv, times(2)).appendChild(any());
	}

	@Test
	public void testHandleCancelButtonClick()
	{
		// given
		final WidgetInstanceManager widgetInstanceManager = mock(WidgetInstanceManager.class);
		selectiveSyncController.setWidgetInstanceManager(widgetInstanceManager);

		// when
		selectiveSyncController.handleCancelButtonClick();

		// then
		verify(widgetInstanceManager).sendOutput(SOCKET_OUTPUT_CANCEL, null);
	}

	@Test
	public void testHandleSaveButtonClick() throws ObjectSavingException
	{
		// given
		final WidgetInstanceManager widgetInstanceManager = mock(WidgetInstanceManager.class);
		selectiveSyncController.setWidgetInstanceManager(widgetInstanceManager);

		final CatalogVersionSyncJobModel currentObject = mock(CatalogVersionSyncJobModel.class);
		doReturn(currentObject).when(selectiveSyncController).getCurrentObject();

		final CatalogVersionSyncJobModel savedObject = mock(CatalogVersionSyncJobModel.class);
		when(objectFacade.save(currentObject)).thenReturn(savedObject);

		doNothing().when(selectiveSyncController).handleObjectSavingSuccess(savedObject);
		doNothing().when(selectiveSyncController).setValue(MODEL_VALUE_CHANGED, Boolean.FALSE);

		// when
		selectiveSyncController.handleSaveButtonClick();

		// then
		verify(selectiveSyncController).setValue(MODEL_VALUE_CHANGED, Boolean.FALSE);
		verify(objectFacade).save(currentObject);
		verify(selectiveSyncController).handleObjectSavingSuccess(savedObject);
		verify(widgetInstanceManager).sendOutput(SOCKET_OUTPUT_OBJECT_SAVED, savedObject);
	}

	@Test
	public void testRenderCurrentObject()
	{
		// given
		final CatalogVersionSyncJobModel object = mock(CatalogVersionSyncJobModel.class);
		selectiveSyncController.setCurrentObject(object);

		// when
		selectiveSyncController.renderCurrentObject();

		// then
		verify(selectiveSyncRenderer).render(contentDiv, selectiveSyncController.getCurrentObject(),
				selectiveSyncController.getWidgetInstanceManager());
	}

	@Override
	protected SelectiveSyncController getWidgetController()
	{
		return selectiveSyncController;
	}
}
