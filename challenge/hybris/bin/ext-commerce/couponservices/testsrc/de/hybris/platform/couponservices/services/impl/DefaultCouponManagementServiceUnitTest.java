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
package de.hybris.platform.couponservices.services.impl;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.couponservices.dao.CouponRedemptionDao;
import de.hybris.platform.couponservices.model.CouponRedemptionModel;
import de.hybris.platform.couponservices.model.MultiCodeCouponModel;
import de.hybris.platform.couponservices.model.SingleCodeCouponModel;
import de.hybris.platform.couponservices.redemption.strategies.CouponRedemptionStrategy;
import de.hybris.platform.couponservices.redemption.strategies.impl.DefaultMultiCodeCouponRedemptionStrategy;
import de.hybris.platform.couponservices.redemption.strategies.impl.DefaultSingleCodeCouponRedemptionStrategy;
import de.hybris.platform.couponservices.service.data.CouponResponse;
import de.hybris.platform.couponservices.strategies.FindCouponStrategy;
import de.hybris.platform.couponservices.strategies.impl.DefaultFindMultiCodeCouponStrategy;
import de.hybris.platform.couponservices.strategies.impl.DefaultFindSingleCodeCouponStrategy;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCouponManagementServiceUnitTest
{
	private static final String COUPON_ID = "SUMMER_2016";
	private final Map<String, CouponRedemptionStrategy> redemptionStrategyMap = new HashMap<>();

	@InjectMocks
	private DefaultCouponManagementService couponManagementService;

	@Mock
	private ModelService modelService;

	@Mock
	private DefaultFindSingleCodeCouponStrategy findSingleCodeCouponStrategy;

	@Mock
	private DefaultFindMultiCodeCouponStrategy findMultiCodeCouponStrategy;

	@Mock
	private DefaultSingleCodeCouponRedemptionStrategy singleCodeCouponRedemptionStrategy;

	@Mock
	private DefaultMultiCodeCouponRedemptionStrategy multiCodeCouponRedemptionStrategy;

	@Mock
	private AbstractOrderModel abstractOrder;

	@Mock
	private CouponRedemptionDao couponRedemptionDao;

	@Mock
	private UserModel userModel;

	@Before
	public void setUp()
	{
		populateCouponStrategiesList();
		populateRedemptionStrategyMap();

	}

	@Test
	public void testValidateSingleCodeCoupon()
	{
		final SingleCodeCouponModel singleCodeCoupon = createSingleCodeCoupon();
		when(abstractOrder.getUser()).thenReturn(userModel);
		when(findSingleCodeCouponStrategy.findValidatedCouponForCouponCode(COUPON_ID)).thenReturn(Optional.of(singleCodeCoupon));
		when(
				Boolean.valueOf(singleCodeCouponRedemptionStrategy.isCouponRedeemable(any(SingleCodeCouponModel.class),
						any(UserModel.class), anyString()))).thenReturn(Boolean.TRUE);
		final CouponResponse response = couponManagementService.verifyCouponCode(COUPON_ID, abstractOrder);
		assertTrue(response.getSuccess().booleanValue());
		assertEquals(COUPON_ID, response.getCouponId());

	}

	@Test
	public void testValidateSingleCodeCouponForUser()
	{
		final SingleCodeCouponModel singleCodeCoupon = createSingleCodeCoupon();
		when(findSingleCodeCouponStrategy.findValidatedCouponForCouponCode(COUPON_ID)).thenReturn(Optional.of(singleCodeCoupon));
		when(
				Boolean.valueOf(singleCodeCouponRedemptionStrategy.isCouponRedeemable(any(SingleCodeCouponModel.class),
						any(UserModel.class), anyString()))).thenReturn(Boolean.TRUE);
		final CouponResponse response = couponManagementService.validateCouponCode(COUPON_ID, userModel);
		assertTrue(response.getSuccess().booleanValue());
		assertEquals(COUPON_ID, response.getCouponId());
	}

	@Test
	public void testValidateSingleCodeCouponWithoutUser()
	{
		final SingleCodeCouponModel singleCodeCoupon = createSingleCodeCoupon();
		when(findSingleCodeCouponStrategy.findValidatedCouponForCouponCode(COUPON_ID)).thenReturn(Optional.of(singleCodeCoupon));
		when(
				Boolean.valueOf(singleCodeCouponRedemptionStrategy.isCouponRedeemable(any(SingleCodeCouponModel.class),
						any(UserModel.class), anyString()))).thenReturn(Boolean.TRUE);
		final CouponResponse response = couponManagementService.validateCouponCode(COUPON_ID, null);
		assertTrue(response.getSuccess().booleanValue());
		assertEquals(COUPON_ID, response.getCouponId());
	}

	@Test
	public void testNotValidSingleCodeCoupon()
	{
		final SingleCodeCouponModel singleCodeCoupon = createSingleCodeCoupon();
		when(abstractOrder.getUser()).thenReturn(userModel);
		when(findSingleCodeCouponStrategy.findValidatedCouponForCouponCode(COUPON_ID)).thenReturn(Optional.of(singleCodeCoupon));
		when(
				Boolean.valueOf(singleCodeCouponRedemptionStrategy.isCouponRedeemable(any(SingleCodeCouponModel.class),
						any(UserModel.class), anyString()))).thenReturn(Boolean.FALSE);

		final CouponResponse response = couponManagementService.verifyCouponCode(COUPON_ID, abstractOrder);
		assertFalse(response.getSuccess().booleanValue());

	}

	@Test
	public void testRedeemSingleCodeCouponForCart()
	{
		final CartModel cart = new CartModel();
		final SingleCodeCouponModel singleCodeCoupon = createSingleCodeCoupon();
		when(abstractOrder.getUser()).thenReturn(userModel);
		when(findSingleCodeCouponStrategy.findValidatedCouponForCouponCode(COUPON_ID)).thenReturn(Optional.of(singleCodeCoupon));
		when(
				Boolean.valueOf(singleCodeCouponRedemptionStrategy.isCouponRedeemable(any(SingleCodeCouponModel.class),
						any(UserModel.class), anyString()))).thenReturn(Boolean.TRUE);
		doNothing().when(modelService).saveAll(anyCollection());
		final boolean redeemFlag = couponManagementService.redeem(COUPON_ID, cart);
		assertTrue(redeemFlag);
	}

	@Test
	public void testRedeemSingleCodeCouponForOrder()
	{
		final CouponRedemptionModel couponRedemption = new CouponRedemptionModel();
		final OrderModel order = new OrderModel();
		final SingleCodeCouponModel singleCodeCoupon = createSingleCodeCoupon();
		when(abstractOrder.getUser()).thenReturn(userModel);
		when(
				Boolean.valueOf(singleCodeCouponRedemptionStrategy.isCouponRedeemable(any(SingleCodeCouponModel.class),
						any(UserModel.class), anyString()))).thenReturn(Boolean.TRUE);
		when(findSingleCodeCouponStrategy.findValidatedCouponForCouponCode(COUPON_ID)).thenReturn(Optional.of(singleCodeCoupon));

		when(modelService.create(CouponRedemptionModel.class)).thenReturn(couponRedemption);
		doNothing().when(modelService).save(couponRedemption);
		final CouponResponse response = couponManagementService.redeem(COUPON_ID, order);
		assertTrue(response.getSuccess().booleanValue());
	}

	private SingleCodeCouponModel createSingleCodeCoupon()
	{
		final SingleCodeCouponModel singleCodeCoupon = new SingleCodeCouponModel();
		singleCodeCoupon.setCouponId(COUPON_ID);
		singleCodeCoupon.setActive(Boolean.TRUE);
		final Date currentDate = Calendar.getInstance().getTime();
		singleCodeCoupon.setStartDate(currentDate);
		singleCodeCoupon.setEndDate(DateUtils.addYears(currentDate, 1));
		return singleCodeCoupon;
	}

	private void populateRedemptionStrategyMap()
	{
		redemptionStrategyMap.put(SingleCodeCouponModel._TYPECODE, singleCodeCouponRedemptionStrategy);
		redemptionStrategyMap.put(MultiCodeCouponModel._TYPECODE, multiCodeCouponRedemptionStrategy);
		couponManagementService.setRedemptionStrategyMap(redemptionStrategyMap);
	}

	private void populateCouponStrategiesList()
	{
		final List<FindCouponStrategy> findCouponStrategiesList = asList(findSingleCodeCouponStrategy, findMultiCodeCouponStrategy);
		couponManagementService.setFindCouponStrategiesList(findCouponStrategiesList);
	}


}
