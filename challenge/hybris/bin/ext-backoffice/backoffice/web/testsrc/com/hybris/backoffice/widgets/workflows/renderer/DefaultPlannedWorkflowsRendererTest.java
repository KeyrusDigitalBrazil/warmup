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
package com.hybris.backoffice.widgets.workflows.renderer;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.workflow.model.WorkflowItemAttachmentModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.Collections;
import java.util.function.Function;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listitem;

import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.cockpitng.common.renderer.AbstractCustomMenuActionRenderer;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectNotFoundException;
import com.hybris.cockpitng.dnd.DragAndDropStrategy;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class DefaultPlannedWorkflowsRendererTest
{
	@Spy
	@InjectMocks
	private DefaultPlannedWorkflowsRenderer plannedWorkflowsRenderer;

	@Mock
	private Function<WorkflowModel, Boolean> startWorkflowExecutor;
	@Mock
	private WorkflowModel data;
	@Mock
	private WidgetInstanceManager wim;
	@Mock
	private DragAndDropStrategy dragAndDropStrategy;
	@Mock
	private AbstractCustomMenuActionRenderer abstractCustomMenuActionRenderer;
	@Mock
	private ObjectFacade objectFacade;
	@Mock
	private LabelService labelService;
	@Mock
	private NotificationService notificationService;

	private Listitem listitem;

	@Before
	public void setUp() throws ObjectNotFoundException
	{
		CockpitTestUtil.mockZkEnvironment();
		given(objectFacade.reload(data)).willReturn(data);
		final WorkflowTemplateModel job = mock(WorkflowTemplateModel.class);
		when(job.getName()).thenReturn("template name");
		when(data.getJob()).thenReturn(job);
		when(data.getName()).thenReturn("instance name");
		listitem = new Listitem();
	}

	@Test
	public void shouldDroppingOnComponentInvokeLabelUpdate()
	{
		// given
		render(false);
		final Div droppableContent = (Div) listitem.query("." + DefaultPlannedWorkflowsRenderer.SCLASS_WORKFLOWS_LIST_DROP);
		// when
		CockpitTestUtil.simulateEvent(droppableContent, Events.ON_DROP, null);

		// then
		verify(plannedWorkflowsRenderer, times(2)).updateNoOfAttachmentsLabel(eq(wim), eq(data), any(Label.class));
	}

	@Test
	public void shouldClickingOnButtonInvokeStartWorkflowExecutorWhenWorkflowHasAttachments()
	{
		// given
		render(true);
		final Button startWorkflowButton = (Button) listitem
				.query("." + DefaultPlannedWorkflowsRenderer.SCLASS_WORKFLOWS_LIST_BOTTOM_START);
		// when
		CockpitTestUtil.simulateEvent(startWorkflowButton, Events.ON_CLICK, null);
		// then
		verify(startWorkflowExecutor).apply(data);
	}

	@Test
	public void shouldClickingOnButtonNotInvokeStartWorkflowExecutorWhenWorkflowHasNotAttachments()
	{
		// given
		render(false);
		final Button startWorkflowButton = (Button) listitem
				.query("." + DefaultPlannedWorkflowsRenderer.SCLASS_WORKFLOWS_LIST_BOTTOM_START);
		// when
		CockpitTestUtil.simulateEvent(startWorkflowButton, Events.ON_CLICK, null);
		// then
		verify(startWorkflowExecutor, never()).apply(data);
	}

	@Test
	public void shouldCountOnlyNotNullAttachments()
	{
		// given
		final WorkflowItemAttachmentModel attachmentModel1 = mock(WorkflowItemAttachmentModel.class);
		final WorkflowItemAttachmentModel attachmentModel2 = mock(WorkflowItemAttachmentModel.class);
		final WorkflowItemAttachmentModel attachmentModel3 = mock(WorkflowItemAttachmentModel.class);
		given(attachmentModel1.getItem()).willReturn(mock(ItemModel.class));
		given(attachmentModel2.getItem()).willReturn(null);
		given(attachmentModel3.getItem()).willReturn(null);
		given(data.getAttachments()).willReturn(Lists.newArrayList(attachmentModel1, attachmentModel2, attachmentModel3));
		final Label label = new Label();

		// when
		plannedWorkflowsRenderer.updateNoOfAttachmentsLabel(wim, data, label);

		// then
		verify(plannedWorkflowsRenderer).getAttachmentsLabelValue(any(), argThat(new ArgumentMatcher<Long>()
		{
			@Override
			public boolean matches(final Object o)
			{
				final Long input = (Long) o;
				return input == 1l;
			}
		}));
	}

	private void render(final boolean withAttachments)
	{
		if (withAttachments)
		{
			final WorkflowItemAttachmentModel workflowItemAttachmentModel = mock(WorkflowItemAttachmentModel.class);
			given(workflowItemAttachmentModel.getItem()).willReturn(mock(ItemModel.class));
			when(data.getAttachments()).thenReturn(Lists.newArrayList(workflowItemAttachmentModel));
		}
		else
		{
			when(data.getAttachments()).thenReturn(Collections.emptyList());
		}
		listitem = new Listitem();
		plannedWorkflowsRenderer.render(listitem, null, data, null, wim);
		doNothing().when(plannedWorkflowsRenderer).updateNoOfAttachmentsLabel(eq(wim), eq(data), any(Label.class));
	}

}
