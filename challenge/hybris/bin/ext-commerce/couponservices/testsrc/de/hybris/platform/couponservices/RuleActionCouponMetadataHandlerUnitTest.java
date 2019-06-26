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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.couponservices.model.MultiCodeCouponModel;
import de.hybris.platform.couponservices.model.SingleCodeCouponModel;
import de.hybris.platform.couponservices.services.CouponService;
import de.hybris.platform.promotionengineservices.model.AbstractRuleBasedPromotionActionModel;
import de.hybris.platform.promotionengineservices.model.RuleBasedOrderAdjustTotalActionModel;
import de.hybris.platform.promotionengineservices.util.PromotionResultUtils;
import de.hybris.platform.promotions.model.PromotionResultModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleActionCouponMetadataHandlerUnitTest
{
	@InjectMocks
	private RuleActionCouponMetadataHandler ruleActionCouponMetadataHandler;

	@Mock
	private PromotionResultModel promotionResultModel;

	@Mock
	private AbstractOrderModel orderModel;

	@Mock
	private CouponService couponService;
	
	@Mock
	private PromotionResultUtils promotionResultUtils;

	private static final String SINGLE_COUPON1_CODE = "CPN1";
	private static final String SINGLE_COUPON2_CODE = "CPN2";
	private static final String MULTI_COUPON1_CODE = "CPN3-MULTI";
	private static final String MULTI_COUPON1_ID = "CPN3";
	private static final SingleCodeCouponModel singleCodeCpn1 = new SingleCodeCouponModel();
	private static final SingleCodeCouponModel singleCodeCpn2 = new SingleCodeCouponModel();
	private static final MultiCodeCouponModel multiCodeCpn1 = new MultiCodeCouponModel();
	static
	{
		singleCodeCpn1.setCouponId(SINGLE_COUPON1_CODE);
		singleCodeCpn2.setCouponId(SINGLE_COUPON2_CODE);
		multiCodeCpn1.setCouponId(MULTI_COUPON1_ID);
	}

	@Before
	public void setUp()
	{
		when(promotionResultModel.getOrder()).thenReturn(orderModel);
		when(orderModel.getAllPromotionResults()).thenReturn(Collections.singleton(promotionResultModel));
		final Optional<AbstractCouponModel> cpn1Optional = Optional.of(singleCodeCpn1);
		when(couponService.getCouponForCode(SINGLE_COUPON1_CODE)).thenReturn(cpn1Optional);
		final Optional<AbstractCouponModel> cpn2Optional = Optional.of(singleCodeCpn2);
		when(couponService.getCouponForCode(SINGLE_COUPON2_CODE)).thenReturn(cpn2Optional);
		final Optional<AbstractCouponModel> multiCpn1Optional = Optional.of(multiCodeCpn1);
		when(couponService.getCouponForCode(MULTI_COUPON1_CODE)).thenReturn(multiCpn1Optional);
		when(promotionResultUtils.getOrder(promotionResultModel)).thenReturn(orderModel);
	}

	@Test
	public void testHandleSingleCodeCoupon()
	{
		ruleActionCouponMetadataHandler = spy(ruleActionCouponMetadataHandler);
		final AbstractRuleBasedPromotionActionModel actionModel = new RuleBasedOrderAdjustTotalActionModel();
		actionModel.setPromotionResult(promotionResultModel);
		actionModel.setUsedCouponCodes(new ArrayList<String>());
		when(orderModel.getAppliedCouponCodes()).thenReturn(Collections.singleton(SINGLE_COUPON1_CODE));
		when(promotionResultModel.getActions()).thenReturn(Collections.singleton(actionModel));
		when(ruleActionCouponMetadataHandler.getMetadataId()).thenReturn("cpnHandler");
		ruleActionCouponMetadataHandler.handle(actionModel, SINGLE_COUPON1_CODE + ", " + SINGLE_COUPON2_CODE);
		assertEquals(1, actionModel.getUsedCouponCodes().size());
		assertTrue(actionModel.getUsedCouponCodes().contains(SINGLE_COUPON1_CODE));
		assertEquals(1, actionModel.getMetadataHandlers().size());
		assertTrue(actionModel.getMetadataHandlers().contains("cpnHandler"));
	}

	@Test
	public void testHandleMultiCodeCoupon()
	{
		ruleActionCouponMetadataHandler = spy(ruleActionCouponMetadataHandler);
		final AbstractRuleBasedPromotionActionModel actionModel = new RuleBasedOrderAdjustTotalActionModel();
		actionModel.setPromotionResult(promotionResultModel);
		actionModel.setUsedCouponCodes(new ArrayList<String>());
		when(orderModel.getAppliedCouponCodes()).thenReturn(Collections.singleton(MULTI_COUPON1_CODE));
		when(promotionResultModel.getActions()).thenReturn(Collections.singleton(actionModel));
		when(ruleActionCouponMetadataHandler.getMetadataId()).thenReturn("cpnHandler");
		ruleActionCouponMetadataHandler.handle(actionModel, MULTI_COUPON1_ID + ", CPN4");
		assertEquals(1, actionModel.getUsedCouponCodes().size());
		assertTrue(actionModel.getUsedCouponCodes().contains(MULTI_COUPON1_CODE));
		assertEquals(1, actionModel.getMetadataHandlers().size());
		assertTrue(actionModel.getMetadataHandlers().contains("cpnHandler"));
	}

	@Test
	public void testUndoHandle()
	{
		ruleActionCouponMetadataHandler = spy(ruleActionCouponMetadataHandler);
		final AbstractRuleBasedPromotionActionModel actionModel = new RuleBasedOrderAdjustTotalActionModel();
		actionModel.setPromotionResult(promotionResultModel);
		actionModel.setUsedCouponCodes(new ArrayList<String>());
		actionModel.setMetadataHandlers(Collections.singletonList("anotherCpnHandler"));
		when(orderModel.getAppliedCouponCodes()).thenReturn(Collections.singleton(SINGLE_COUPON1_CODE));
		when(promotionResultModel.getActions()).thenReturn(Collections.singleton(actionModel));
		when(ruleActionCouponMetadataHandler.getMetadataId()).thenReturn("cpnHandler");
		ruleActionCouponMetadataHandler.handle(actionModel, SINGLE_COUPON1_CODE + ", " + SINGLE_COUPON2_CODE);
		ruleActionCouponMetadataHandler.undoHandle(actionModel);
		assertEquals(1, actionModel.getMetadataHandlers().size());
		assertTrue(actionModel.getMetadataHandlers().contains("anotherCpnHandler"));
	}
}
