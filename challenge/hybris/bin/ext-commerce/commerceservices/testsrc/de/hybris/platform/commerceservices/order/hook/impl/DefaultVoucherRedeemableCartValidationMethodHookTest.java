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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.voucher.VoucherModelService;
import de.hybris.platform.voucher.VoucherService;
import de.hybris.platform.voucher.model.VoucherModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultVoucherRedeemableCartValidationMethodHookTest
{
	private static final String TEST_VOUCHER_CODE = "testVoucherCode";

	@InjectMocks
	DefaultVoucherRedeemableCartValidationMethodHook defaultVoucherRedeemableCartValidationMethodHook = new DefaultVoucherRedeemableCartValidationMethodHook();

	@Mock
	private VoucherService voucherService;
	@Mock
	private VoucherModelService voucherModelService;
	@Mock
	private CommerceCartParameter parameter;
	@Mock
	private CartModel cartModel;
	@Mock
	private VoucherModel testVoucherModel;
	@Mock
	private UserModel userModel;

	@Before()
	public void setup()
	{
		given(parameter.getCart()).willReturn(cartModel);
		given(cartModel.getUser()).willReturn(userModel);
	}

	@Test
	public void shouldAfterValidateCartCatchNonReservableVoucher() throws JaloPriceFactoryException
	{
		final List<CommerceCartModification> modifications = new ArrayList<>();

		given(voucherService.getAppliedVoucherCodes(cartModel)).willReturn(Collections.singleton(TEST_VOUCHER_CODE));
		given(Boolean.valueOf(voucherModelService.isApplicable(any(VoucherModel.class), any(CartModel.class))))
				.willReturn(Boolean.TRUE);
		given(Boolean.valueOf(voucherModelService.isReservable(any(VoucherModel.class), anyString(), any(UserModel.class))))
				.willReturn(Boolean.FALSE);
		given(voucherService.getVoucher(TEST_VOUCHER_CODE)).willReturn(testVoucherModel);

		defaultVoucherRedeemableCartValidationMethodHook.afterValidateCart(parameter, modifications);

		verify(voucherService).releaseVoucher(TEST_VOUCHER_CODE, cartModel);
		Assert.assertEquals("Cart modification should be created for non-reservable voucher", 1, modifications.size());
	}

	@Test
	public void shouldAfterValidateCartCatchNonApplicableVoucher() throws JaloPriceFactoryException
	{
		final List<CommerceCartModification> modifications = new ArrayList<>();

		given(voucherService.getAppliedVoucherCodes(cartModel)).willReturn(Collections.singleton(TEST_VOUCHER_CODE));
		given(Boolean.valueOf(voucherModelService.isApplicable(any(VoucherModel.class), any(CartModel.class))))
				.willReturn(Boolean.FALSE);
		given(voucherService.getVoucher(TEST_VOUCHER_CODE)).willReturn(testVoucherModel);

		defaultVoucherRedeemableCartValidationMethodHook.afterValidateCart(parameter, modifications);

		verify(voucherModelService).isApplicable(testVoucherModel, cartModel);
		verify(voucherService).releaseVoucher(TEST_VOUCHER_CODE, cartModel);
		Assert.assertEquals("Cart modification should be created for non-applicable voucher", 1, modifications.size());
	}

	@Test
	public void shouldAfterValidateCartHandleValidVoucher() throws JaloPriceFactoryException
	{
		final List<CommerceCartModification> modifications = new ArrayList<>();

		given(voucherService.getAppliedVoucherCodes(cartModel)).willReturn(Collections.singleton(TEST_VOUCHER_CODE));
		given(Boolean.valueOf(voucherModelService.isReservable(any(VoucherModel.class), anyString(), any(UserModel.class))))
				.willReturn(Boolean.TRUE);
		given(Boolean.valueOf(voucherModelService.isApplicable(any(VoucherModel.class), any(CartModel.class))))
				.willReturn(Boolean.TRUE);
		given(voucherService.getVoucher(TEST_VOUCHER_CODE)).willReturn(testVoucherModel);

		defaultVoucherRedeemableCartValidationMethodHook.afterValidateCart(parameter, modifications);

		verify(voucherService, never()).releaseVoucher(TEST_VOUCHER_CODE, cartModel);
		Assert.assertEquals("Cart modification should not be created for valid voucher", 0, modifications.size());
	}

	@Test
	public void shouldAfterValidateCartFailureHandleVouchersRedemption() throws JaloPriceFactoryException
	{
		final List<CommerceCartModification> modifications = new ArrayList<>();

		given(voucherService.getAppliedVoucherCodes(cartModel)).willReturn(Collections.singleton(TEST_VOUCHER_CODE));
		given(Boolean.valueOf(voucherModelService.isApplicable(any(VoucherModel.class), any(CartModel.class))))
				.willReturn(Boolean.FALSE);
		doThrow(JaloPriceFactoryException.class).when(voucherService).releaseVoucher(anyString(), any(CartModel.class));

		try
		{
			defaultVoucherRedeemableCartValidationMethodHook.afterValidateCart(parameter, modifications);
		}
		catch (final Throwable e)
		{
			Assert.assertTrue(e instanceof JaloSystemException);
			Assert.assertEquals("Cart modification should not be created after cart failure", 0, modifications.size());
		}
	}

	@Test
	public void shouldAfterValidateCartHandleCartWithNoAppliedVouchers() throws JaloPriceFactoryException
	{
		final List<CommerceCartModification> modifications = new ArrayList<>();

		given(voucherService.getAppliedVoucherCodes(cartModel)).willReturn(Collections.<String> emptyList());

		defaultVoucherRedeemableCartValidationMethodHook.afterValidateCart(parameter, modifications);

		verify(voucherService, never()).releaseVoucher(anyString(), any(CartModel.class));
		Assert.assertEquals("Cart modification should not be created for no applied voucher", 0, modifications.size());
	}

	@Test
	public void shouldAfterValidateCartHandleCartWithNullVoucherList() throws JaloPriceFactoryException
	{
		final List<CommerceCartModification> modifications = new ArrayList<>();

		given(voucherService.getAppliedVoucherCodes(cartModel)).willReturn(null);

		defaultVoucherRedeemableCartValidationMethodHook.afterValidateCart(parameter, modifications);

		verify(voucherService, never()).releaseVoucher(anyString(), any(CartModel.class));
		Assert.assertEquals("Cart modification should not be created for null voucher", 0, modifications.size());
	}
}
