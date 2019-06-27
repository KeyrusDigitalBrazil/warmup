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
package de.hybris.platform.commerceservices.order.hook.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.voucher.VoucherService;
import de.hybris.platform.voucher.model.VoucherInvalidationModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultVoucherRedemptionPlaceOrderMethodHookTest
{

	private static final String VOUCHER_CODE_1 = "voucherCode1";
	private static final String VOUCHER_CODE_2 = "voucherCode2";

	@InjectMocks
	DefaultVoucherRedemptionPlaceOrderMethodHook defaultVoucherRedemptionPlaceOrderMethodHook = new DefaultVoucherRedemptionPlaceOrderMethodHook();

	@Mock
	private VoucherService voucherService;
	@Mock
	private CommerceCheckoutParameter commerceCheckoutParameter;
	@Mock
	private OrderModel orderModel;
	@Mock
	private CartModel cartModel;
	@Mock
	private VoucherInvalidationModel voucherInvalidationModel;
	@Mock
	private CommerceOrderResult commerceOrderResult;

	private Collection<String> voucherCodes = Arrays.asList(VOUCHER_CODE_1, VOUCHER_CODE_2);

	@Before()
	public void setup()
	{
		given(commerceCheckoutParameter.getCart()).willReturn(cartModel);
		given(commerceOrderResult.getOrder()).willReturn(orderModel);
	}

	@Test
	public void shouldBeforeSubmitOrderRedeemVouchersOnOrder() throws InvalidCartException
	{
		given(voucherService.getAppliedVoucherCodes(cartModel)).willReturn(voucherCodes);
		given(voucherService.redeemVoucher(anyString(), eq(orderModel))).willReturn(voucherInvalidationModel);

		defaultVoucherRedemptionPlaceOrderMethodHook.beforeSubmitOrder(commerceCheckoutParameter, commerceOrderResult);

		verify(voucherService).redeemVoucher(VOUCHER_CODE_1, orderModel);
		verify(voucherService).redeemVoucher(VOUCHER_CODE_2, orderModel);
	}

	@Test(expected = InvalidCartException.class)
	public void shouldBeforeSubmitOrderThrowExceptionForUnredeemableVoucher() throws InvalidCartException
	{
		given(voucherService.getAppliedVoucherCodes(cartModel)).willReturn(voucherCodes);
		given(voucherService.redeemVoucher(VOUCHER_CODE_1, orderModel)).willReturn(voucherInvalidationModel);
		given(voucherService.redeemVoucher(VOUCHER_CODE_2, orderModel)).willReturn(null);

		defaultVoucherRedemptionPlaceOrderMethodHook.beforeSubmitOrder(commerceCheckoutParameter, commerceOrderResult);
	}

	@Test
	public void shouldBeforeSubmitOrderHandleOrderWithNoVouchers() throws InvalidCartException
	{
		given(voucherService.getAppliedVoucherCodes(cartModel)).willReturn(Collections.<String> emptyList());
		given(voucherService.redeemVoucher(anyString(), eq(orderModel))).willReturn(voucherInvalidationModel);

		defaultVoucherRedemptionPlaceOrderMethodHook.beforeSubmitOrder(commerceCheckoutParameter, commerceOrderResult);

		verify(voucherService, never()).redeemVoucher(anyString(), any(OrderModel.class));
	}

	@Test
	public void shouldBeforeSubmitOrderHandleOrderWithNullVoucherList() throws InvalidCartException
	{
		given(voucherService.getAppliedVoucherCodes(cartModel)).willReturn(null);
		given(voucherService.redeemVoucher(anyString(), eq(orderModel))).willReturn(voucherInvalidationModel);

		defaultVoucherRedemptionPlaceOrderMethodHook.beforeSubmitOrder(commerceCheckoutParameter, commerceOrderResult);

		verify(voucherService, never()).redeemVoucher(anyString(), any(OrderModel.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldBeforeSubmitOrderValidateNullCommerceCheckoutParameter() throws InvalidCartException
	{
		defaultVoucherRedemptionPlaceOrderMethodHook.beforeSubmitOrder(null, commerceOrderResult);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldBeforeSubmitOrderValidateNullResult() throws InvalidCartException
	{
		defaultVoucherRedemptionPlaceOrderMethodHook.beforeSubmitOrder(commerceCheckoutParameter, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldBeforeSubmitOrderValidateNullCartModel() throws InvalidCartException
	{
		given(commerceCheckoutParameter.getCart()).willReturn(null);

		defaultVoucherRedemptionPlaceOrderMethodHook.beforeSubmitOrder(commerceCheckoutParameter, commerceOrderResult);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldBeforeSubmitOrderValidateNullOrderModel() throws InvalidCartException
	{
		given(commerceOrderResult.getOrder()).willReturn(null);

		defaultVoucherRedemptionPlaceOrderMethodHook.beforeSubmitOrder(commerceCheckoutParameter, commerceOrderResult);
	}
}
