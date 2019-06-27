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
package de.hybris.platform.couponservices.redemption.strategies.impl;

import static java.lang.Integer.valueOf;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.couponservices.dao.CouponRedemptionDao;
import de.hybris.platform.couponservices.model.CouponRedemptionModel;
import de.hybris.platform.couponservices.model.SingleCodeCouponModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;



/**
 * JUnit test suite for {@link DefaultSingleCodeCouponRedemptionStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SingleCodeCouponRedemptionStrategyUnitTest
{
	private static final String COUPON_ID = "SUMMER_2016";
	private static final String ANY_STRING = "ANY_STRING";

	@InjectMocks
	private DefaultSingleCodeCouponRedemptionStrategy singleCodeCouponRedemptionStrategy;

	@Mock
	private CouponRedemptionDao couponRedemptionDao;

	@Mock
	private AbstractOrderModel abstractOrder;
	@Mock
	private UserModel userModel;
	private SingleCodeCouponModel singleCodeCoupon;

	@Before
	public void setUp()
	{
		singleCodeCoupon = createSingleCodeCoupon();

		final List<CouponRedemptionModel> redemptionList = asList(mock(CouponRedemptionModel.class));

		when(couponRedemptionDao.findCouponRedemptionsByCodeAndUser(anyString(), any(UserModel.class))).thenReturn(redemptionList);
		when(couponRedemptionDao.findCouponRedemptionsByCode(anyString())).thenReturn(redemptionList);
		when(abstractOrder.getUser()).thenReturn(userModel);
	}

	@Test
	public void testSingleCodeCouponRedeemable()
	{
		final boolean isRedeemable = singleCodeCouponRedemptionStrategy.isRedeemable(singleCodeCoupon, abstractOrder, ANY_STRING);
		assertThat(isRedeemable).isTrue();
	}

	@Test
	public void testSingleCodeCouponRedeemableWithUser()
	{
		final boolean isRedeemable = singleCodeCouponRedemptionStrategy.isCouponRedeemable(singleCodeCoupon, userModel, ANY_STRING);
		assertThat(isRedeemable).isTrue();
	}

	@Test
	public void testSingleCodeCouponRedeemableWithUserNull()
	{
		final boolean isRedeemable = singleCodeCouponRedemptionStrategy.isCouponRedeemable(singleCodeCoupon, null, ANY_STRING);
		assertThat(isRedeemable).isTrue();
	}

	@Test
	public void testSingleCodeCouponRedeemableMaxRedemptionsPerCustomerIsNull()
	{
		singleCodeCoupon.setMaxRedemptionsPerCustomer(null);
		singleCodeCoupon.setMaxTotalRedemptions(valueOf(2));
		final boolean isRedeemable = singleCodeCouponRedemptionStrategy.isRedeemable(singleCodeCoupon, abstractOrder, ANY_STRING);
		assertTrue(isRedeemable);
	}

	@Test
	public void testSingleCodeCouponRedeemableMaxTotalRedemptionsIsReached()
	{
		final List<CouponRedemptionModel> redemptionList = asList(mock(CouponRedemptionModel.class),
				mock(CouponRedemptionModel.class));
		when(couponRedemptionDao.findCouponRedemptionsByCode(anyString())).thenReturn(redemptionList);

		singleCodeCoupon.setMaxRedemptionsPerCustomer(null);
		singleCodeCoupon.setMaxTotalRedemptions(valueOf(1));
		final boolean isRedeemable = singleCodeCouponRedemptionStrategy.isRedeemable(singleCodeCoupon, abstractOrder, ANY_STRING);
		assertFalse(isRedeemable);
	}


	@Test
	public void testSingleCodeCouponRedeemableBothMaxRedemptionsAreNull()
	{
		singleCodeCoupon.setMaxRedemptionsPerCustomer(null);
		singleCodeCoupon.setMaxTotalRedemptions(null);
		final boolean isRedeemable = singleCodeCouponRedemptionStrategy.isRedeemable(singleCodeCoupon, abstractOrder, ANY_STRING);
		assertTrue(isRedeemable);
	}

	@Test
	public void testSingleCodeCouponRedeemableMaxTotalRedemptionsIsNull()
	{
		singleCodeCoupon.setMaxRedemptionsPerCustomer(valueOf(2));
		singleCodeCoupon.setMaxTotalRedemptions(null);
		final boolean isRedeemable = singleCodeCouponRedemptionStrategy.isRedeemable(singleCodeCoupon, abstractOrder, ANY_STRING);
		assertTrue(isRedeemable);
	}

	private SingleCodeCouponModel createSingleCodeCoupon()
	{
		final SingleCodeCouponModel singleCodeCoupon = new SingleCodeCouponModel();
		singleCodeCoupon.setCouponId(COUPON_ID);
		singleCodeCoupon.setActive(Boolean.TRUE);
		final Date currentDate = Calendar.getInstance().getTime();
		singleCodeCoupon.setStartDate(currentDate);
		singleCodeCoupon.setEndDate(DateUtils.addYears(currentDate, 1));
		singleCodeCoupon.setMaxRedemptionsPerCustomer(Integer.valueOf(2));
		singleCodeCoupon.setMaxTotalRedemptions(Integer.valueOf(10));
		return singleCodeCoupon;
	}
}
