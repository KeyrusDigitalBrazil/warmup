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
package de.hybris.platform.customercouponfacades.strategies;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.customercouponfacades.strategies.impl.DefaultCustomerCouponRemovableStrategy;
import de.hybris.platform.customercouponservices.CustomerCouponService;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * 
 */
@UnitTest
public class DefaultCustomerCouponRemovableStrategyTest
{
	private DefaultCustomerCouponRemovableStrategy strategy;

	private static final String COUPON_CODE = "testcoupon";

	@Mock
	private CustomerCouponService customerCouponService;
	@Mock
	private AbstractCouponModel coupon;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		strategy = new DefaultCustomerCouponRemovableStrategy();
		strategy.setCustomerCouponService(customerCouponService);

	}

	@Test
	public void test_checkRemoveable()
	{
		Optional<AbstractCouponModel> op = Optional.empty();
		Mockito.when(customerCouponService.getCouponForCode(Mockito.anyString())).thenReturn(op);

		Assert.assertFalse(strategy.checkRemovable(COUPON_CODE));
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 10);
		Date endDate = cal.getTime();
		Mockito.when(coupon.getEndDate()).thenReturn(endDate);
		op = Optional.of(coupon);
		Mockito.when(customerCouponService.getCouponForCode(Mockito.anyString())).thenReturn(op);

		Assert.assertTrue(strategy.checkRemovable(COUPON_CODE));

	}

}
