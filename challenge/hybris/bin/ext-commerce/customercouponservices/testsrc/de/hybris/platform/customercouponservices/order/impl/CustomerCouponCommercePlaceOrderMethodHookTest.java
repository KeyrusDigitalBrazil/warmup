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
package de.hybris.platform.customercouponservices.order.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customercouponservices.CustomerCouponService;
import de.hybris.platform.order.InvalidCartException;

@UnitTest
public class CustomerCouponCommercePlaceOrderMethodHookTest {
	@Mock
	private CustomerCouponService customerCouponService;
	@Mock
	private CartModel cart;
	@Mock
	private CommerceCheckoutParameter parameter;
	@Mock
	private CommerceOrderResult result;
	@Mock
	private CustomerCouponCommercePlaceOrderMethodHook hook;

	private OrderModel order;
	private UserModel user;	
	
	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		
		hook.setCustomerCouponService(customerCouponService);

		order = new OrderModel();
		Collection<String> coupons = new ArrayList<String>();
		coupons.add("test");
		order.setAppliedCouponCodes(coupons);
		
		user = new UserModel();
		
		Mockito.when(parameter.getCart()).thenReturn(cart);
		Mockito.when(cart.getUser()).thenReturn(user);
		Mockito.when(result.getOrder()).thenReturn(order);
		Mockito.doNothing().when(hook).removeCouponsForCustomer(user, order);
	}

	@Test
	public void testAfterPlaceOrder() throws InvalidCartException
	{
		hook.afterPlaceOrder(parameter, result);
		verify(hook, times(0)).removeCouponsForCustomer(user,order);
	}
}
