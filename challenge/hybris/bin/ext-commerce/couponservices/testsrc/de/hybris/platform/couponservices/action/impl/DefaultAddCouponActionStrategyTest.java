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
package de.hybris.platform.couponservices.action.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.couponservices.model.RuleBasedAddCouponActionModel;
import de.hybris.platform.promotionengineservices.model.RuleBasedOrderChangeDeliveryModeActionModel;
import de.hybris.platform.promotionengineservices.promotionengine.PromotionActionService;
import de.hybris.platform.promotionengineservices.util.ActionUtils;
import de.hybris.platform.promotionengineservices.util.PromotionResultUtils;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.ruleengineservices.rao.AbstractActionedRAO;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.AddCouponRAO;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Default unit-test for {@link DefaultAddCouponActionStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAddCouponActionStrategyTest
{
	@InjectMocks
	private DefaultAddCouponActionStrategy defaultAddCouponActionStrategy;

	@Mock
	private AddCouponRAO addCouponRAO;

	@Mock
	private CartRAO cartRao;

	@Mock
	private PromotionActionService promotionActionService;

	@Mock
	private PromotionResultModel promotionResult;

	@Mock
	private ModelService modelService;

	@Mock
	private CartModel cart;

	@Mock
	private PromotionResultUtils promotionResultUtils;

	@Mock
	private ActionUtils actionUtils;

	private Class<RuleBasedAddCouponActionModel> promotionAction;

	@Before
	public void setUp()
	{
		when(addCouponRAO.getAppliedToObject()).thenReturn(cartRao);
		when(promotionActionService.createPromotionResult(addCouponRAO)).thenReturn(promotionResult);
		when(promotionResult.getOrder()).thenReturn(cart);
		when(addCouponRAO.getCouponId()).thenReturn("testCouponId");
		when(modelService.create(promotionAction)).thenReturn(new RuleBasedAddCouponActionModel());
		when(promotionResultUtils.getOrder(promotionResult)).thenReturn(cart);
		when(Boolean.valueOf(actionUtils.isActionUUID(anyString()))).thenReturn(Boolean.TRUE);
	}

	@Test
	public void testApplyNotAddCouponRAO()
	{
		final List result = defaultAddCouponActionStrategy.apply(new AbstractRuleActionRAO());
		assertTrue(result.isEmpty());
	}

	@Test
	public void testApplyAppliedToObjectNotCartRAO()
	{
		when(addCouponRAO.getAppliedToObject()).thenReturn(new AbstractActionedRAO());

		final List result = defaultAddCouponActionStrategy.apply(addCouponRAO);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testApplyPromotionResultNull()
	{
		when(promotionActionService.createPromotionResult(addCouponRAO)).thenReturn(null);

		final List result = defaultAddCouponActionStrategy.apply(addCouponRAO);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testApplyOrderNull()
	{
		when(promotionResult.getOrder()).thenReturn(null);
		when(promotionResultUtils.getOrder(promotionResult)).thenReturn(null);
		final List result = defaultAddCouponActionStrategy.apply(addCouponRAO);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testApply()
	{
		final List result = defaultAddCouponActionStrategy.apply(addCouponRAO);

		assertFalse(result.isEmpty());
		assertEquals(1, result.size());
		assertEquals(promotionResult, result.get(0));
	}

	@Test
	public void testUndo()
	{
		final RuleBasedOrderChangeDeliveryModeActionModel action = new RuleBasedOrderChangeDeliveryModeActionModel();
		action.setPromotionResult(promotionResult);
		action.setReplacedDeliveryCost(BigDecimal.valueOf(0));
		defaultAddCouponActionStrategy.undo(action);
	}
}
