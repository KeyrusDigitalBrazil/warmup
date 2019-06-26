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
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.promotionengineservices.model.RuleBasedPotentialPromotionMessageActionModel;
import de.hybris.platform.promotionengineservices.promotionengine.PromotionActionService;
import de.hybris.platform.promotionengineservices.util.ActionUtils;
import de.hybris.platform.promotionengineservices.util.PromotionResultUtils;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.DiscountRAO;
import de.hybris.platform.ruleengineservices.rao.DisplayMessageRAO;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultPotentialPromotionMessageActionStrategyTest
{
	@InjectMocks
	private DefaultPotentialPromotionMessageActionStrategy defaultPotentialPromotionMessageActionStrategy;

	@Mock
	private DisplayMessageRAO displayMessageRao;

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

	private Class<RuleBasedPotentialPromotionMessageActionModel> promotionAction;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		when(promotionActionService.createPromotionResult(any(DiscountRAO.class))).thenReturn(promotionResult);
		when(promotionResultUtils.getOrder(promotionResult)).thenReturn(cart);
		when(Boolean.valueOf(actionUtils.isActionUUID(anyString()))).thenReturn(Boolean.TRUE);
	}

	@Test
	public void testApplyNotDisplayMessageRAO()
	{
		final List result = defaultPotentialPromotionMessageActionStrategy.apply(new AbstractRuleActionRAO());
		assertTrue(result.isEmpty());
	}

	@Test
	public void testApplyPromotionResultNull()
	{
		when(promotionActionService.createPromotionResult(displayMessageRao)).thenReturn(null);
		final List result = defaultPotentialPromotionMessageActionStrategy.apply(displayMessageRao);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testApplyOrderNull()
	{
		when(promotionResult.getOrder()).thenReturn(null);
		when(promotionResultUtils.getOrder(promotionResult)).thenReturn(null);
		final List result = defaultPotentialPromotionMessageActionStrategy.apply(displayMessageRao);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testApply()
	{
		when(promotionResult.getOrder()).thenReturn(cart);
		when(modelService.create(promotionAction)).thenReturn(new RuleBasedPotentialPromotionMessageActionModel());

		final List result = defaultPotentialPromotionMessageActionStrategy.apply(displayMessageRao);

		assertFalse(result.isEmpty());
		assertEquals(1, result.size());
		assertEquals(promotionResult, result.get(0));
	}

	@Test
	public void shouldUpdateFiredMessageAfterActionAndPromotionResultIsPersisted()
	{
		//given
		given(promotionResult.getOrder()).willReturn(cart);
		when(modelService.create(promotionAction)).thenReturn(new RuleBasedPotentialPromotionMessageActionModel());
		final InOrder inOrder = inOrder(modelService, promotionActionService);
		//when
		defaultPotentialPromotionMessageActionStrategy.apply(displayMessageRao);
		//then
		inOrder.verify(modelService).saveAll(Mockito.eq(promotionResult), Mockito.anyObject());
		inOrder.verify(promotionActionService).recalculateFiredPromotionMessage(promotionResult);
	}

	@Test
	public void testUndo()
	{
		final RuleBasedPotentialPromotionMessageActionModel action = new RuleBasedPotentialPromotionMessageActionModel();

		action.setPromotionResult(promotionResult);
		defaultPotentialPromotionMessageActionStrategy.undo(action);
	}
}
