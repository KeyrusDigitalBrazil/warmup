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
package de.hybris.platform.couponservices.order.hooks;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.couponservices.CouponServiceException;
import de.hybris.platform.couponservices.service.data.CouponResponse;
import de.hybris.platform.couponservices.services.CouponService;
import de.hybris.platform.order.InvalidCartException;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * JUnit test suite for implementation {@link CouponRedemptionMethodHook}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CouponRedemptionMethodHookUnitTest
{
	private static final String COUPON_CODE = "testCouponCode";

	@InjectMocks
	private CouponRedemptionMethodHook couponRedemptionMethodHook;

	@Mock
	private CouponService couponService;

	@Mock
	private CommerceOrderResult commerceOrderResult;

	@Mock
	private CommerceCheckoutParameter parameter;

	@Mock
	private OrderModel order;

	@Test
	public void testCouponRedemptionAfterPlaceOrder() throws InvalidCartException
	{
		when(commerceOrderResult.getOrder()).thenReturn(order);
		when(order.getAppliedCouponCodes()).thenReturn(Collections.singleton(COUPON_CODE));
		final CouponResponse response = mock(CouponResponse.class);
		when(couponService.redeemCoupon(Matchers.anyString(), Matchers.any(OrderModel.class))).thenReturn(response);
		couponRedemptionMethodHook.afterPlaceOrder(parameter, commerceOrderResult);
		verify(couponService, times(1)).redeemCoupon(Matchers.anyString(), Matchers.any(OrderModel.class));
		assertThat(order.getAppliedCouponCodes()).hasSize(1);
	}

	@Test(expected = CouponServiceException.class)
	public void testCouponRedemptionAfterPlaceOrderFailure() throws InvalidCartException
	{
		when(commerceOrderResult.getOrder()).thenReturn(order);
		when(order.getAppliedCouponCodes()).thenReturn(Collections.singleton(COUPON_CODE));

		doThrow(CouponServiceException.class).when(couponService).redeemCoupon(Matchers.anyString(),
				Matchers.any(OrderModel.class));

		couponRedemptionMethodHook.afterPlaceOrder(parameter, commerceOrderResult);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testCouponRedemptionWhenOrderNull() throws InvalidCartException
	{
		couponRedemptionMethodHook.afterPlaceOrder(parameter, commerceOrderResult);
	}

	@Test
	public void testCouponRedemptionAfterPlaceOrderWithNoAppliedCoupons() throws InvalidCartException
	{
		when(commerceOrderResult.getOrder()).thenReturn(order);
		couponRedemptionMethodHook.afterPlaceOrder(parameter, commerceOrderResult);
		verify(couponService, times(0)).redeemCoupon(Matchers.anyString(), Matchers.any(OrderModel.class));
	}

}
