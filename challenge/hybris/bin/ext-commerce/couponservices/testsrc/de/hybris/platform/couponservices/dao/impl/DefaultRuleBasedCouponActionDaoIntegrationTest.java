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
package de.hybris.platform.couponservices.dao.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.couponservices.dao.RuleBasedCouponActionDao;
import de.hybris.platform.couponservices.model.RuleBasedAddCouponActionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.promotionengineservices.model.RuleBasedOrderAdjustTotalActionModel;
import de.hybris.platform.promotionengineservices.model.RuleBasedPromotionModel;
import de.hybris.platform.promotions.model.AbstractPromotionActionModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Integration Test Suite for {@link DefaultRuleBasedCouponActionDao }
 */
@IntegrationTest
public class DefaultRuleBasedCouponActionDaoIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private RuleBasedCouponActionDao ruleBasedCouponActionDao;
	@Resource
	private ModelService modelService;

	private OrderModel order;


	@Before
	public void setUp() throws ImpExException
	{
		order = modelService.create(OrderModel.class);
		order.setCode("ORD-TEST-001");
		order.setDate(new Date());
		final CurrencyModel currency = modelService.create(CurrencyModel.class);
		currency.setActive(Boolean.valueOf(true));
		currency.setSymbol("$");
		currency.setIsocode("USD");
		order.setCurrency(currency);
		final CustomerModel customer = modelService.create(CustomerModel.class);
		customer.setUid("TestCustomer");
		customer.setName("Test Customer");
		order.setUser(customer);

		final Set<PromotionResultModel> promotionResults = new HashSet<>();

		final PromotionResultModel pr1 = modelService.create(PromotionResultModel.class);
		final RuleBasedPromotionModel rulePromotion1 = modelService.create(RuleBasedPromotionModel.class);
		pr1.setPromotion(rulePromotion1);
		final Set<AbstractPromotionActionModel> promotionAction = new HashSet<>();
		final RuleBasedAddCouponActionModel couponAction = modelService.create(RuleBasedAddCouponActionModel.class);
		couponAction.setCouponCode("TESTPROMO16");
		couponAction.setCouponId("TESTPROMO16");
		promotionAction.add(couponAction);
		pr1.setAllPromotionActions(promotionAction);
		promotionResults.add(pr1);


		final PromotionResultModel pr2 = modelService.create(PromotionResultModel.class);
		final RuleBasedPromotionModel rulePromotion2 = modelService.create(RuleBasedPromotionModel.class);
		pr2.setPromotion(rulePromotion2);
		final Set<AbstractPromotionActionModel> promotionAction2 = new HashSet<>();
		final RuleBasedOrderAdjustTotalActionModel orderAdjustTotalAction = modelService
				.create(RuleBasedOrderAdjustTotalActionModel.class);
		orderAdjustTotalAction.setAmount(BigDecimal.TEN);
		promotionAction2.add(orderAdjustTotalAction);
		pr2.setAllPromotionActions(promotionAction2);
		promotionResults.add(pr2);
		order.setAllPromotionResults(promotionResults);
		modelService.save(order);
	}

	@Test
	public void testFindRuleBasedCouponActionByOrder()
	{
		final List<RuleBasedAddCouponActionModel> couponActionList = ruleBasedCouponActionDao
				.findRuleBasedCouponActionByOrder(order);

		assertEquals(1, couponActionList.size());
		assertEquals("TESTPROMO16", couponActionList.get(0).getCouponCode());
	}
}
