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
package de.hybris.platform.promotionengineservices.promotionengine.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.promotionengineservices.promotionengine.PromotionActionService;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.ruleengineservices.action.RuleActionService;
import de.hybris.platform.ruleengineservices.rao.AbstractOrderRAO;
import de.hybris.platform.ruleengineservices.rao.DiscountRAO;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultIntegrationPromotionActionServiceTest extends ServicelayerTransactionalTest
{
	private static final String ORDER_CODE = "ahertzCart";

	@Resource
	private RuleActionService ruleActionService;

	@Resource
	private PromotionActionService promotionActionService;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/promotionengineservices/test/testRuleActionService.csv", "utf-8");
	}

	@Test
	public void testRemoveDiscountValue()
	{
		final RuleEngineResultRAO ruleEngineResult = new RuleEngineResultRAO();
		final DiscountRAO discountAction = new DiscountRAO();
		final AbstractOrderRAO orderRao = new AbstractOrderRAO();
		orderRao.setCode(ORDER_CODE);
		discountAction.setValue(BigDecimal.valueOf(10.0d));
		discountAction.setAppliedToObject(orderRao);
		discountAction.setActionStrategyKey("defaultRuleOrderPercentageDiscountRAOAction");
		ruleEngineResult.setActions(new LinkedHashSet<>(Collections.singletonList(discountAction)));
		final List<ItemModel> results = ruleActionService.applyAllActions(ruleEngineResult);
		assertEquals(1, results.size());
		assertTrue(results.get(0) instanceof PromotionResultModel);
		final PromotionResultModel promotionResultModel = (PromotionResultModel) results.get(0);
		assertEquals(1, promotionResultModel.getOrder().getGlobalDiscountValues().size());

		assertEquals(0, promotionActionService.removeDiscountValue("wrong_code", promotionResultModel.getOrder()).size());
		assertEquals(1,
				promotionActionService.removeDiscountValue(promotionResultModel.getOrder().getGlobalDiscountValues().get(0).getCode(),
						promotionResultModel.getOrder()).size());
	}

}
