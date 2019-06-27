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
package de.hybris.platform.couponservices.cart.hooks;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.couponservices.CouponServiceException;
import de.hybris.platform.couponservices.service.data.CouponResponse;
import de.hybris.platform.couponservices.services.CouponService;
import de.hybris.platform.order.InvalidCartException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * JUnit test suite for implementation {@link CouponRedeemableValidationHook}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CouponRedeemableValidationHookUnitTest
{
	private static final String COUPON_CODE = "testCouponCode";

	@InjectMocks
	private CouponRedeemableValidationHook couponRedeemableValidationHook;

	@Mock
	private CouponService couponService;

	@Mock
	private CommerceOrderResult commerceOrderResult;

	@Mock
	private CommerceCartParameter parameter;

	@Mock
	private CartModel cart;

	@Test
	public void testCouponRedeemableAfterValidateCart()
	{
		final List<CommerceCartModification> modifications = new ArrayList<>();
		when(parameter.getCart()).thenReturn(cart);
		when(cart.getAppliedCouponCodes()).thenReturn(Collections.singleton(COUPON_CODE));
		final CouponResponse response = new CouponResponse();
		response.setCouponId(COUPON_CODE);
		response.setSuccess(Boolean.FALSE);
		when(couponService.verifyCouponCode(Matchers.anyString(), Matchers.any(CartModel.class))).thenReturn(response);
		doNothing().when(couponService).releaseCouponCode(Matchers.anyString(), Matchers.any(CartModel.class));
		couponRedeemableValidationHook.afterValidateCart(parameter, modifications);

		verify(couponService, times(1)).releaseCouponCode(Matchers.anyString(), Matchers.any(CartModel.class));
		assertThat(cart.getAppliedCouponCodes()).hasSize(1);
		assertThat(modifications).hasSize(1);

	}

	@Test(expected = CouponServiceException.class)
	public void testCouponRedeemableAfterValidateCartFailure() throws InvalidCartException
	{
		final List<CommerceCartModification> modifications = new ArrayList<>();
		when(parameter.getCart()).thenReturn(cart);
		when(cart.getAppliedCouponCodes()).thenReturn(Collections.singleton(COUPON_CODE));

		final CouponResponse response = new CouponResponse();
		response.setCouponId(COUPON_CODE);
		response.setSuccess(Boolean.FALSE);
		when(couponService.verifyCouponCode(Matchers.anyString(), Matchers.any(CartModel.class))).thenReturn(response);

		doThrow(CouponServiceException.class).when(couponService).releaseCouponCode(Matchers.anyString(),
				Matchers.any(CartModel.class));

		couponRedeemableValidationHook.afterValidateCart(parameter, modifications);
		assertThat(modifications).hasSize(0);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testCouponRedeemableWhenCartNull() throws InvalidCartException
	{
		final List<CommerceCartModification> modifications = new ArrayList<>();
		couponRedeemableValidationHook.afterValidateCart(parameter, modifications);
		assertThat(modifications).hasSize(0);
	}

	@Test
	public void testCouponRedeemableAfterValidateCartWithNoAppliedCoupons() throws InvalidCartException
	{
		when(parameter.getCart()).thenReturn(cart);
		final List<CommerceCartModification> modifications = new ArrayList<>();
		couponRedeemableValidationHook.afterValidateCart(parameter, modifications);
		verify(couponService, times(0)).releaseCouponCode(Matchers.anyString(), Matchers.any(CartModel.class));
		assertThat(modifications).hasSize(0);
	}
}
