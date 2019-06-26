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
package de.hybris.platform.chineselogisticservices.delivery.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chineselogisticservices.delivery.dao.DeliveryTimeSlotDao;
import de.hybris.platform.chineselogisticservices.model.DeliveryTimeSlotModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultChineseDeliveryTimeSlotServiceTest
{

	private static final String DELIVERY_TIME_SLOT_CODE = "testcode";

	@Mock
	private DeliveryTimeSlotDao deliveryTimeSlotDao;
	@Mock
	private ModelService modelService;

	private DefaultChineseDeliveryTimeSlotService service;
	private List<DeliveryTimeSlotModel> deliveryTimeSlots;
	private DeliveryTimeSlotModel deliveryTimeSlot;
	private CartModel cart;


	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		service = new DefaultChineseDeliveryTimeSlotService();
		service.setDeliveryTimeSlotDao(deliveryTimeSlotDao);
		service.setModelService(modelService);

		deliveryTimeSlots = new ArrayList<>();
		deliveryTimeSlot = new DeliveryTimeSlotModel();
		cart = new CartModel();
		cart.setDeliveryTimeSlot(deliveryTimeSlot);

		BDDMockito.given(deliveryTimeSlotDao.getAllDeliveryTimeSlots()).willReturn(deliveryTimeSlots);
		BDDMockito.given(deliveryTimeSlotDao.getDeliveryTimeSlotByCode(DELIVERY_TIME_SLOT_CODE)).willReturn(deliveryTimeSlot);
		BDDMockito.doNothing().when(modelService).save(cart);
	}

	@Test
	public void testGetAllDeliveryTimeSlots()
	{
		final List<DeliveryTimeSlotModel> list = service.getAllDeliveryTimeSlots();
		Assert.assertEquals(deliveryTimeSlots, list);
	}

	@Test
	public void testGetDeliveryTimeSlotByCode()
	{
		final DeliveryTimeSlotModel model = service.getDeliveryTimeSlotByCode(DELIVERY_TIME_SLOT_CODE);
		Assert.assertEquals(deliveryTimeSlot, model);
	}

	@Test
	public void testSetDeliveryTimeSlot()
	{
		service.setDeliveryTimeSlot(cart, DELIVERY_TIME_SLOT_CODE);
		BDDMockito.verify(modelService, BDDMockito.times(1)).save(cart);
	}
}
