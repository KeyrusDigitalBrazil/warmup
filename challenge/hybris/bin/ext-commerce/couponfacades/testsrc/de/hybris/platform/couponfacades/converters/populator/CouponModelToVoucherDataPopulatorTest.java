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
import de.hybris.platform.couponservices.model.AbstractCouponModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Junit Test Suite for {@link CouponModelToVoucherDataPopulator }
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CouponModelToVoucherDataPopulatorTest
{
	private final static String COUPON_CODE = "couponCode";

	@InjectMocks
	private CouponModelToVoucherDataPopulator couponModelToVoucherDataPopulator;

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateSourceNull()
	{
		final VoucherData voucherData = new VoucherData();
		couponModelToVoucherDataPopulator.populate(null, voucherData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateTargetNull()
	{
		final AbstractCouponModel couponModel = new AbstractCouponModel();
		couponModelToVoucherDataPopulator.populate(couponModel, null);
	}

	@Test
	public void testPopulate()
	{
		final AbstractCouponModel couponModel = new AbstractCouponModel();
		couponModel.setCouponId(COUPON_CODE);

		final VoucherData voucherData = new VoucherData();
		couponModelToVoucherDataPopulator.populate(couponModel, voucherData);

		assertEquals(COUPON_CODE, voucherData.getCode());
	}
}
