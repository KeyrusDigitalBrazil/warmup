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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DeliveryTimeSlotPopulatorTest
{
	private DeliveryTimeSlotData target;
	private static final String TIME_SLOT_CODE = "0001";
	private static final String TIME_SLOT_NAME = "testname";

	private DeliveryTimeSlotPopulator populator;

	@Mock
	private DeliveryTimeSlotModel source;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		populator = new DeliveryTimeSlotPopulator();
		target = new DeliveryTimeSlotData();

		BDDMockito.given(source.getCode()).willReturn(TIME_SLOT_CODE);
		BDDMockito.given(source.getName()).willReturn(TIME_SLOT_NAME);
	}

	@Test
	public void testPopulate()
	{
		populator.populate(source, target);
		Assert.assertEquals(TIME_SLOT_CODE, target.getCode());
		Assert.assertEquals(TIME_SLOT_NAME, target.getName());
	}

}
