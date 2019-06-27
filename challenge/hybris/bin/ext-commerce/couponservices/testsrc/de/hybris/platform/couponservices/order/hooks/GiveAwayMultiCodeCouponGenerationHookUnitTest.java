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
package de.hybris.platform.couponservices.order.hooks;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.couponservices.couponcodegeneration.CouponCodeGenerationException;
import de.hybris.platform.couponservices.dao.CouponDao;
import de.hybris.platform.couponservices.dao.RuleBasedCouponActionDao;
import de.hybris.platform.couponservices.model.MultiCodeCouponModel;
import de.hybris.platform.couponservices.model.RuleBasedAddCouponActionModel;
import de.hybris.platform.couponservices.services.CouponCodeGenerationService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;




/**
 * JUnit test suite for implementation {@link CouponRedemptionMethodHook}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GiveAwayMultiCodeCouponGenerationHookUnitTest
{

	@InjectMocks
	private GiveAwayMultiCodeCouponGenerationHook giveAwayMultiCodeCouponGenerationHook;

	@Mock
	private CouponDao couponDao;

	@Mock
	private RuleBasedCouponActionDao ruleBasedCouponActionDao;

	@Mock
	private CouponCodeGenerationService couponCodeGenerationService;

	@Mock
	private ModelService modelService;

	@Mock
	private CommerceOrderResult commerceOrderResult;

	@Mock
	private CommerceCheckoutParameter parameter;

	@Mock
	private OrderModel order;

	private MultiCodeCouponModel multiCodeCoupon;

	private RuleBasedAddCouponActionModel addCouponAction;

	private List<RuleBasedAddCouponActionModel> couponActionList;

	private static final String GIVE_AWAY_MULTICODE_COUPON_ID = "TESTMULTICODE16";

	private static final String GIVE_AWAY_MULTICODE_COUPON_CODE = "MULTI-TEST-1234-XYZ";

	@Before
	public void setUp()
	{
		multiCodeCoupon = new MultiCodeCouponModel();
		multiCodeCoupon.setCouponId(GIVE_AWAY_MULTICODE_COUPON_ID);

		couponActionList = new ArrayList<>();
		addCouponAction = new RuleBasedAddCouponActionModel();
		addCouponAction.setCouponId(GIVE_AWAY_MULTICODE_COUPON_ID);
		couponActionList.add(addCouponAction);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGiveAwayMultiCodeCouponGenerationHookWhenOrderNull() throws InvalidCartException
	{
		giveAwayMultiCodeCouponGenerationHook.beforeSubmitOrder(parameter, commerceOrderResult);
	}

	@Test
	public void testGiveAwayMultiCodeCouponGenerationBeforeSubmitOrder() throws InvalidCartException, CouponCodeGenerationException
	{

		when(commerceOrderResult.getOrder()).thenReturn(order);
		when(ruleBasedCouponActionDao.findRuleBasedCouponActionByOrder(Matchers.any(OrderModel.class)))
				.thenReturn(couponActionList);
		when(couponDao.findCouponById(Matchers.anyString())).thenReturn(multiCodeCoupon);
		when(couponCodeGenerationService.generateCouponCode(Matchers.any(MultiCodeCouponModel.class)))
				.thenReturn(GIVE_AWAY_MULTICODE_COUPON_CODE);

		doNothing().when(modelService).save(Matchers.any(RuleBasedAddCouponActionModel.class));

		giveAwayMultiCodeCouponGenerationHook.beforeSubmitOrder(parameter, commerceOrderResult);
		verify(modelService, times(1)).save(addCouponAction);
		verify(modelService, times(1)).save(multiCodeCoupon);
		assertEquals(GIVE_AWAY_MULTICODE_COUPON_CODE, addCouponAction.getCouponCode());
	}

	@Test
	public void testGiveAwayMultiCodeCouponGenerationException() throws InvalidCartException, CouponCodeGenerationException
	{
		when(commerceOrderResult.getOrder()).thenReturn(order);
		when(ruleBasedCouponActionDao.findRuleBasedCouponActionByOrder(Matchers.any(OrderModel.class)))
				.thenReturn(couponActionList);
		when(couponDao.findCouponById(Matchers.anyString())).thenReturn(multiCodeCoupon);

		doThrow(CouponCodeGenerationException.class).when(couponCodeGenerationService)
				.generateCouponCode(Matchers.any(MultiCodeCouponModel.class));
		doNothing().when(modelService).save(Matchers.any(RuleBasedAddCouponActionModel.class));

		giveAwayMultiCodeCouponGenerationHook.beforeSubmitOrder(parameter, commerceOrderResult);

		verify(modelService, times(1)).save(Matchers.any(RuleBasedAddCouponActionModel.class));
		assertEquals(StringUtils.EMPTY, addCouponAction.getCouponCode());

	}

	@Test
	public void testGiveAwayMultiCodeCouponGenerationWhenNoCouponFound() throws InvalidCartException
	{
		when(commerceOrderResult.getOrder()).thenReturn(order);
		when(ruleBasedCouponActionDao.findRuleBasedCouponActionByOrder(Matchers.any(OrderModel.class)))
				.thenReturn(couponActionList);
		doThrow(ModelNotFoundException.class).when(couponDao)
				.findCouponById(Matchers.anyString());

		doNothing().when(modelService).save(Matchers.any(RuleBasedAddCouponActionModel.class));

		giveAwayMultiCodeCouponGenerationHook.beforeSubmitOrder(parameter, commerceOrderResult);

		verify(modelService, times(1)).save(Matchers.any(RuleBasedAddCouponActionModel.class));
		assertEquals(StringUtils.EMPTY, addCouponAction.getCouponCode());
	}

	public void testGiveAwayMultiCodeCouponGenerationHookWhenActionHasNullCouponId()
			throws InvalidCartException, CouponCodeGenerationException
	{
		when(commerceOrderResult.getOrder()).thenReturn(order);
		final List<RuleBasedAddCouponActionModel> mockCouponActionList = new ArrayList<>();
		final RuleBasedAddCouponActionModel couponAction = mock(RuleBasedAddCouponActionModel.class);
		mockCouponActionList.add(couponAction);
		when(ruleBasedCouponActionDao.findRuleBasedCouponActionByOrder(Matchers.any(OrderModel.class)))
				.thenReturn(mockCouponActionList);

		giveAwayMultiCodeCouponGenerationHook.beforeSubmitOrder(parameter, commerceOrderResult);

		verify(modelService, times(0)).save(Matchers.any(RuleBasedAddCouponActionModel.class));
		verify(couponCodeGenerationService, times(0)).generateCouponCode(Matchers.any(MultiCodeCouponModel.class));
	}
}
