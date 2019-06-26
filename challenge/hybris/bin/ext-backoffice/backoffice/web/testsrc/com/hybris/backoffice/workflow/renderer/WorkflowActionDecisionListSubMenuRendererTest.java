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


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.workflow.model.WorkflowActionModel;

import java.util.Collections;
import java.util.function.Predicate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zul.Menupopup;

import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@ExtensibleWidget(level = ExtensibleWidget.ALL)
@RunWith(MockitoJUnitRunner.class)
public class WorkflowActionDecisionListSubMenuRendererTest
		extends AbstractCockpitngUnitTest<WorkflowActionDecisionListSubMenuRenderer>
{
	@Spy
	@InjectMocks
	private WorkflowActionDecisionListSubMenuRenderer renderer;

	@Mock
	private Predicate predicate;

	@Mock
	private Menupopup menupopup;

	@Mock
	private ListColumn listColumn;

	@Mock
	private WorkflowActionModel workflowActionModel;

	@Mock
	private DataType dataType;

	@Mock
	private WidgetInstanceManager widgetInstanceManager;

	@Test
	public void shouldFireComponentRendered()
	{
		CockpitTestUtil.mockZkEnvironment();
		// given
		when(workflowActionModel.getDecisions()).thenReturn(Collections.emptyList());
		when(predicate.negate()).thenReturn(predicate);
		when(Boolean.valueOf(predicate.test(workflowActionModel))).thenReturn(Boolean.FALSE);

		// when
		renderer.render(menupopup, listColumn, workflowActionModel, dataType, widgetInstanceManager);

		// then
		verify(renderer).fireComponentRendered(menupopup, listColumn, workflowActionModel);
	}
}
