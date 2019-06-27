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
package com.hybris.backoffice.widgets.workflowdetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.Objects;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.google.common.collect.Lists;
import com.hybris.backoffice.BackofficeTestUtil;
import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.core.events.impl.DefaultCockpitEvent;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectCRUDHandler;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredGlobalCockpitEvent;
import com.hybris.cockpitng.testing.annotation.DeclaredGlobalCockpitEvents;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredInputs;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;


@DeclaredGlobalCockpitEvents(
{ @DeclaredGlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_DELETED_EVENT, scope = CockpitEvent.SESSION),
		@DeclaredGlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, scope = CockpitEvent.SESSION) })
@DeclaredInputs(
{ @DeclaredInput(value = WorkflowDetailsManagerController.SOCKET_IN_SHOW_WORKFLOW, socketType = WorkflowModel.class) })
@NullSafeWidget
@ExtensibleWidget(level = ExtensibleWidget.ALL)
public class WorkflowDetailsManagerControllerTest extends AbstractWidgetUnitTest<WorkflowDetailsManagerController>
{

	@InjectMocks
	private WorkflowDetailsManagerController controller;
	@Mock
	private ModelService modelService;

	@Override
	protected WorkflowDetailsManagerController getWidgetController()
	{
		return controller;
	}

	@Test
	public void testCurrentWorkflowDeleted()
	{
		final WorkflowModel workflow = mock(WorkflowModel.class);
		widgetModel.setValue(WorkflowDetailsManagerController.MODEL_CURRENT_WORKFLOW, workflow);

		executeGlobalEvent(ObjectCRUDHandler.OBJECTS_DELETED_EVENT, CockpitEvent.SESSION,
				new DefaultCockpitEvent(ObjectCRUDHandler.OBJECTS_DELETED_EVENT, Lists.newArrayList(workflow), null));

		assertSocketOutput(WorkflowDetailsManagerController.SOCKET_OUT_SELECTED_WORKFLOW_DELETED,
				(WorkflowModel o) -> Objects.equals(o, workflow));
		assertThat(controller.getCurrentWorkflow().isPresent()).isFalse();
	}

	@Test
	public void testCurrentWorkflowUpdated()
	{
		final WorkflowModel workflow = new WorkflowModel();
		BackofficeTestUtil.setPk(workflow, 1);
		final WorkflowModel updatedWorkflow = new WorkflowModel();
		BackofficeTestUtil.setPk(updatedWorkflow, 1);

		widgetModel.setValue(WorkflowDetailsManagerController.MODEL_CURRENT_WORKFLOW, workflow);

		executeGlobalEvent(ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, CockpitEvent.SESSION,
				new DefaultCockpitEvent(ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, Lists.newArrayList(updatedWorkflow), null));

		assertSocketOutput(WorkflowDetailsManagerController.SOCKET_OUT_SELECTED_WORKFLOW_UPDATED,
				(WorkflowModel o) -> o == updatedWorkflow);
		assertThat(controller.getCurrentWorkflow().isPresent()).isTrue();
		assertThat(controller.getCurrentWorkflow().get()).isSameAs(updatedWorkflow);
	}

}
