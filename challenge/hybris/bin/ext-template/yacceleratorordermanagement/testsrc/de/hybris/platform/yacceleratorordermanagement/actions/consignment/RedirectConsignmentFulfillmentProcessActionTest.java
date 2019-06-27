/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.yacceleratorordermanagement.actions.consignment;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RedirectConsignmentFulfillmentProcessActionTest
{
	@InjectMocks
	private RedirectConsignmentFulfillmentProcessAction action;

	@Mock
	private ConsignmentModel consignment;
	@Mock
	private ConsignmentProcessModel consignmentProcess;
	@Mock
	private Object fulfillmentSystemConfig;

	@Before
	public void setup()
	{
		when(consignmentProcess.getConsignment()).thenReturn(consignment);
	}

	@Test
	public void shouldTransitionToInternalFulfillment()
	{
		//Given
		when(consignment.getStatus()).thenReturn(ConsignmentStatus.READY);
		//When
		final String transition = action.execute(consignmentProcess);
		//Then
		assertEquals(RedirectConsignmentFulfillmentProcessAction.Transition.INTERNALPROCESS.toString(), transition);
	}

	@Test
	public void shouldTransitionToExternalFulfillment()
	{
		//Given
		when(consignment.getStatus()).thenReturn(ConsignmentStatus.READY);
		when(consignment.getFulfillmentSystemConfig()).thenReturn(fulfillmentSystemConfig);
		//When
		final String transition = action.execute(consignmentProcess);
		//Then
		assertEquals(RedirectConsignmentFulfillmentProcessAction.Transition.EXTERNALPROCESS.toString(), transition);
	}

	@Test
	public void shouldTransitionToUndeterministicFulfillment()
	{
		//Given
		when(consignment.getStatus()).thenReturn(ConsignmentStatus.CANCELLED);
		//When
		final String transition = action.execute(consignmentProcess);
		//Then
		assertEquals(RedirectConsignmentFulfillmentProcessAction.Transition.UNDETERMINISTIC.toString(), transition);
	}

}
