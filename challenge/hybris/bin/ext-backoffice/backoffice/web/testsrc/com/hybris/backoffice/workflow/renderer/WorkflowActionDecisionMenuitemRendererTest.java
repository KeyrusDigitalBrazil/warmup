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
package com.hybris.backoffice.workflow.renderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.Collections;
import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.backoffice.workflow.WorkflowEventPublisher;
import com.hybris.backoffice.workflow.impl.DefaultWorkflowDecisionMaker;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@ExtensibleWidget(level = ExtensibleWidget.ALL)
@RunWith(MockitoJUnitRunner.class)
public class WorkflowActionDecisionMenuitemRendererTest extends AbstractCockpitngUnitTest<WorkflowActionDecisionMenuitemRenderer>
{
	private static final String DECISION_CODE = "DECISION";

	@InjectMocks
	private WorkflowActionDecisionMenuitemRenderer renderer;

	@Mock
	private LabelService labelService;
	@Mock
	private PermissionFacade permissionFacade;

	@Spy
	@InjectMocks
	private DefaultWorkflowDecisionMaker workflowDecisionMaker;

	@Mock
	private WorkflowProcessingService workflowProcessingService;
	@Mock
	private WorkflowEventPublisher workflowEventPublisher;
	@Mock
	private NotificationService notificationService;

	@Before
	public void setUp() throws Exception
	{
		workflowDecisionMaker.setWorkflowProcessingService(workflowProcessingService);
		workflowDecisionMaker.setWorkflowEventPublisher(workflowEventPublisher);
		CockpitTestUtil.mockZkEnvironment();
	}

	@Test
	public void whenUserHasPermissionsShouldRenderDecisionLabel()
	{
		final WorkflowDecisionModel decision = createDecision();
		final WorkflowActionModel action = createAction(decision);
		final Menupopup popup = new Menupopup();

		when(labelService.getObjectLabel(decision)).thenReturn(DECISION_CODE);
		mockThatUserHasPermissionForAction(action);
		mockThatUserHasPermissionForDecision(decision);

		renderer.render(popup, null, new ImmutablePair<>(action, decision), null, CockpitTestUtil.mockWidgetInstanceManager());

		final Optional<Menuitem> decisionMenuitem = CockpitTestUtil.findChild(popup, Menuitem.class);
		assertThat(decisionMenuitem.isPresent()).isTrue();
		//noinspection ConstantConditions
		assertThat(decisionMenuitem.get().getLabel()).isEqualTo(DECISION_CODE);
	}

	@Test
	public void whenUserHasNoPermissionsForWorkflowActionShouldNotRenderDecisionLabel()
	{
		final WorkflowDecisionModel decision = createDecision();
		final WorkflowActionModel action = createAction(decision);
		final Menupopup popup = new Menupopup();

		when(labelService.getObjectLabel(decision)).thenReturn(DECISION_CODE);
		mockThatUserHasNoPermissionForAction(action);
		mockThatUserHasPermissionForDecision(decision);

		renderer.render(popup, null, new ImmutablePair<>(action, decision), null, CockpitTestUtil.mockWidgetInstanceManager());

		final Optional<Menuitem> decisionMenuitem = CockpitTestUtil.findChild(popup, Menuitem.class);
		assertThat(decisionMenuitem.isPresent()).isFalse();
	}

	@Test
	public void whenUserHasNoPermissionsForWorkflowDecisionShouldNotRenderDecisionLabel()
	{
		final WorkflowDecisionModel decision = createDecision();
		final WorkflowActionModel action = createAction(decision);
		final Menupopup popup = new Menupopup();

		when(labelService.getObjectLabel(decision)).thenReturn(DECISION_CODE);
		mockThatUserHasPermissionForAction(action);
		mockThatUserHasNoPermissionForDecision(decision);

		renderer.render(popup, null, new ImmutablePair<>(action, decision), null, CockpitTestUtil.mockWidgetInstanceManager());

		final Optional<Menuitem> decisionMenuitem = CockpitTestUtil.findChild(popup, Menuitem.class);
		assertThat(decisionMenuitem.isPresent()).isFalse();
	}

	@Test
	public void whenUserHasPermissionsShouldMakeDecisionOnClick()
	{
		final WorkflowDecisionModel decision = createDecision();
		final WorkflowActionModel action = createAction(decision);
		final Menupopup container = new Menupopup();

		when(labelService.getObjectLabel(decision)).thenReturn(DECISION_CODE);
		mockThatUserHasPermissionForAction(action);
		mockThatUserHasPermissionForDecision(decision);

		renderer.render(container, null, new ImmutablePair<>(action, decision), null, CockpitTestUtil.mockWidgetInstanceManager());
		final Optional<Menuitem> decisionMenuitem = CockpitTestUtil.findChild(container, Menuitem.class);
		assertThat(decisionMenuitem.isPresent()).isTrue();
		//noinspection ConstantConditions
		CockpitTestUtil.simulateEvent(decisionMenuitem.get(), Events.ON_CLICK, null);

		verify(workflowProcessingService).decideAction(action, decision);
	}

	private WorkflowDecisionModel createDecision()
	{
		final WorkflowDecisionModel decision = new WorkflowDecisionModel();
		decision.setCode(DECISION_CODE);
		return decision;
	}

	private WorkflowActionModel createAction(final WorkflowDecisionModel decision)
	{
		final WorkflowActionModel action = new WorkflowActionModel();
		action.setDecisions(Collections.singletonList(decision));
		action.setWorkflow(new WorkflowModel());
		return action;
	}

	private void mockThatUserHasPermissionForAction(final WorkflowActionModel action)
	{
		when(permissionFacade.canReadType(WorkflowActionModel._TYPECODE)).thenReturn(true);
		when(permissionFacade.canReadInstance(action)).thenReturn(true);
		when(permissionFacade.canChangeInstance(action)).thenReturn(true);
	}

	private void mockThatUserHasNoPermissionForAction(final WorkflowActionModel action)
	{
		when(permissionFacade.canReadType(WorkflowActionModel._TYPECODE)).thenReturn(false);
		when(permissionFacade.canReadInstance(action)).thenReturn(false);
		when(permissionFacade.canChangeInstance(action)).thenReturn(false);
	}

	private void mockThatUserHasPermissionForDecision(final WorkflowDecisionModel decision)
	{
		when(permissionFacade.canReadType(WorkflowDecisionModel._TYPECODE)).thenReturn(true);
		when(permissionFacade.canReadInstance(decision)).thenReturn(true);
	}

	private void mockThatUserHasNoPermissionForDecision(final WorkflowDecisionModel decision)
	{
		when(permissionFacade.canReadType(WorkflowDecisionModel._TYPECODE)).thenReturn(false);
		when(permissionFacade.canReadInstance(decision)).thenReturn(false);
	}
}
