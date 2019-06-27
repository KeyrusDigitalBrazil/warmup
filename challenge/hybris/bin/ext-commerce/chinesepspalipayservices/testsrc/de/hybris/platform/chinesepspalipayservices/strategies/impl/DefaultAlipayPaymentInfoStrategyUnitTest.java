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
package de.hybris.platform.chinesepspalipayservices.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesepaymentservices.enums.ServiceType;
import de.hybris.platform.chinesepaymentservices.model.ChinesePaymentInfoModel;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultAlipayPaymentInfoStrategyUnitTest
{
	private DefaultAlipayPaymentInfoStrategy defaultAlipayPaymentInfoStrategy;
	@Mock
	private UserService userService;
	@Mock
	private ModelService modelService;

	private UserModel customer;

	private ChinesePaymentInfoModel chinesePaymentInfo;

	private OrderModel orderModel;
	
	private OrderModel orderModel2;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultAlipayPaymentInfoStrategy = new DefaultAlipayPaymentInfoStrategy();
		customer = new CustomerModel();
		customer.setUid("test-uid");

		chinesePaymentInfo = new ChinesePaymentInfoModel();

		orderModel = new OrderModel();
		orderModel.setCode("test-code");
		orderModel.setPaymentInfo(chinesePaymentInfo);
		orderModel2 = new OrderModel();
		orderModel2.setPaymentInfo(null);
		

		defaultAlipayPaymentInfoStrategy.setModelService(modelService);
		defaultAlipayPaymentInfoStrategy.setUserService(userService);
	}

	@Test
	public void testUpdatePaymentInfoForPaymentMethod()
	{
		given(userService.getCurrentUser()).willReturn(customer);

		ChinesePaymentInfoModel chinesePaymentInfoModel = new ChinesePaymentInfoModel();
		defaultAlipayPaymentInfoStrategy.updatePaymentInfoForPayemntMethod(chinesePaymentInfoModel);

		assertTrue(chinesePaymentInfoModel.getCode().startsWith("test-uid"));
		assertEquals(customer, chinesePaymentInfoModel.getUser());
		assertEquals(ServiceType.DIRECTPAY, chinesePaymentInfoModel.getServiceType());
		verify(modelService, times(1)).save(chinesePaymentInfoModel);
		
		
	}

	@Test
	public void testUpdatePaymentInfoForPlaceOrder()
	{
		defaultAlipayPaymentInfoStrategy.updatePaymentInfoForPlaceOrder(orderModel);

		assertEquals(orderModel.getCode(), chinesePaymentInfo.getOrderCode());
		assertEquals(PaymentStatus.NOTPAID, orderModel.getPaymentStatus());
		verify(modelService).save(orderModel);
		verify(modelService).save(chinesePaymentInfo);
		
		defaultAlipayPaymentInfoStrategy.updatePaymentInfoForPlaceOrder(orderModel2);
	}

}
