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

import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesepaymentservices.model.ChinesePaymentInfoModel;
import de.hybris.platform.chinesepspalipayservices.strategies.AlipayPaymentInfoStrategy;
import de.hybris.platform.core.model.order.OrderModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultAlipaySubmitOrderStrategyUnitTest
{
	private DefaultAlipaySubmitOrderStrategy defaultAlipaySubmitOrderStrategy;

	@Mock
	private AlipayPaymentInfoStrategy alipayPaymentInfoStrategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultAlipaySubmitOrderStrategy = new DefaultAlipaySubmitOrderStrategy();
		defaultAlipaySubmitOrderStrategy.setAlipayPaymentInfoStrategy(alipayPaymentInfoStrategy);
	}

	@Test
	public void testSubmitOrder()
	{
		OrderModel orderModel = new OrderModel();
		orderModel.setStatus(null);
		ChinesePaymentInfoModel chinesePaymentInfoModel = new ChinesePaymentInfoModel();
		orderModel.setPaymentInfo(chinesePaymentInfoModel);
		defaultAlipaySubmitOrderStrategy.submitOrder(orderModel);
		verify(alipayPaymentInfoStrategy).updatePaymentInfoForPlaceOrder(orderModel);

	}

}
