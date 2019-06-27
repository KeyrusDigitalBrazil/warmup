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

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.widgets.networkchart.context.NetworkChartContext;
import com.hybris.cockpitng.components.visjs.network.data.Network;
import com.hybris.cockpitng.components.visjs.network.response.NetworkUpdates;


@RunWith(MockitoJUnitRunner.class)
public class WorkflowPopulatorTest
{
	@Mock
	WorkflowNetworkFactory mockedWorkflowNetworkFactory;

	@Mock
	WorkflowItemExtractor mockedWorkflowItemExtractor;

	@InjectMocks
	WorkflowPopulator workflowPopulator;

	@Test
	public void shouldPopulateNetwork()
	{
		// given
		final List<WorkflowItem> items = newArrayList(mock(WorkflowItem.class), mock(WorkflowItem.class));

		final NetworkChartContext mockedNetworkChartContext = mock(NetworkChartContext.class);
		given(mockedWorkflowItemExtractor.extract(mockedNetworkChartContext)).willReturn(items);

		final Network mockedNetwork = mock(Network.class);
		given(mockedWorkflowNetworkFactory.create(items)).willReturn(mockedNetwork);

		// when
		final Network result = workflowPopulator.populate(mockedNetworkChartContext);

		// then
		assertThat(result).isEqualTo(mockedNetwork);
	}

	@Test
	public void shouldNotHandleUpdates()
	{
		// given
		final Object anyUpdatedObject = mock(Object.class);
		final NetworkChartContext anyNetworkChartContext = mock(NetworkChartContext.class);

		// when
		final NetworkUpdates result = workflowPopulator.update(anyUpdatedObject, anyNetworkChartContext);

		// then
		assertThat(result).isSameAs(NetworkUpdates.EMPTY);
	}
}
