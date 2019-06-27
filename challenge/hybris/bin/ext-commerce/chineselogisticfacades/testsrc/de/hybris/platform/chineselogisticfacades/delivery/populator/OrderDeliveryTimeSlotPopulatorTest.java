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
package de.hybris.platform.chineselogisticfacades.delivery.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chineselogisticfacades.delivery.data.DeliveryTimeSlotData;
import de.hybris.platform.chineselogisticservices.model.DeliveryTimeSlotModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.OrderModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class OrderDeliveryTimeSlotPopulatorTest
{
	private static final String TIME_SLOT_CODE = "0001";
	private static final String TIME_SLOT_NAME = "testname";

	@Mock
	private OrderModel source;
	@Mock
	private DeliveryTimeSlotModel deliveryTimeSlot;

	private OrderDeliveryTimeSlotPopulator populator;
	private OrderData target;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		populator = new OrderDeliveryTimeSlotPopulator();
		target = new OrderData();

		BDDMockito.given(deliveryTimeSlot.getCode()).willReturn(TIME_SLOT_CODE);
		BDDMockito.given(deliveryTimeSlot.getName()).willReturn(TIME_SLOT_NAME);
	}

	@Test
	public void testPopulate()
	{
		BDDMockito.given(source.getDeliveryTimeSlot()).willReturn(deliveryTimeSlot);
		populator.populate(source, target);
		final DeliveryTimeSlotData deliveryTimeSlotData = target.getDeliveryTimeSlot();

		Assert.assertNotNull(deliveryTimeSlotData);
		Assert.assertEquals(TIME_SLOT_CODE, deliveryTimeSlotData.getCode());
		Assert.assertEquals(TIME_SLOT_NAME, deliveryTimeSlotData.getName());
	}

	@Test
	public void testPopulate_noDeliverytimeslot()
	{
		populator.populate(source, target);
		final DeliveryTimeSlotData deliveryTimeSlotData = target.getDeliveryTimeSlot();

		Assert.assertNull(deliveryTimeSlotData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulatorWithSourceNull()
	{
		populator.populate(null, target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulatorWithTargetNull()
	{
		populator.populate(source, null);
	}
}
