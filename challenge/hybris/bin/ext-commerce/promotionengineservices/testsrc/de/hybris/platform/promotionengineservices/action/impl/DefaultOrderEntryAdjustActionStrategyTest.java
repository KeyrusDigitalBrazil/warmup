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
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.promotionengineservices.model.RuleBasedOrderEntryAdjustActionModel;
import de.hybris.platform.promotionengineservices.promotionengine.PromotionActionService;
import de.hybris.platform.promotionengineservices.util.ActionUtils;
import de.hybris.platform.promotionengineservices.util.PromotionResultUtils;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.DiscountRAO;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultOrderEntryAdjustActionStrategyTest
{
	private static final BigDecimal DISCOUNT_AMOUNT = BigDecimal.valueOf(5);

	@InjectMocks
	private DefaultOrderEntryAdjustActionStrategy defaultOrderEntryAdjustActionStrategy;

	@Mock
	private PromotionActionService promotionActionService;

	@Mock
	private DiscountRAO discountRao;

	@Mock
	private AbstractOrderEntryModel orderEntry;

	@Mock
	private PromotionResultModel promotionResult;

	@Mock
	private RuleBasedOrderEntryAdjustActionModel ruleBasedOrderEntryAdjustAction;

	@Mock
	private CartModel cart;

	@Mock
	private ModelService modelService;

	@Mock
	private ProductModel product;

	@Mock
	private PromotionResultUtils promotionResultUtils;

	@Mock
	private ActionUtils actionUtils;

	private Class<RuleBasedOrderEntryAdjustActionModel> promotionAction;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		when(promotionActionService.getOrderEntry(discountRao)).thenReturn(orderEntry);
		when(promotionActionService.createPromotionResult(discountRao)).thenReturn(promotionResult);
		when(promotionResultUtils.getOrder(promotionResult)).thenReturn(cart);
		when(Boolean.valueOf(actionUtils.isActionUUID(anyString()))).thenReturn(Boolean.TRUE);
	}

	@Test
	public void testApplyNotDiscountRAO()
	{
		final List result = defaultOrderEntryAdjustActionStrategy.apply(new AbstractRuleActionRAO());
		assertTrue(result.isEmpty());
	}

	@Test
	public void testApplyOrderEntryNull()
	{
		when(promotionActionService.getOrderEntry(any(DiscountRAO.class))).thenReturn(null);
		final List result = defaultOrderEntryAdjustActionStrategy.apply(discountRao);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testApplyPromotionResultNull()
	{
		when(promotionActionService.createPromotionResult(discountRao)).thenReturn(null);
		final List result = defaultOrderEntryAdjustActionStrategy.apply(discountRao);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testApplyOrderNull()
	{
		when(orderEntry.getOrder()).thenReturn(null);
		final List result = defaultOrderEntryAdjustActionStrategy.apply(discountRao);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testApply()
	{
		when(orderEntry.getOrder()).thenReturn(cart);
		when(modelService.create(promotionAction)).thenReturn(ruleBasedOrderEntryAdjustAction);
		when(Boolean.valueOf(discountRao.isPerUnit())).thenReturn(Boolean.FALSE);

		final List result = defaultOrderEntryAdjustActionStrategy.apply(discountRao);
		assertFalse(result.isEmpty());
		assertEquals(1, result.size());
		assertEquals(promotionResult, result.get(0));
	}

	@Test
	public void testUndo()
	{
		final RuleBasedOrderEntryAdjustActionModel action = new RuleBasedOrderEntryAdjustActionModel();
		action.setPromotionResult(promotionResult);
		defaultOrderEntryAdjustActionStrategy.undo(action);
	}

	@Test
	public void testCreateOrderEntryAdjustAction()
	{
		when(modelService.create(promotionAction)).thenReturn(new RuleBasedOrderEntryAdjustActionModel());
		when(orderEntry.getEntryNumber()).thenReturn(Integer.valueOf(0));
		when(orderEntry.getProduct()).thenReturn(product);

		final RuleBasedOrderEntryAdjustActionModel action = defaultOrderEntryAdjustActionStrategy.createOrderEntryAdjustAction(
				promotionResult, discountRao, orderEntry, DISCOUNT_AMOUNT);

		assertEquals(promotionResult, action.getPromotionResult());
		assertEquals(DISCOUNT_AMOUNT, action.getAmount());
		assertEquals(orderEntry.getEntryNumber(), action.getOrderEntryNumber());
		assertEquals(orderEntry.getProduct(), action.getOrderEntryProduct());
		assertTrue(action.getMarkedApplied().booleanValue());
	}
}
