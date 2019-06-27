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
package com.hybris.backoffice.widgets.workflows;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hybris.cockpitng.core.util.impl.TypedSettingsMap;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;

import com.google.common.collect.Lists;
import com.hybris.backoffice.workflow.WorkflowConstants;
import com.hybris.backoffice.workflow.WorkflowFacade;
import com.hybris.backoffice.workflow.WorkflowsTypeFacade;
import com.hybris.backoffice.workflow.wizard.CollaborationWorkflowWizardForm;
import com.hybris.backoffice.workflow.wizard.WorkflowsDropConsumer;
import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.core.events.impl.DefaultCockpitEvent;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectCRUDHandler;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dnd.DragAndDropStrategy;
import com.hybris.cockpitng.list.LazyPageableListModel;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredGlobalCockpitEvent;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;
import com.hybris.cockpitng.widgets.configurableflow.ConfigurableFlowContextParameterNames;


@DeclaredGlobalCockpitEvent(eventName = ObjectFacade.OBJECTS_DELETED_EVENT, scope = CockpitEvent.SESSION)
@DeclaredGlobalCockpitEvent(eventName = ObjectFacade.OBJECTS_UPDATED_EVENT, scope = CockpitEvent.SESSION)
@DeclaredInput(WorkflowsController.SOCKET_IN_REFRESH)
@DeclaredViewEvent(componentID = WorkflowsController.COMP_ID_ADD_WORKFLOW, eventName = Events.ON_CLICK)
@ExtensibleWidget(level = ExtensibleWidget.ALL)
@NullSafeWidget
public class WorkflowsControllerTest extends AbstractWidgetUnitTest<WorkflowsController>
{
	@Spy
	@InjectMocks
	private WorkflowsController controller;

	@Mock
	private Listbox workflowListBox;
	@Mock
	private TypeFacade typeFacade;
	@Mock
	private Div createWorkflowDropArea;
	@Mock
	private WorkflowFacade workflowFacade;
	@Mock
	private WorkflowsTypeFacade workflowsTypeFacade;
	@Mock
	private DragAndDropStrategy dragAndDropStrategy;
	@Mock
	private LazyPageableListModel<WorkflowModel> workflowListModel;

	@Before
	public void setUp()
	{
		doReturn(dragAndDropStrategy).when(controller).getDragAndDropStrategy();
	}

	@Test
	public void testCreateNewWorkflow()
	{
		//given
		final List<ItemModel> itemsDropped = Lists.newArrayList();
		final ArgumentCaptor<WorkflowsDropConsumer> dropOnObjectCaptor = ArgumentCaptor.forClass(WorkflowsDropConsumer.class);
		doNothing().when(dragAndDropStrategy).makeDroppable(any(), any(), any());
		final ComposedTypeModel commonType = mock(ComposedTypeModel.class);
		when(workflowsTypeFacade.findCommonAttachmentType(anyList())).thenReturn(Optional.of(commonType));
		final TypedSettingsMap settingMock = mock(TypedSettingsMap.class);
		when(controller.getWidgetSettings()).thenReturn(settingMock);
		when(settingMock.getBoolean(any())).thenReturn(true);

		//when
		controller.initialize(new Div());
		verify(dragAndDropStrategy).makeDroppable(eq(createWorkflowDropArea), dropOnObjectCaptor.capture(), any());
		final WorkflowsDropConsumer creator = dropOnObjectCaptor.getValue();
		creator.itemsDropped(itemsDropped);

		//then
		assertSocketOutput(WorkflowsController.SOCKET_OUT_CREATE_WORKFLOW, (final Map<String, Object> ctx) -> {
			assertThat(ctx.get(WorkflowConstants.WIZARD_WORKFLOW_CTX_ATTACHMENTS)).isSameAs(itemsDropped);
			assertThat(ctx.get(WorkflowConstants.WIZARD_WORKFLOW_CTX_ATTACHMENT_TYPE)).isEqualTo(commonType);
			assertThat(ctx.get(ConfigurableFlowContextParameterNames.TYPE_CODE.getName()))
					.isEqualTo(CollaborationWorkflowWizardForm.class.getName());
			return true;
		});
	}

