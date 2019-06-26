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
package de.hybris.platform.couponservices.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CouponVoucherServiceUnitTest
{
	private static final String COUPON_CODE1 = "couponCode1";
	private static final String COUPON_CODE2 = "couponCode2";

	@InjectMocks
	private CouponVoucherService couponVoucherService;

	@Test
	public void testGetAppliedVoucherCodesCart()
	{
		final CartModel cart = new CartModel();
		cart.setAppliedCouponCodes(Arrays.asList(COUPON_CODE1, COUPON_CODE2));

		final Collection<String> couponCodes = couponVoucherService.getAppliedVoucherCodes(cart);
		assertEquals(2, couponCodes.size());
		assertTrue(couponCodes.contains(COUPON_CODE1));
		assertTrue(couponCodes.contains(COUPON_CODE2));
	}

	@Test
	public void testGetAppliedVoucherCodesCartNoCoupons()
	{
		final CartModel cart = new CartModel();

		final Collection<String> couponCodes = couponVoucherService.getAppliedVoucherCodes(cart);
		assertTrue(CollectionUtils.isEmpty(couponCodes));
	}

	@Test
	public void testGetAppliedVoucherCodesOrder()
	{
		final OrderModel order = new OrderModel();
		order.setAppliedCouponCodes(Arrays.asList(COUPON_CODE1));

		final Collection<String> couponCodes = couponVoucherService.getAppliedVoucherCodes(order);
		assertEquals(1, couponCodes.size());
		assertTrue(couponCodes.contains(COUPON_CODE1));
	}

	@Test
	public void testGetAppliedVoucherCodesOrderNoCoupons()
	{
		final OrderModel order = new OrderModel();

		final Collection<String> couponCodes = couponVoucherService.getAppliedVoucherCodes(order);
		assertTrue(CollectionUtils.isEmpty(couponCodes));
	}
}
