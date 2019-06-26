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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.model.PickUpDeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RedirectConsignmentByDeliveryModeActionTest
{

	private ConsignmentEntryModel consignmentEntry;
	private ConsignmentProcessModel consignmentProcessModel;
	private  Set<ConsignmentEntryModel> consignmentEntriesModel;

	@InjectMocks
	private final RedirectConsignmentByDeliveryModeAction action = new RedirectConsignmentByDeliveryModeAction();

	@Before
	public void setup()
	{
		consignmentEntry = spy(new ConsignmentEntryModel());

		consignmentEntriesModel = new HashSet<>();
		consignmentEntriesModel.add(consignmentEntry);

	}

	@Test
	public void shouldTransitToPickupBranchOfConsignmentWorkflow() throws Exception
	{
		final ConsignmentModel shippingConsignment = new ConsignmentModel();
		shippingConsignment.setConsignmentEntries(consignmentEntriesModel);
		shippingConsignment.setDeliveryMode(new PickUpDeliveryModeModel());
		consignmentProcessModel = new ConsignmentProcessModel();
		consignmentProcessModel.setConsignment(shippingConsignment);

		final String transition = action.execute(consignmentProcessModel);
		assertEquals(RedirectConsignmentByDeliveryModeAction.Transition.PICKUP.toString(), transition);
	}

	@Test
	public void shouldTransitToShipBranchOfConsignmentWorkflow() throws Exception
	{
		final ConsignmentModel pickupConsignment = new ConsignmentModel();
		pickupConsignment.setConsignmentEntries(consignmentEntriesModel);
		pickupConsignment.setDeliveryMode(new ZoneDeliveryModeModel());
		consignmentProcessModel = new ConsignmentProcessModel();
		consignmentProcessModel.setConsignment(pickupConsignment);

		final String transition = action.execute(consignmentProcessModel);
		assertEquals(RedirectConsignmentByDeliveryModeAction.Transition.SHIP.toString(), transition);
	}

}