	@Test
	public void testCreateNewWorkflowOnClick()
	{
		when(workflowsTypeFacade.findCommonAttachmentType(anyList())).thenReturn(Optional.empty());
		executeViewEvent(WorkflowsController.COMP_ID_ADD_WORKFLOW, Events.ON_CLICK);

		assertSocketOutput(WorkflowsController.SOCKET_OUT_CREATE_WORKFLOW, (final Map<String, Object> ctx) -> {
			assertThat(ctx.get(WorkflowConstants.WIZARD_WORKFLOW_CTX_ATTACHMENTS)).isNull();
			assertThat(ctx.get(WorkflowConstants.WIZARD_WORKFLOW_CTX_ATTACHMENT_TYPE)).isNull();
			assertThat(ctx.get(ConfigurableFlowContextParameterNames.TYPE_CODE.getName()))
					.isEqualTo(CollaborationWorkflowWizardForm.class.getName());
			return true;
		});
	}

	@Test
	public void testWorkflowRefreshedOnObjectUpdate()
	{
		final WorkflowModel workflow = mock(WorkflowModel.class);

		executeGlobalEvent(ObjectFacade.OBJECTS_UPDATED_EVENT, CockpitEvent.SESSION,
				new DefaultCockpitEvent(ObjectFacade.OBJECTS_UPDATED_EVENT, workflow, null));

		verify(workflowListModel).refresh();
	}

	@Test
	public void testPlannedWorkflowRefreshedOnObjectDelete()
	{
		final WorkflowModel workflow = mock(WorkflowModel.class);

		widgetSettings.put(WorkflowsController.SETTING_IS_WORKFLOW_PLANNED_ENABLED, Boolean.TRUE, Boolean.class);

		executeGlobalEvent(ObjectFacade.OBJECTS_DELETED_EVENT, CockpitEvent.SESSION,
				new DefaultCockpitEvent(ObjectFacade.OBJECTS_DELETED_EVENT, workflow, null));

		verify(workflowListModel).refresh();
	}

	@Test
	public void testVFlexEnabledForLazyDataLoading()
	{
		controller.initialize(new Div());

		verify(workflowListBox).setVflex(true);
		verify(workflowListBox, never()).setVflex(false);
	}

	@Test
	public void testRefreshOnWorkflowDeleted()
	{
		final WorkflowModel workflowModel = mock(WorkflowModel.class);

		widgetSettings.put(WorkflowsController.SETTING_IS_WORKFLOW_PLANNED_ENABLED, Boolean.TRUE, Boolean.class);

		executeGlobalEvent(ObjectCRUDHandler.OBJECTS_DELETED_EVENT, CockpitEvent.SESSION,
				new DefaultCockpitEvent(ObjectCRUDHandler.OBJECTS_DELETED_EVENT, Lists.newArrayList(workflowModel), null));

		verify(workflowListModel).refresh();
	}

	@Test
	public void testRefreshOnWorkflowUpdated()
	{
		final WorkflowModel workflowModel = mock(WorkflowModel.class);
		executeGlobalEvent(ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, CockpitEvent.SESSION,
				new DefaultCockpitEvent(ObjectCRUDHandler.OBJECTS_DELETED_EVENT, Lists.newArrayList(workflowModel), null));

		verify(workflowListModel).refresh();
	}

	@Test
	public void refreshOnSocket()
	{
		executeInputSocketEvent(WorkflowsController.SOCKET_IN_REFRESH, true);

		verify(workflowListModel).refresh();
	}

	@Override
	protected WorkflowsController getWidgetController()
	{
		return controller;
	}
}
