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
package com.hybris.backoffice.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.hybris.cockpitng.i18n.CockpitLocaleService;
import com.hybris.cockpitng.labels.LabelService;


@RunWith(Parameterized.class)
public class WorkflowItemFromWorkflowActionModelGetGroupTest
{
	private final WorkflowActionStatus status;
	private final String expectedGroupName;
	private final WorkflowItemFromWorkflowActionModel workflowItemFromWorkflowActionModel;

	public WorkflowItemFromWorkflowActionModelGetGroupTest(final WorkflowActionStatus status, final String expectedGroupName)
	{
		final WorkflowActionModel workflowActionModel = mock(WorkflowActionModel.class);
		final CockpitLocaleService localeService = mock(CockpitLocaleService.class);
		final LabelService labelService = mock(LabelService.class);

		this.status = status;
		this.expectedGroupName = expectedGroupName;
		this.workflowItemFromWorkflowActionModel = new WorkflowItemFromWorkflowActionModel(workflowActionModel, localeService,
				labelService);
	}

	@Parameterized.Parameters
	public static Collection<Object[]> data()
	{
		final String defaultGroupName = "action";
		return Arrays.asList(new Object[][]
		{
				{ WorkflowActionStatus.PENDING, "actionPending" },
				{ WorkflowActionStatus.IN_PROGRESS, "actionInProgress" },
				{ WorkflowActionStatus.COMPLETED, "actionCompleted" },
				{ WorkflowActionStatus.TERMINATED, "actionTerminated" },
				{ WorkflowActionStatus.PAUSED, defaultGroupName },
				{ WorkflowActionStatus.DISABLED, defaultGroupName },
				{ WorkflowActionStatus.ENDED_THROUGH_END_OF_WORKFLOW, defaultGroupName } });
	}


	@Test
	public void shouldGetGroupNameAsStatus()
	{
		// when
		final String result = workflowItemFromWorkflowActionModel.getGroupName(status);

		// then
		assertThat(result).isEqualTo(expectedGroupName);
	}
}
