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
package com.hybris.backoffice.widgets.actions.workflow;

import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.function.Function;
import java.util.function.Predicate;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.hybris.backoffice.workflow.renderer.actionexecutors.WorkflowTerminateActionExecutor;
import com.hybris.backoffice.workflow.renderer.predicates.TerminateWorkflowActionPredicate;
import com.hybris.cockpitng.labels.LabelService;


public class TerminateWorkflowActionTest extends AbstractWorkflowActionTest<TerminateWorkflowAction>
{

	@Mock
	WorkflowTerminateActionExecutor workflowTerminateActionExecutor;
	@Mock
	TerminateWorkflowActionPredicate terminateWorkflowActionPredicate;
	@Mock
	LabelService labelService;

	@Spy
	@InjectMocks
	private final TerminateWorkflowAction action = new TerminateWorkflowAction();

	@Override
	public TerminateWorkflowAction getActionInstance()
	{
		return action;
	}

	@Override
	public Function<WorkflowModel, Boolean> getWorkflowMockExecutor()
	{
		return workflowTerminateActionExecutor;
	}

	@Override
	public Predicate<WorkflowModel> getWorkflowMockPredicate()
	{
		return terminateWorkflowActionPredicate;
	}
}
