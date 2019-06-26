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
package de.hybris.platform.timedaccesspromotionengineservices.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.couponservices.redemption.strategies.CouponRedemptionStrategy;
import de.hybris.platform.couponservices.services.CouponManagementService;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengineservices.maintenance.RuleMaintenanceService;
import de.hybris.platform.timedaccesspromotionengineservices.daos.FlashBuyDao;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


/**
 * Unit test for {@link DefaultFlashBuyService}
 */
@UnitTest
public class DefaultFlashBuyServiceTest
{

	@Spy
	private DefaultFlashBuyService flashBuyService;
	private final static String moduleName = "module";
	private PromotionSourceRuleModel promotionSourceRule;


	@Mock
	private CouponManagementService couponManagementService;
	@Mock
	private RuleMaintenanceService ruleMaintenanceService;
	@Mock
	private FlashBuyDao flashBuyDao;
	@Mock
	private CouponRedemptionStrategy couponRedemptionStrategy;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		flashBuyService = Mockito.spy(new DefaultFlashBuyService());
		flashBuyService.setFlashBuyDao(flashBuyDao);
		promotionSourceRule = new PromotionSourceRuleModel();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetProductForPromotion_without_param()
	{
		flashBuyService.getProductForPromotion(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetFlashBuyCouponByPromotionCode_without_param()
	{
		flashBuyService.getFlashBuyCouponByPromotionCode(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPromotionSourceRulesByProductCode_without_param()
	{
		flashBuyService.getPromotionSourceRulesByProductCode(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUndeployFlashBuyPromotion_without_param()
	{
		flashBuyService.undeployFlashBuyPromotion(null);
	}

	@Test
	public void testUndeployFlashBuyPromotion()
	{
		final DroolsRuleModel droolsRuleModel = new DroolsRuleModel();
		final DroolsKIEBaseModel droolsKIEBaseModel = new DroolsKIEBaseModel();
		droolsRuleModel.setKieBase(droolsKIEBaseModel);
		Mockito.doReturn(moduleName).when(flashBuyService).getModuleName(Mockito.any());
		Mockito.doReturn(Optional.of(promotionSourceRule)).when(ruleMaintenanceService)
				.undeployRules(Arrays.asList(promotionSourceRule), moduleName);
		flashBuyService.setRuleMaintenanceService(ruleMaintenanceService);

		final DroolsRuleModel ruleEngine = new DroolsRuleModel();
		final Set ruleEngines = new HashSet();
		ruleEngines.add(ruleEngine);
		promotionSourceRule.setEngineRules(ruleEngines);

		flashBuyService.undeployFlashBuyPromotion(promotionSourceRule);

		Mockito.verify(ruleMaintenanceService, Mockito.times(1)).undeployRules(Arrays.asList(promotionSourceRule), moduleName);
	}

	@Test
	public void testUndeployFlashBuyPromotion_multi_ruleEninge()
	{
		final DroolsRuleModel droolsRuleModel = new DroolsRuleModel();
		final DroolsKIEBaseModel droolsKIEBaseModel = new DroolsKIEBaseModel();
		droolsRuleModel.setKieBase(droolsKIEBaseModel);
		Mockito.doReturn(moduleName).when(flashBuyService).getModuleName(Mockito.any());
		Mockito.doReturn(Optional.of(promotionSourceRule)).when(ruleMaintenanceService)
				.undeployRules(Arrays.asList(promotionSourceRule), moduleName);
		flashBuyService.setRuleMaintenanceService(ruleMaintenanceService);

		final Set ruleEngines = new HashSet();
		final DroolsRuleModel ruleEngine1 = new DroolsRuleModel();
		final DroolsRuleModel ruleEngine2 = new DroolsRuleModel();
		ruleEngines.add(ruleEngine1);
		ruleEngines.add(ruleEngine2);
		promotionSourceRule.setEngineRules(ruleEngines);

		flashBuyService.undeployFlashBuyPromotion(promotionSourceRule);

		Mockito.verify(ruleMaintenanceService, Mockito.times(2)).undeployRules(Arrays.asList(promotionSourceRule), moduleName);
	}


}