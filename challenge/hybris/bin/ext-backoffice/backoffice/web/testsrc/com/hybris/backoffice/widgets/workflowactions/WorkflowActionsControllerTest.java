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
package com.hybris.backoffice.widgets.workflowactions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.workflow.model.WorkflowActionModel;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;

import com.google.common.collect.Lists;
import com.hybris.backoffice.workflow.WorkflowFacade;
import com.hybris.cockpitng.admin.CockpitMainWindowComposer;
import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectNotFoundException;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredGlobalCockpitEvent;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;


@DeclaredGlobalCockpitEvent(eventName = CockpitMainWindowComposer.HEARTBEAT_EVENT, scope = CockpitEvent.SESSION)
@DeclaredGlobalCockpitEvent(eventName = ObjectFacade.OBJECTS_DELETED_EVENT, scope = CockpitEvent.SESSION)
@NullSafeWidget
@ExtensibleWidget(level = ExtensibleWidget.ALL)
public class WorkflowActionsControllerTest extends AbstractWidgetUnitTest<WorkflowActionsController>
{
	@Mock
	private WorkflowFacade workflowFacade;
	@Mock
	private Listbox workflowActionsListbox;
	@Mock
	private TypeFacade typeFacade;
	@Mock
	private ObjectFacade objectFacade;
	@InjectMocks
	private WorkflowActionsController controller;

	@Override
	protected WorkflowActionsController getWidgetController()
	{
		return controller;
	}

	@Test
	public void taskCounterUpdatedOnInit()
	{

		given(workflowFacade.getWorkflowActions()).willReturn(Lists.newArrayList(new WorkflowActionModel(),
				new WorkflowActionModel(), new WorkflowActionModel(), new WorkflowActionModel()));

		controller.initialize(new Div());

		assertSocketOutput(WorkflowActionsController.SOCKET_OUT_NUMBER_OF_WORKFLOW_ACTIONS, 2, new ArgumentMatcher<Integer>()
		{
			@Override
			public boolean matches(final Object o)
			{
				return Integer.valueOf(4).equals(o);
			}
		});
	}

	@Test
	public void taskCounterUpdatedOnInitWhenNoWorkflowsFound()
	{
		given(workflowFacade.getWorkflowActions()).willReturn(new ArrayList<>());
		controller.initialize(new Div());

		assertSocketOutput(WorkflowActionsController.SOCKET_OUT_NUMBER_OF_WORKFLOW_ACTIONS,
				(Integer o) -> Integer.valueOf(0).equals(o));
	}

	@Test
	public void taskCounterUpdatedOnHeartbeat()
	{
		controller.initialize(new Div());
		given(workflowFacade.getWorkflowActions()).willReturn(Arrays.asList(new WorkflowActionModel(), new WorkflowActionModel(),
				new WorkflowActionModel(), new WorkflowActionModel()));

		final CockpitEvent cockpitEvent = mock(CockpitEvent.class);
		executeGlobalEvent(CockpitMainWindowComposer.HEARTBEAT_EVENT, CockpitEvent.SESSION, cockpitEvent);

		assertSocketOutput(WorkflowActionsController.SOCKET_OUT_NUMBER_OF_WORKFLOW_ACTIONS,
				(Integer o) -> Integer.valueOf(4).equals(o));
	}

	@Test
	public void shouldReloadActionWhenAttachedItemIsDeleted() throws ObjectNotFoundException
	{
		// given
		final WorkflowActionModel workflowAction = mock(WorkflowActionModel.class);
		final WorkflowActionModel reloadedWorkflowAction = mock(WorkflowActionModel.class);
		final ProductModel product = mock(ProductModel.class);

		given(workflowFacade.getWorkflowActions()).willReturn(Lists.newArrayList(workflowAction));
		given(workflowAction.getAttachmentItems()).willReturn(Lists.newArrayList(product));
		given(objectFacade.reload(workflowAction)).willReturn(reloadedWorkflowAction);

		final CockpitEvent cockpitEvent = mock(CockpitEvent.class);
		given(cockpitEvent.getDataAsCollection()).willReturn(Lists.newArrayList(product));

		controller.initialize(new Div());

		// when
		executeGlobalEvent(ObjectFacade.OBJECTS_DELETED_EVENT, CockpitEvent.SESSION, cockpitEvent);

		// then
		assertThat(controller.getWorkflowActionsListModel()).containsExactly(reloadedWorkflowAction);
	}
}
