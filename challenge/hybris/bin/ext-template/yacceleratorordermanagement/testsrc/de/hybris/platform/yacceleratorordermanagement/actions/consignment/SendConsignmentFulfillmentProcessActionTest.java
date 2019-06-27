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
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.externalfulfillment.strategy.SendConsignmentToExternalFulfillmentSystemStrategy;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SendConsignmentFulfillmentProcessActionTest
{

	@InjectMocks
	private SendConsignmentToExternalFulfillmentSystemAction action;

	@Mock
	private ConsignmentProcessModel consignmentProcess;
	@Mock
	private Map<String, SendConsignmentToExternalFulfillmentSystemStrategy> sendConsignmentToExternalFulfillmentSystemStrategyMap;
	@Mock
	private SendConsignmentToExternalFulfillmentSystemStrategy sendConsignmentToExternalFulfillmentSystemStrategy;
	@Mock
	private ModelService modelService;

	private Object fulfillmentSystemConfig;
	private ConsignmentModel consignment;

	@Before
	public void setup()
	{
		consignment = new ConsignmentModel();
		fulfillmentSystemConfig = new Object();
		consignment.setFulfillmentSystemConfig(fulfillmentSystemConfig);
		when(consignmentProcess.getConsignment()).thenReturn(consignment);
		when(sendConsignmentToExternalFulfillmentSystemStrategyMap.get(Object.class.getSimpleName()))
				.thenReturn(sendConsignmentToExternalFulfillmentSystemStrategy);
	}

	@Test
	public void shouldTransitionToOkWhenStrategyExistsForConfig()
	{
		//When
		final String transition = action.execute(consignmentProcess);
		//Then
		assertEquals(SendConsignmentToExternalFulfillmentSystemAction.Transition.OK.toString(), transition);
		verify(modelService, never()).save(consignment);
	}

	@Test
	public void shouldTransitionToErrorWhenLastPreFulfillmentStrategyPresent()
	{
		//Given
		when(sendConsignmentToExternalFulfillmentSystemStrategyMap.get(Object.class.getSimpleName())).thenReturn(null);
		//When
		final String transition = action.execute(consignmentProcess);
		//Then
		assertEquals(SendConsignmentToExternalFulfillmentSystemAction.Transition.ERROR.toString(), transition);
		assertEquals(ConsignmentStatus.CANCELLED, consignment.getStatus());
		verify(modelService, times(1)).save(consignment);

	}
}
