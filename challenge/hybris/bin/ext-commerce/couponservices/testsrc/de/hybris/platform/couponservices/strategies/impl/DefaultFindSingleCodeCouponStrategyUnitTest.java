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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.couponservices.model.MultiCodeCouponModel;
import de.hybris.platform.couponservices.model.SingleCodeCouponModel;
import de.hybris.platform.couponservices.strategies.FindCouponStrategy;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultFindSingleCodeCouponStrategyUnitTest extends AbstractFindCouponStrategyUnitTest
{

	@InjectMocks
	private DefaultFindSingleCodeCouponStrategy strategy;

	@Mock
	private SingleCodeCouponModel coupon;

	@Mock
	private MultiCodeCouponModel wrongCoupon;

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
