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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Ignore;
import org.junit.Test;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.testing.AbstractActionUnitTest;


@Ignore
public abstract class AbstractWorkflowActionTest<T extends CockpitAction<WorkflowModel, WorkflowModel>>
		extends AbstractActionUnitTest<T>
{

	public abstract Function<WorkflowModel, Boolean> getWorkflowMockExecutor();

	public abstract Predicate<WorkflowModel> getWorkflowMockPredicate();

	@Test
	public void shouldCanPerformLogicDelegateToPredicate()
	{
		// given
		final WorkflowModel workflowModel = mock(WorkflowModel.class);
		final ActionContext<WorkflowModel> context = mock(ActionContext.class);
		given(getWorkflowMockPredicate().test(workflowModel)).willReturn(true);
		given(context.getData()).willReturn(workflowModel);

		// when
		final boolean output = getActionInstance().canPerform(context);

		// then
		then(getWorkflowMockPredicate()).should().test(workflowModel);
		assertThat(output).isTrue();
	}

	@Test
	public void shouldPerformLogicDelegateToExecutor()
	{
		// given
		final WorkflowModel workflowModel = mock(WorkflowModel.class);
		final ActionContext<WorkflowModel> context = mock(ActionContext.class);
		given(getWorkflowMockExecutor().apply(workflowModel)).willReturn(true);
		given(context.getData()).willReturn(workflowModel);

		// when
		final ActionResult<WorkflowModel> actionResult = getActionInstance().perform(context);

		// then
		then(getWorkflowMockExecutor()).should().apply(workflowModel);
		assertThat(actionResult.getResultCode()).isEqualTo(ActionResult.SUCCESS);
	}

	@Test
	public void shouldPerformFailWhenExecutorFail()
	{
		// given
		final ActionContext<WorkflowModel> context = mock(ActionContext.class);
		given(context.getData()).willReturn(null);

		// when
		final ActionResult<WorkflowModel> actionResult = getActionInstance().perform(context);

		// then
		then(getWorkflowMockExecutor()).should(never()).apply(any());
		assertThat(actionResult.getResultCode()).isEqualTo(ActionResult.ERROR);
	}

	@Test
	public void shouldUserNotPerformWhenTheresNotDataInContext()
	{
		// given
		final ActionContext<WorkflowModel> context = mock(ActionContext.class);
		given(context.getData()).willReturn(null);

		// when
		final boolean output = getActionInstance().canPerform(context);

		// then
		then(getWorkflowMockPredicate()).should(never()).test(any());
		assertThat(output).isFalse();
	}

}
