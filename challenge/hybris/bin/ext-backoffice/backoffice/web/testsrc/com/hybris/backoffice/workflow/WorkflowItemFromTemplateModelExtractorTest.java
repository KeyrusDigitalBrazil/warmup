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
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowDecisionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.Collection;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.widgets.networkchart.context.NetworkChartContext;


@RunWith(MockitoJUnitRunner.class)
public class WorkflowItemFromTemplateModelExtractorTest
{
	@Mock
	WorkflowTemplateModel mockedWorkflowTemplateModel;

	@Mock
	NetworkChartContext mockedNetworkChartContext;

	@Mock
	WorkflowItemModelFactory mockedWorkflowItemModelFactory;

	@InjectMocks
	WorkflowItemFromTemplateModelExtractor workflowItemFromTemplateModelExtractor;

	@Test
	public void shouldHandleEmptyNetworkChartContext()
	{
		// given
		given(mockedNetworkChartContext.getInitData(WorkflowTemplateModel.class)).willReturn(Optional.empty());

		// when
		final Collection<WorkflowItem> result = workflowItemFromTemplateModelExtractor.extract(mockedNetworkChartContext);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	public void shouldExtractActions()
	{
		// given
		final WorkflowActionTemplateModel mockedAction1 = mock(WorkflowActionTemplateModel.class);
		final WorkflowItem mockedItem1 = mock(WorkflowItem.class);
		given(mockedWorkflowItemModelFactory.create(mockedAction1)).willReturn(mockedItem1);

		final WorkflowActionTemplateModel mockedAction2 = mock(WorkflowActionTemplateModel.class);
		final WorkflowItem mockedItem2 = mock(WorkflowItem.class);
		given(mockedWorkflowItemModelFactory.create(mockedAction2)).willReturn(mockedItem2);

		given(mockedWorkflowTemplateModel.getActions()).willReturn(newArrayList(mockedAction1, mockedAction2));
		given(mockedNetworkChartContext.getInitData(WorkflowTemplateModel.class))
				.willReturn(Optional.of(mockedWorkflowTemplateModel));

		// when
		final Collection<WorkflowItem> result = workflowItemFromTemplateModelExtractor.extract(mockedNetworkChartContext);

		// then
		assertThat(result).containsOnly(mockedItem1, mockedItem2);
	}

	@Test
	public void shouldExtractActionWithDecision()
	{
		// given
		final WorkflowDecisionTemplateModel mockedDecision = mock(WorkflowDecisionTemplateModel.class);
		final WorkflowItem mockedItemFromDecision = mock(WorkflowItem.class);
		given(mockedWorkflowItemModelFactory.create(mockedDecision)).willReturn(mockedItemFromDecision);

		final WorkflowActionTemplateModel mockedActionWithDecision = mock(WorkflowActionTemplateModel.class);
		given(mockedActionWithDecision.getIncomingTemplateDecisions()).willReturn(newArrayList(mockedDecision));
		final WorkflowItem mockedItemFromAction = mock(WorkflowItem.class);
		given(mockedWorkflowItemModelFactory.create(mockedActionWithDecision)).willReturn(mockedItemFromAction);

		given(mockedWorkflowTemplateModel.getActions()).willReturn(newArrayList(mockedActionWithDecision));
		given(mockedNetworkChartContext.getInitData(WorkflowTemplateModel.class))
				.willReturn(Optional.of(mockedWorkflowTemplateModel));

		// when
		final Collection<WorkflowItem> result = workflowItemFromTemplateModelExtractor.extract(mockedNetworkChartContext);

		// then
		assertThat(result).containsOnly(mockedItemFromAction, mockedItemFromDecision);
	}

	@Test
	public void shouldExtractAndLinks()
	{
		// given
		final WorkflowItem mockedAndLinkAsWorkflowAction = mock(WorkflowItem.class);

		final LinkModel mockedAndLink = mock(LinkModel.class);
		given(mockedAndLink.getProperty(WorkflowItemModelFactory.PROPERTY_AND_CONNECTION_TEMPLATE)).willReturn(true);
		final LinkModel mockedOtherLink = mock(LinkModel.class);

		final WorkflowActionTemplateModel mockedAction = mock(WorkflowActionTemplateModel.class);
		given(mockedAction.getIncomingLinkTemplates()).willReturn(newArrayList(mockedAndLink, mockedOtherLink));

		given(mockedWorkflowItemModelFactory.create(mockedAndLink)).willReturn(mockedAndLinkAsWorkflowAction);
		given(mockedWorkflowTemplateModel.getActions()).willReturn(newArrayList(mockedAction));
		given(mockedNetworkChartContext.getInitData(WorkflowTemplateModel.class))
				.willReturn(Optional.of(mockedWorkflowTemplateModel));

		// when
		final Collection<WorkflowItem> result = workflowItemFromTemplateModelExtractor.extract(mockedNetworkChartContext);

		// then
		assertThat(result).contains(mockedAndLinkAsWorkflowAction);
	}
}
