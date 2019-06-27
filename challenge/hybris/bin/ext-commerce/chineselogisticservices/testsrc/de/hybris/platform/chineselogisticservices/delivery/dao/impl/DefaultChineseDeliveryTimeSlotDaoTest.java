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
package de.hybris.platform.chineselogisticservices.delivery.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.chineselogisticservices.model.DeliveryTimeSlotModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultChineseDeliveryTimeSlotDaoTest extends ServicelayerTransactionalTest
{

	private static final String SLOT_CODE = "testcode";
	private static final String SLOT_CODE1 = "testcode1";
	private static final String SLOT_NAME = "testname";

	@Resource(name = "deliveryTimeSlotDao")
	private DefaultChineseDeliveryTimeSlotDao dao;

	@Resource
	private ModelService modelService;

	private DeliveryTimeSlotModel deliveryTimeSlot;

	@Before
	public void prepare()
	{
		deliveryTimeSlot = modelService.create(DeliveryTimeSlotModel.class);
		deliveryTimeSlot.setCode(SLOT_CODE);
		deliveryTimeSlot.setName(SLOT_NAME, new Locale("en"));
		modelService.save(deliveryTimeSlot);
	}

	@Test
	public void testGetAllDeliveryTimeSlots()
	{
		final List<DeliveryTimeSlotModel> deliveryTimeSlots = dao.getAllDeliveryTimeSlots();
		Assert.assertEquals(deliveryTimeSlot, deliveryTimeSlots.get(0));
	}

	@Test
	public void testGetDeliveryTimeSlotByCode()
	{
		final DeliveryTimeSlotModel result = dao.getDeliveryTimeSlotByCode(SLOT_CODE);
		Assert.assertEquals(deliveryTimeSlot, result);
	}

	@Test
	public void testGetDeliveryTimeSlotByCode_null()
	{
		final DeliveryTimeSlotModel result = dao.getDeliveryTimeSlotByCode(SLOT_CODE1);
		Assert.assertEquals(null, result);
	}
}
