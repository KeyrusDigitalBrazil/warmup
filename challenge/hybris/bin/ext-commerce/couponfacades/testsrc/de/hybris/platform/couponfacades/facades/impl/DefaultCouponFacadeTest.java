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
package de.hybris.platform.couponfacades.facades.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.voucher.data.VoucherData;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.couponfacades.CouponFacadeIllegalStateException;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.couponservices.service.data.CouponResponse;
import de.hybris.platform.couponservices.services.CouponService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCouponFacadeTest
{
	public static final String COUPON_CODE = "testCouponCode";
	public static final String COUPON_ID = "testCouponId";

	@InjectMocks
	private DefaultCouponFacade couponFacade;
	@Mock
	private CouponService couponService;
	@Mock
	private CartService cartService;
	@Mock
	private AbstractPopulatingConverter<String, VoucherData> couponCodeConverter;
	@Mock
	private Converter<AbstractCouponModel, VoucherData> couponModelConverter;

	private CartModel cart;

	private VoucherData voucherData;

	private AbstractCouponModel couponModel;

	@Before
	public void setUp()
	{
		voucherData = new VoucherData();
		voucherData.setCode(COUPON_CODE);
		couponModel = new AbstractCouponModel();
		couponModel.setCouponId(COUPON_ID);

		couponFacade.setCouponModelConverter(couponModelConverter);
		couponFacade.setCouponCodeModelConverter(couponCodeConverter);

		when(couponCodeConverter.convert(COUPON_CODE)).thenReturn(voucherData);
		when(couponModelConverter.convert(couponModel)).thenReturn(voucherData);

		cart = mock(CartModel.class);
		when(cartService.getSessionCart()).thenReturn(cart);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testApplyVoucherNullArg() throws VoucherOperationException
	{
		couponFacade.applyVoucher(null);
	}

	@Test(expected = VoucherOperationException.class)
	public void testApplyVoucherNoCart() throws VoucherOperationException
	{
		when(cartService.getSessionCart()).thenReturn(null);
		couponFacade.applyVoucher(COUPON_CODE);
	}

	@Test
	public void testApplyVoucher() throws VoucherOperationException
	{
		final CouponResponse couponResponse = new CouponResponse();
		couponResponse.setSuccess(Boolean.TRUE);
		when(couponService.redeemCoupon(COUPON_CODE, cart)).thenReturn(couponResponse);
		couponFacade.applyVoucher(COUPON_CODE);
		verify(cartService).getSessionCart();
		verify(couponService).redeemCoupon(COUPON_CODE, cart);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testReleaseVoucherNullCode() throws VoucherOperationException
	{
		couponFacade.releaseVoucher(null);
	}

	@Test(expected = VoucherOperationException.class)
	public void testReleaseVoucherNoCart() throws VoucherOperationException
	{
		when(cartService.getSessionCart()).thenReturn(null);
		couponFacade.releaseVoucher(COUPON_CODE);
	}

	@Test
	public void testReleaseVoucher() throws VoucherOperationException
	{
		couponFacade.releaseVoucher(COUPON_CODE);
		verify(cartService).getSessionCart();
		verify(couponService).releaseCouponCode(COUPON_CODE, cart);
	}

	@Test(expected = CouponFacadeIllegalStateException.class)
	public void testGetVouchersForCartNoCart()
	{
		when(cartService.getSessionCart()).thenReturn(null);
		when(cart.getAppliedCouponCodes()).thenReturn(Collections.singleton(COUPON_CODE));

		couponFacade.getVouchersForCart();
	}

	@Test
	public void testGetVouchersForCartEmptyList()
	{
		when(cart.getAppliedCouponCodes()).thenReturn(Collections.emptySet());

		final List<VoucherData> couponsForOrder = couponFacade.getVouchersForCart();
		verify(cartService).getSessionCart();
		assertThat(couponsForOrder).isEmpty();
	}

	@Test
	public void testGetVouchersForCart()
	{
		when(cart.getAppliedCouponCodes()).thenReturn(Collections.singleton(COUPON_CODE));

		final List<VoucherData> couponsForOrder = couponFacade.getVouchersForCart();
		verify(cartService).getSessionCart();
		assertThat(couponsForOrder).isNotEmpty().hasSize(1).containsOnly(voucherData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCheckVoucherCodeNullArg()
	{
		couponFacade.checkVoucherCode(null);
	}

	@Test
	public void testCheckVoucherCode()
	{
		final CouponResponse couponResponse = new CouponResponse();
		couponResponse.setSuccess(Boolean.TRUE);
		when(couponService.verifyCouponCode(COUPON_CODE, cart)).thenReturn(couponResponse);

		final boolean validateCouponCode = couponFacade.checkVoucherCode(COUPON_CODE);
		verify(couponService).verifyCouponCode(COUPON_CODE, cart);
		assertThat(validateCouponCode).isTrue();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetCouponDetailsNullArg() throws VoucherOperationException
	{
		couponFacade.getVoucher(null);
	}

	@Test
	public void testGetCouponDetails() throws VoucherOperationException
	{
		when(couponService.getValidatedCouponForCode(Matchers.anyString())).thenReturn(Optional.of(couponModel));

		final VoucherData cd = couponFacade.getVoucher(COUPON_CODE);
		verify(couponService).getValidatedCouponForCode(COUPON_CODE);
		assertThat(cd).isEqualTo(voucherData);
	}

}
