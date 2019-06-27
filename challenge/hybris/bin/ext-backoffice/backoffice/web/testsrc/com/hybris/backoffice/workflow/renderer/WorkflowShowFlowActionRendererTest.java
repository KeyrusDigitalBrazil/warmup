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
import static org.mockito.Mockito.verify;

import de.hybris.platform.workflow.model.WorkflowModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class WorkflowShowFlowActionRendererTest
{
	@Mock
	private WidgetInstanceManager mockedWidgetInstanceManager;
	@Mock
	private WorkflowModel mockedWorkflowModel;

	private WorkflowShowFlowActionRenderer workflowShowFlowActionRenderer;

	@Before
	public void before()
	{
		CockpitTestUtil.mockZkEnvironment();
		workflowShowFlowActionRenderer = new WorkflowShowFlowActionRenderer();
	}

	@Test
	public void shouldCreateMenuItemShowFlow()
	{
		// given
		final Menupopup menupopup = new Menupopup();

		// when
		workflowShowFlowActionRenderer.render(menupopup, null, null, null, null);

		// then
		assertThat(findChild(menupopup, Menuitem.class).isPresent()).isTrue();
	}

	@Test
	public void shouldSendWorkflowModelToOutputOnClick()
	{
		// given
		final Menupopup menupopup = new Menupopup();
		workflowShowFlowActionRenderer.render(menupopup, null, mockedWorkflowModel, null, mockedWidgetInstanceManager);
		final Menuitem menuitem = findChild(menupopup, Menuitem.class).orElseThrow(AssertionError::new);

		// when
		simulateEvent(menuitem, Events.ON_CLICK, null);

		// then
		verify(mockedWidgetInstanceManager).sendOutput("showFlow", mockedWorkflowModel);
	}
}
