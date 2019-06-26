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
package de.hybris.platform.couponfacades.converters.populator;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.voucher.data.VoucherData;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CouponCodeToVoucherDataPopulatorTest
{
	private final static String COUPON_CODE = "couponCode";

	@InjectMocks
	private CouponCodeToVoucherDataPopulator couponCodeToVoucherDataPopulator;

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateSourceNull()
	{
		final VoucherData voucherData = new VoucherData();
		couponCodeToVoucherDataPopulator.populate(null, voucherData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateTargetNull()
	{
		couponCodeToVoucherDataPopulator.populate(COUPON_CODE, null);
	}

	@Test
	public void testPopulate()
	{
		final VoucherData voucherData = new VoucherData();
		couponCodeToVoucherDataPopulator.populate(COUPON_CODE, voucherData);

		assertEquals(COUPON_CODE, voucherData.getCode());
	}
}
