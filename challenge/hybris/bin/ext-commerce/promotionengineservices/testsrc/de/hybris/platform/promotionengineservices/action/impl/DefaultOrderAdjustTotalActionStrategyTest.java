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
package de.hybris.platform.promotionengineservices.action.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.promotionengineservices.model.RuleBasedOrderAdjustTotalActionModel;
import de.hybris.platform.promotionengineservices.promotionengine.PromotionActionService;
import de.hybris.platform.promotionengineservices.util.ActionUtils;
import de.hybris.platform.promotionengineservices.util.PromotionResultUtils;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.DiscountRAO;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultOrderAdjustTotalActionStrategyTest
{
	private static final BigDecimal DISCOUNT_VALUE = BigDecimal.valueOf(20);
	private static final String BEAN_NAME = "defaultOrderAdjustTotalActionStrategy";

	@InjectMocks
	private DefaultOrderAdjustTotalActionStrategy defaultOrderAdjustTotalActionStrategy;

	@Mock
	private PromotionActionService promotionActionService;

	@Mock
	private ModelService modelService;

	@Mock
	private PromotionResultModel promotionResult;

	@Mock
	private DiscountRAO discountRao;

	@Mock
	private CartModel cart;

	@Mock
	private RuleBasedOrderAdjustTotalActionModel ruleBasedOrderAdjustTotalAction;

	@Mock
	private AbstractRuleEngineRuleModel rule;

	@Mock
	private PromotionResultUtils promotionResultUtils;

	@Mock
	private ActionUtils actionUtils;

	private Class<RuleBasedOrderAdjustTotalActionModel> promotionAction;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		defaultOrderAdjustTotalActionStrategy.setPromotionAction(promotionAction);
		defaultOrderAdjustTotalActionStrategy.setBeanName(BEAN_NAME);
		when(Boolean.valueOf(modelService.isNew(any(Object.class)))).thenReturn(Boolean.FALSE);
		when(promotionResult.getOrder()).thenReturn(cart);
		when(promotionActionService.getRule(discountRao)).thenReturn(rule);
		when(promotionResultUtils.getOrder(promotionResult)).thenReturn(cart);
		when(Boolean.valueOf(actionUtils.isActionUUID(anyString()))).thenReturn(Boolean.TRUE);
	}

	@Test
	public void testApplyNotDiscountRAO()
	{
		final List result = defaultOrderAdjustTotalActionStrategy.apply(new AbstractRuleActionRAO());
		assertTrue(result.isEmpty());
	}

	@Test
	public void testApplyPromotionResultNull()
	{
		when(promotionActionService.createPromotionResult(any(DiscountRAO.class))).thenReturn(null);
		final List result = defaultOrderAdjustTotalActionStrategy.apply(new DiscountRAO());
		assertTrue(result.isEmpty());
	}

	@Test
	public void testApplyOrderNull()
	{
		when(promotionActionService.createPromotionResult(any(DiscountRAO.class))).thenReturn(promotionResult);
		when(promotionResult.getOrder()).thenReturn(null);
		when(promotionResultUtils.getOrder(promotionResult)).thenReturn(null);
		final List result = defaultOrderAdjustTotalActionStrategy.apply(discountRao);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testApply()
	{
		when(promotionActionService.createPromotionResult(discountRao)).thenReturn(promotionResult);
		when(modelService.create(promotionAction)).thenReturn(ruleBasedOrderAdjustTotalAction);
		doNothing().when(promotionActionService).createDiscountValue(any(DiscountRAO.class), Matchers.anyString(),
				any(AbstractOrderModel.class));

		final List result = defaultOrderAdjustTotalActionStrategy.apply(discountRao);

		assertFalse(result.isEmpty());
		assertEquals(1, result.size());
		assertEquals(promotionResult, result.get(0));
	}

	@Test
	public void testCreateOrderAdjustTotalAction()
	{
		when(modelService.create(promotionAction)).thenReturn(new RuleBasedOrderAdjustTotalActionModel());
		when(discountRao.getValue()).thenReturn(DISCOUNT_VALUE);

		final RuleBasedOrderAdjustTotalActionModel action = defaultOrderAdjustTotalActionStrategy
				.createOrderAdjustTotalAction(promotionResult, discountRao);

		assertEquals(promotionResult, action.getPromotionResult());
		assertEquals(rule, action.getRule());
		assertTrue(action.getMarkedApplied().booleanValue());
		assertEquals(BEAN_NAME, action.getStrategyId());
		assertEquals(DISCOUNT_VALUE, action.getAmount());
	}

	@Test
	public void testUndo()
	{
		final RuleBasedOrderAdjustTotalActionModel action = new RuleBasedOrderAdjustTotalActionModel();

		action.setPromotionResult(promotionResult);
		defaultOrderAdjustTotalActionStrategy.undo(action);
	}
}
