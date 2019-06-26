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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import de.hybris.platform.workflow.WorkflowStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zul.Label;
import org.zkoss.zul.Span;

import com.hybris.backoffice.renderer.WorkflowStatusRenderer;
import com.hybris.backoffice.workflow.WorkflowFacade;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;


@RunWith(MockitoJUnitRunner.class)
@ExtensibleWidget(level = ExtensibleWidget.ALL)
public class WorkflowStatusRendererTest extends AbstractWidgetComponentRendererTest<WorkflowStatusRenderer>
{

	@Mock
	private WorkflowFacade workflowFacade;


	@Override
	@Before
	public void setUp()
	{
		super.setUp();
		when(workflowFacade.getWorkflowStatus(any())).thenReturn(WorkflowStatus.FINISHED);
	}

	@Override
	protected WorkflowStatusRenderer createRendererInstance()
	{
		final WorkflowStatusRenderer workflowStatusRenderer = new WorkflowStatusRenderer();
		workflowStatusRenderer.setWorkflowFacade(workflowFacade);

		return workflowStatusRenderer;
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
	public void testMinimumNotification() {
		super.testMinimumNotification();

		assertFireComponentRendererCalled(Label.class, parent, times(2));
		assertFireComponentRendererCalled(Span.class, parent, times(2));
	}

}
