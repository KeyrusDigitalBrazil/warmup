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
package de.hybris.platform.couponservices.strategies.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.couponservices.model.MultiCodeCouponModel;
import de.hybris.platform.couponservices.model.SingleCodeCouponModel;
import de.hybris.platform.couponservices.services.CouponCodeGenerationService;
import de.hybris.platform.couponservices.strategies.FindCouponStrategy;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultFindMultiCodeCouponStrategyUnitTest extends AbstractFindCouponStrategyUnitTest
{

	@InjectMocks
	private DefaultFindMultiCodeCouponStrategy strategy;

	@Mock
	private CouponCodeGenerationService couponCodeGenerationService;

	@Mock
	private MultiCodeCouponModel coupon;

	@Mock
	private SingleCodeCouponModel wrongCoupon;

	@Override
	@Before
	public void setup()
	{
		super.setup();
		when(couponCodeGenerationService.extractCouponPrefix(COUPON_ID)).thenReturn(COUPON_ID);
	}

	@Override
	FindCouponStrategy getStrategy()
	{
		return strategy;
	}

	@Override
	AbstractCouponModel getCoupon()
	{
		return coupon;
	}

	@Override
	AbstractCouponModel getWrongCouponType()
	{
		return wrongCoupon;
	}

}
