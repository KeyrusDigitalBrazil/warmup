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
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.coupon.data.CouponData;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.couponservices.services.CouponService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Junit Test Suite for {@link DefaultCouponDataFacade}
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCouponDataFacadeTest
{
	public static final String COUPON_CODE = "testCouponCode";
	public static final String COUPON_ID = "testCouponId";

	@InjectMocks
	private DefaultCouponDataFacade couponDataFacade;
	@Mock
	private CouponService couponService;
	@Mock
	private Converter<AbstractCouponModel, CouponData> couponConverter;

	private AbstractCouponModel couponModel;

	private CouponData couponData;


	@Before
	public void setUp()
	{
		couponData = new CouponData();
		couponData.setCouponId(COUPON_ID);
		couponModel = new AbstractCouponModel();
		couponModel.setCouponId(COUPON_ID);

		couponDataFacade.setCouponConverter(couponConverter);

		when(couponConverter.convert(couponModel)).thenReturn(couponData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetCouponDetailsNullArg() throws VoucherOperationException
	{
		couponDataFacade.getCouponDetails(null);
	}

	@Test
	public void testGetCouponDetails() throws VoucherOperationException
	{
		when(couponService.getCouponForCode(Matchers.anyString())).thenReturn(Optional.of(couponModel));

		final Optional<CouponData> cd = couponDataFacade.getCouponDetails(COUPON_CODE);

		verify(couponService).getCouponForCode(COUPON_CODE);
		assertThat(cd.get()).isEqualTo(couponData);
		assertEquals(COUPON_ID, cd.get().getCouponId());
		assertEquals(COUPON_CODE, cd.get().getCouponCode());

	}

	@Test
	public void testGetCouponDetailsWhenCouponModelEmpty() throws VoucherOperationException
	{
		when(couponService.getCouponForCode(Matchers.anyString())).thenReturn(Optional.empty());

		final Optional<CouponData> cd = couponDataFacade.getCouponDetails(COUPON_CODE);

		verify(couponService).getCouponForCode(COUPON_CODE);

		assertThat(cd).isEqualTo(Optional.empty());
	}
}
