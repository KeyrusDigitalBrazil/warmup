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
package de.hybris.platform.chinesepaymentservices.order;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesepaymentservices.model.ChinesePaymentInfoModel;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ChineseCommercePlaceOrderMethodHookTest
{
	@Mock
	private ModelService modelService;
	@Mock
	private CartModel cart;
	@Mock
	private ChinesePaymentInfoModel chinesePaymentInfo;

	private ChineseCommercePlaceOrderMethodHook hook;
	private CommerceCheckoutParameter parameter;
	private CommerceOrderResult result;
	private OrderModel order;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		order = new OrderModel();

		parameter = new CommerceCheckoutParameter();
		parameter.setCart(cart);

		result = new CommerceOrderResult();
		result.setOrder(order);

		hook = new ChineseCommercePlaceOrderMethodHook();
		hook.setModelService(modelService);

		given(cart.getChinesePaymentInfo()).willReturn(chinesePaymentInfo);
		Mockito.doNothing().when(modelService).save(result.getOrder());
		Mockito.doNothing().when(modelService).refresh(result.getOrder());
	}

	@Test
	public void testAfterPlaceOrder() throws InvalidCartException
	{
		hook.afterPlaceOrder(parameter, result);

		assertEquals(chinesePaymentInfo, order.getChinesePaymentInfo());
		assertEquals(OrderStatus.CREATED, order.getStatus());
		assertEquals(PaymentStatus.NOTPAID, order.getPaymentStatus());
	}
}
