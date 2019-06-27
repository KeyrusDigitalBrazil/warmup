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
import de.hybris.platform.orderprocessing.events.ConsignmentProcessingEvent;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ConfirmShippingActionTest
{
	@InjectMocks
	private ConfirmShipConsignmentAction action = new ConfirmShipConsignmentAction();

	@Mock
	private ConsignmentProcessModel consignmentProcessModel;
	@Mock
	private ConsignmentModel consignmentModel;
	@Mock
	private EventService eventService;
	@Mock
	private ModelService modelService;

	@Before
	public void setup()
	{
		when(consignmentProcessModel.getConsignment()).thenReturn(consignmentModel);
		doNothing().when(modelService).save(any());
	}

	@Test
	public void shouldSetConsignmentStatusToShippedWhenExecuted() throws Exception
	{
		//when
		action.executeAction(consignmentProcessModel);
		//then
		verify(consignmentModel, times(1)).setStatus(ConsignmentStatus.SHIPPED);
		verify(eventService).publishEvent(any(ConsignmentProcessingEvent.class));
	}
}
