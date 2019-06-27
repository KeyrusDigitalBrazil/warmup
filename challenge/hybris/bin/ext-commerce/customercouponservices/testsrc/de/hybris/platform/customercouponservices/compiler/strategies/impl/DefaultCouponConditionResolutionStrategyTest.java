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
package de.hybris.platform.customercouponservices.compiler.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.customercouponservices.daos.CustomerCouponDao;
import de.hybris.platform.customercouponservices.model.CustomerCouponForPromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.RuleBasedPromotionModel;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link DefaultCouponConditionResolutionStrategy}
 */
@UnitTest
public class DefaultCouponConditionResolutionStrategyTest
{

	private static final String PARAM_KEY = "coupons";
	private static final String COUPON_CODE = "test";
	private static final String MODULE_NAME = "module";

	private DefaultCouponConditionResolutionStrategy strategy;

	@Mock
	private ModelService modelService;
	@Mock
	private CustomerCouponDao customerCouponDao;

	@Mock
	private RuleConditionData condition;
	@Mock
	private PromotionSourceRuleModel rule;
	@Mock
	private RuleBasedPromotionModel promotion;
	@Mock
	private RuleParameterData parameter;
	@Mock
	private RuleCompilerContext context;
	@Mock
	private Map<String, RuleParameterData> parameters;

	private List<String> couponCodes;
	private CustomerCouponForPromotionSourceRuleModel cusCouponForRule;
	private List<CustomerCouponForPromotionSourceRuleModel> cusCouponForRules;


	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		strategy = new DefaultCouponConditionResolutionStrategy();
		strategy.setModelService(modelService);
		strategy.setCustomerCouponDao(customerCouponDao);

		couponCodes = Collections.singletonList(COUPON_CODE);
		cusCouponForRule = new CustomerCouponForPromotionSourceRuleModel();
		cusCouponForRules = Collections.emptyList();

		Mockito.when(condition.getParameters()).thenReturn(parameters);
		Mockito.when(parameters.get(PARAM_KEY)).thenReturn(parameter);
		Mockito.when(parameter.getValue()).thenReturn(couponCodes);
		Mockito.when(modelService.create(CustomerCouponForPromotionSourceRuleModel.class)).thenReturn(cusCouponForRule);
		Mockito.doNothing().when(modelService).save(cusCouponForRule);

		Mockito.when(context.getRule()).thenReturn(rule);
		Mockito.when(context.getModuleName()).thenReturn(MODULE_NAME);
		Mockito.when(customerCouponDao.findAllCusCouponForSourceRules(rule, MODULE_NAME)).thenReturn(cusCouponForRules);
		Mockito.doNothing().when(modelService).removeAll(cusCouponForRules);
	}

	@Test
	public void testGetAndStoreParameterValues()
	{
		strategy.getAndStoreParameterValues(condition, rule, promotion);
		Mockito.verify(modelService, Mockito.times(couponCodes.size())).save(cusCouponForRule);
	}

	@Test
	public void testCleanStoredParameterValues()
	{
		strategy.cleanStoredParameterValues(context);
		Mockito.verify(modelService, Mockito.times(1)).removeAll(cusCouponForRules);
	}

	@Test
	public void testGetAndStoreParameterValues_couponRuleParamsNull()
	{
		Mockito.when(condition.getParameters().get("coupons")).thenReturn(null);
		strategy.getAndStoreParameterValues(condition, rule, promotion);
		Mockito.verify(modelService, Mockito.times(0)).save(cusCouponForRule);
	}

	@Test
	public void testGetAndStoreParameterValues_couponCodesEmpty()
	{
		Mockito.when(parameter.getValue()).thenReturn(Collections.emptyList());
		strategy.getAndStoreParameterValues(condition, rule, promotion);
		Mockito.verify(modelService, Mockito.times(0)).save(cusCouponForRule);

	}
}
