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
package de.hybris.platform.couponservices;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.couponservices.rao.CouponRAO;
import de.hybris.platform.couponservices.util.CouponAwareCartTestContextBuilder;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.Collections;

import org.junit.Before;


@IntegrationTest
public class AbstractCouponAwareCartIT extends ServicelayerTest
{

	protected final static String COUPON_CODE = "testCouponCode";
	protected final static String COUPON_ID = "testCouponId";

	private CouponRAO expectedCouponRAO;
	private CouponAwareCartTestContextBuilder contextBuilder;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/couponservices/test/couponAwareTest.impex", "utf-8");

		contextBuilder = new CouponAwareCartTestContextBuilder();
		contextBuilder = (CouponAwareCartTestContextBuilder) contextBuilder.withCouponCodes(Collections.singleton(COUPON_CODE));

		expectedCouponRAO = new CouponRAO();
		expectedCouponRAO.setCouponCode(COUPON_CODE);
		expectedCouponRAO.setCouponId(COUPON_CODE);
	}

	protected CouponRAO getExpectedCouponRAO()
	{
		return expectedCouponRAO;
	}

	protected CouponAwareCartTestContextBuilder getCartTestContextBuilder()
	{
		return contextBuilder;
	}
}
