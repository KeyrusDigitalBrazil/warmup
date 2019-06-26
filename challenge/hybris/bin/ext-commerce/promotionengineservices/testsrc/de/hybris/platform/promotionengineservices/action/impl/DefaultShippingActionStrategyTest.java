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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.order.daos.DeliveryModeDao;
import de.hybris.platform.promotionengineservices.model.RuleBasedOrderChangeDeliveryModeActionModel;
import de.hybris.platform.promotionengineservices.promotionengine.PromotionActionService;
import de.hybris.platform.promotionengineservices.util.ActionUtils;
import de.hybris.platform.promotionengineservices.util.PromotionResultUtils;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.ruleengineservices.rao.AbstractActionedRAO;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.DeliveryModeRAO;
import de.hybris.platform.ruleengineservices.rao.ShipmentRAO;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.List;

import org.jaxen.util.SingletonList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultShippingActionStrategyTest
{
	private static final String DELIVERY_MODE_CODE = "deliveryMode";
	private static final BigDecimal DELIVERY_COST = BigDecimal.valueOf(5);

	@InjectMocks
	private DefaultShippingActionStrategy defaultShippingActionStrategy;

	@Mock
	private ShipmentRAO shipmentRAO;

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
	private DeliveryModeRAO deliveryModeRao;

	@Mock
	private DeliveryModeDao deliveryModeDao;

	@Mock
	private DeliveryModeModel deliveryMode;

	@Mock
	private PromotionResultUtils promotionResultUtils;

	@Mock
	private ActionUtils actionUtils;

	private Class<RuleBasedOrderChangeDeliveryModeActionModel> promotionAction;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		when(shipmentRAO.getAppliedToObject()).thenReturn(cartRao);
		when(promotionActionService.createPromotionResult(shipmentRAO)).thenReturn(promotionResult);
		when(promotionResult.getOrder()).thenReturn(cart);
		when(shipmentRAO.getMode()).thenReturn(deliveryModeRao);
		when(deliveryModeRao.getCode()).thenReturn(DELIVERY_MODE_CODE);
		when(deliveryModeDao.findDeliveryModesByCode(DELIVERY_MODE_CODE)).thenReturn(new SingletonList(deliveryMode));
		when(deliveryModeRao.getCost()).thenReturn(DELIVERY_COST);
		when(modelService.create(promotionAction)).thenReturn(new RuleBasedOrderChangeDeliveryModeActionModel());
		when(promotionResultUtils.getOrder(promotionResult)).thenReturn(cart);
		when(Boolean.valueOf(actionUtils.isActionUUID(anyString()))).thenReturn(Boolean.TRUE);
	}

	@Test
	public void testApplyNotShipmentRAO()
	{
		final List result = defaultShippingActionStrategy.apply(new AbstractRuleActionRAO());
		assertTrue(result.isEmpty());
	}

	@Test
	public void testApplyAppliedToObjectNotCartRAO()
	{
		when(shipmentRAO.getAppliedToObject()).thenReturn(new AbstractActionedRAO());

		final List result = defaultShippingActionStrategy.apply(shipmentRAO);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testApplyPromotionResultNull()
	{
		when(promotionActionService.createPromotionResult(shipmentRAO)).thenReturn(null);

		final List result = defaultShippingActionStrategy.apply(shipmentRAO);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testApplyOrderNull()
	{
		when(promotionResult.getOrder()).thenReturn(null);
		when(promotionResultUtils.getOrder(promotionResult)).thenReturn(null);
		final List result = defaultShippingActionStrategy.apply(shipmentRAO);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testApply()
	{
		final List result = defaultShippingActionStrategy.apply(shipmentRAO);

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
		defaultShippingActionStrategy.undo(action);
	}
}
