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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.workflow.WorkflowActionService;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zul.Label;

import com.hybris.cockpitng.labels.LabelService;


@RunWith(MockitoJUnitRunner.class)
public class WorkflowDetailedNameRendererTest extends AbstractWidgetComponentRendererTest<WorkflowDetailedNameRenderer>
{
	@Mock
	private WorkflowActionService workflowActionService;
	@Mock
	private LabelService labelService;
	@InjectMocks
	private WorkflowDetailedNameRenderer workflowDetailedNameRenderer;

	@Override
	protected WorkflowDetailedNameRenderer createRendererInstance()
	{
		return workflowDetailedNameRenderer;
	}

	@Override
	protected Object createDefaultRenderedData()
	{
		final WorkflowModel workflowModel = mock(WorkflowModel.class);
		final WorkflowActionModel workflowActionModel = mock(WorkflowActionModel.class);
		when(workflowModel.getActions()).thenReturn(Collections.singletonList(workflowActionModel));
		return workflowModel;
	}

	@Test
	@Override
	public void testMinimumNotification()
	{
		super.testMinimumNotification();

		assertFireComponentRendererCalled(Label.class, parent, times(2));
	}

	@Test
	public void testActiveAction() throws Exception
	{
		// given
		when(Boolean.valueOf(workflowActionService.isActive(any()))).thenReturn(Boolean.TRUE);

		// when
		executeRendering();

		// then
		verify(renderer, never()).getNoTaskLabel(same(widgetInstanceManager));
	}

	@Test
	public void testNoActiveAction() throws Exception
	{
		// given
		when(Boolean.valueOf(workflowActionService.isActive(any()))).thenReturn(Boolean.FALSE);

		// when
		executeRendering(createDefaultRenderedConfiguration(), createDefaultRenderedData());

		// then
		verify(renderer).getNoTaskLabel(same(widgetInstanceManager));
	}
}
