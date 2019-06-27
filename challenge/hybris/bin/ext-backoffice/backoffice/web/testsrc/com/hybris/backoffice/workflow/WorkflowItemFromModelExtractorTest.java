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

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.link.LinkModel;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.Collection;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.widgets.networkchart.context.NetworkChartContext;


@RunWith(MockitoJUnitRunner.class)
public class WorkflowItemFromModelExtractorTest
{
	@Mock
	WorkflowModel mockedWorkflowModel;

	@Mock
	NetworkChartContext mockedNetworkChartContext;

	@Mock
	WorkflowItemModelFactory mockedWorkflowItemModelFactory;

	@InjectMocks
	WorkflowItemFromModelExtractor workflowItemFromModelExtractor;

	@Test
	public void shouldHandleEmptyNetworkChartContext()
	{
		// given
		given(mockedNetworkChartContext.getInitData(WorkflowModel.class)).willReturn(Optional.empty());

		// when
		final Collection<WorkflowItem> result = workflowItemFromModelExtractor.extract(mockedNetworkChartContext);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	public void shouldExtractActions()
	{
		// given
		final WorkflowActionModel mockedAction1 = mock(WorkflowActionModel.class);
		final WorkflowItem mockedItem1 = mock(WorkflowItem.class);
		given(mockedWorkflowItemModelFactory.create(mockedAction1)).willReturn(mockedItem1);

		final WorkflowActionModel mockedAction2 = mock(WorkflowActionModel.class);
		final WorkflowItem mockedItem2 = mock(WorkflowItem.class);
		given(mockedWorkflowItemModelFactory.create(mockedAction2)).willReturn(mockedItem2);

		given(mockedWorkflowModel.getActions()).willReturn(newArrayList(mockedAction1, mockedAction2));
		given(mockedNetworkChartContext.getInitData(WorkflowModel.class)).willReturn(Optional.of(mockedWorkflowModel));

		// when
		final Collection<WorkflowItem> result = workflowItemFromModelExtractor.extract(mockedNetworkChartContext);

		// then
		assertThat(result).containsOnly(mockedItem1, mockedItem2);
	}

	@Test
	public void shouldExtractActionWithDecision()
	{
		// given
		final WorkflowDecisionModel mockedDecision = mock(WorkflowDecisionModel.class);
		final WorkflowItem mockedItemFromDecision = mock(WorkflowItem.class);
		given(mockedWorkflowItemModelFactory.create(mockedDecision)).willReturn(mockedItemFromDecision);

		final WorkflowActionModel mockedActionWithDecision = mock(WorkflowActionModel.class);
		given(mockedActionWithDecision.getIncomingDecisions()).willReturn(newArrayList(mockedDecision));
		final WorkflowItem mockedItemFromAction = mock(WorkflowItem.class);
		given(mockedWorkflowItemModelFactory.create(mockedActionWithDecision)).willReturn(mockedItemFromAction);

		given(mockedWorkflowModel.getActions()).willReturn(newArrayList(mockedActionWithDecision));
		given(mockedNetworkChartContext.getInitData(WorkflowModel.class)).willReturn(Optional.of(mockedWorkflowModel));

		// when
		final Collection<WorkflowItem> result = workflowItemFromModelExtractor.extract(mockedNetworkChartContext);

		// then
		assertThat(result).containsOnly(mockedItemFromAction, mockedItemFromDecision);
	}

	@Test
	public void shouldExtractAndLinks()
	{
		// given
		final WorkflowItem mockedAndLinkAsWorkflowAction = mock(WorkflowItem.class);

		final LinkModel mockedAndLink = mock(LinkModel.class);
		given(mockedAndLink.getProperty(WorkflowItemModelFactory.PROPERTY_AND_CONNECTION)).willReturn(true);
		final LinkModel mockedOtherLink = mock(LinkModel.class);

		final WorkflowActionModel mockedAction = mock(WorkflowActionModel.class);
		given(mockedAction.getIncomingLinks()).willReturn(newArrayList(mockedAndLink, mockedOtherLink));

		given(mockedWorkflowItemModelFactory.create(mockedAndLink)).willReturn(mockedAndLinkAsWorkflowAction);
		given(mockedWorkflowModel.getActions()).willReturn(newArrayList(mockedAction));
		given(mockedNetworkChartContext.getInitData(WorkflowModel.class)).willReturn(Optional.of(mockedWorkflowModel));

		// when
		final Collection<WorkflowItem> result = workflowItemFromModelExtractor.extract(mockedNetworkChartContext);

		// then
		assertThat(result).contains(mockedAndLinkAsWorkflowAction);
	}
}
