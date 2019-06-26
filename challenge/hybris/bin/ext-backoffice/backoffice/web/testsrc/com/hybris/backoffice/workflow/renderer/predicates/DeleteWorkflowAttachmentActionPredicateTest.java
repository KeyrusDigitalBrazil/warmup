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
package com.hybris.backoffice.workflow.renderer.predicates;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.platform.workflow.WorkflowStatus;
import de.hybris.platform.workflow.model.WorkflowItemAttachmentModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.workflow.WorkflowFacade;

@RunWith(MockitoJUnitRunner.class)
public class DeleteWorkflowAttachmentActionPredicateTest
{
	@Mock
	private WorkflowFacade facade;
	@Mock
	private WorkflowItemAttachmentModel workflowItemAttachment;
	@Mock
	private WorkflowModel workflow;

	@InjectMocks
	private DeleteWorkflowAttachmentActionPredicate predicate;

	@Before
	public void setUp()
	{
		when(workflowItemAttachment.getWorkflow()).thenReturn(workflow);
	}

	@Test
	public void testPredicateWhenWorkflowIsPlanned()
	{
		//given
		when(facade.getWorkflowStatus(workflow)).thenReturn(WorkflowStatus.PLANNED);

		//when
		final boolean result = predicate.test(workflowItemAttachment);

		//then
		assertThat(result).isTrue();
	}

	@Test
	public void testPredicateWhenWorkflowIsRunning()
	{
		//given
		when(facade.getWorkflowStatus(workflow)).thenReturn(WorkflowStatus.RUNNING);

		//when
		final boolean result = predicate.test(workflowItemAttachment);

		//then
		assertThat(result).isFalse();
	}
}