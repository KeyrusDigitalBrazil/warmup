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

import static com.hybris.cockpitng.testing.util.CockpitTestUtil.findChild;
import static com.hybris.cockpitng.testing.util.CockpitTestUtil.simulateEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;

import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class WorkflowShowFlowViewRendererTest
{
	private static final String MODEL_LINK = "WorkflowShowFlowViewRenderer_LINK";
	private static final String MODEL_VALUE_NAME = "workflowForm.workflowTemplate";
	private static final String YTESTID = "ytestid";
	private static final String SCLASS_WORKFLOW_VIEW_LINK = "yw-workflowview-link";
	private static final String YTESTID_WORKFLOW_VIEW_LINK = "yw-workflowview-link";
	private static final String OUTPUT_SHOW_FLOW = "showFlow";

	@Mock
	WidgetInstanceManager mockedWidgetInstanceManager;

	@InjectMocks
	WorkflowShowFlowViewRenderer workflowShowFlowViewRenderer;

	@Before
	public void before()
	{
		CockpitTestUtil.mockZkEnvironment();
	}

	@Test
	public void shouldRenderCreatingNewLink()
	{
		// given
		final Div parent = new Div();

		final WidgetModel mockedWidgetModel = mock(WidgetModel.class);
		given(mockedWidgetModel.getValue(MODEL_VALUE_NAME, WorkflowTemplateModel.class))
				.willReturn(mock(WorkflowTemplateModel.class));
		given(mockedWidgetInstanceManager.getModel()).willReturn(mockedWidgetModel);

		// when
		workflowShowFlowViewRenderer.render(parent, null, null, null, mockedWidgetInstanceManager);

		// then
		final Button link = findChild(parent, Button.class).orElseThrow(AssertionError::new);
		assertThat(link).isNotNull();
		assertThat(link.getSclass()).isEqualTo(SCLASS_WORKFLOW_VIEW_LINK);
		assertThat(link.getAttribute(YTESTID)).isEqualTo(YTESTID_WORKFLOW_VIEW_LINK);
		assertThat(link.isDisabled()).isFalse();
		verify(mockedWidgetModel).setValue(MODEL_LINK, link);
	}

	@Test
	public void shouldRenderAlreadyCreatedLink()
	{
		// given
		final Div parent = new Div();
		final Button mockedLink = new Button();

		final WidgetModel mockedWidgetModel = mock(WidgetModel.class);
		given(mockedWidgetModel.getValue(MODEL_LINK, Button.class)).willReturn(mockedLink);
		given(mockedWidgetInstanceManager.getModel()).willReturn(mockedWidgetModel);

		// when
		workflowShowFlowViewRenderer.render(parent, null, null, null, mockedWidgetInstanceManager);

		// then
		final Button link = findChild(parent, Button.class).orElseThrow(AssertionError::new);
		assertThat(link).isSameAs(mockedLink);
	}

	@Test
	public void shouldDisableLinkWhenNoWorkflowIsSelected()
	{
		// given
		final Div parent = new Div();
		final WidgetModel mockedWidgetModel = mock(WidgetModel.class);
		given(mockedWidgetInstanceManager.getModel()).willReturn(mockedWidgetModel);

		// when
		workflowShowFlowViewRenderer.render(parent, null, null, null, mockedWidgetInstanceManager);

		// then
		final Button link = findChild(parent, Button.class).orElseThrow(AssertionError::new);
		assertThat(link).isNotNull();
		assertThat(link.isDisabled()).isTrue();
	}

	@Test
	public void shouldDisableLinkWhenWorkflowBecomesDeselected()
	{
		// given
		final Div parent = new Div();

		final Button link = new Button();
		final WidgetModel mockedWidgetModel = mock(WidgetModel.class);
		given(mockedWidgetModel.getValue(MODEL_LINK, Button.class)).willReturn(link);
		given(mockedWidgetInstanceManager.getModel()).willReturn(mockedWidgetModel);
		workflowShowFlowViewRenderer.render(parent, null, null, null, mockedWidgetInstanceManager);

		// when
		workflowShowFlowViewRenderer.modelChanged();

		// then
		assertThat(link.isDisabled()).isTrue();
	}

	@Test
	public void shouldHandleModelChangeWithNullWidgetInstanceManager()
	{
		// given
		final WorkflowShowFlowViewRenderer workflowShowFlowViewRenderer = new WorkflowShowFlowViewRenderer();

		// when
		try
		{
			workflowShowFlowViewRenderer.modelChanged();
		}
		// then
		catch (final NullPointerException e)
		{
			fail("Expected NullPointerException not to be thrown");
		}
	}

	@Test
	public void shouldSendShowFlowToOutputWhenLinkIsClicked()
	{
		// given
		final Div parent = new Div();
		final WidgetModel mockedWidgetModel = mock(WidgetModel.class);
		final WorkflowTemplateModel mockedWorkflowTemplateModel = mock(WorkflowTemplateModel.class);
		given(mockedWidgetModel.getValue(MODEL_VALUE_NAME, WorkflowTemplateModel.class)).willReturn(mockedWorkflowTemplateModel);
		given(mockedWidgetInstanceManager.getModel()).willReturn(mockedWidgetModel);

		workflowShowFlowViewRenderer.render(parent, null, null, null, mockedWidgetInstanceManager);
		final Button link = findChild(parent, Button.class).orElseThrow(AssertionError::new);

		// when
		simulateEvent(link, Events.ON_CLICK, null);

		// then
		verify(mockedWidgetInstanceManager).sendOutput(OUTPUT_SHOW_FLOW, mockedWorkflowTemplateModel);
	}
}
