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
package de.hybris.platform.promotionengineservices.compiler.strategies.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.promotionengineservices.dao.PromotionSourceRuleDao;
import de.hybris.platform.promotionengineservices.model.CatForPromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.ProductForPromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.RuleBasedPromotionModel;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.impl.DefaultRuleCompilerContext;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultProductConditionResolutionStrategyUnitTest
{
	private static final String PROD_PARAM_KEY = "products";
	private static final String CONDITION_DEFINITION_ID = "y_qualifying_products";
	private static final String MODULE_NAME = "module-name";

	@Spy
	@InjectMocks
	private DefaultProductConditionResolutionStrategy strategy;

	@Mock
	private ModelService modelService;

	@Mock
	private PromotionSourceRuleDao promotionSourceRuleDao;

	@Test
	public void testGetAndStoreParameterValue()
	{
		final RuleParameterData parameter = new RuleParameterData();
		parameter.setValue(Arrays.asList("111", "222"));

		final Map<String, RuleParameterData> parameters = new HashMap<String, RuleParameterData>();
		parameters.put(PROD_PARAM_KEY, parameter);

		final RuleConditionData condition = new RuleConditionData();
		condition.setDefinitionId(CONDITION_DEFINITION_ID);
		condition.setParameters(parameters);

		final PromotionSourceRuleModel rule = new PromotionSourceRuleModel();
		final RuleBasedPromotionModel promotion = new RuleBasedPromotionModel();

		when(modelService.create(ProductForPromotionSourceRuleModel.class)).thenReturn(new ProductForPromotionSourceRuleModel());
		doNothing().when(modelService).save(anyObject());

		strategy.getAndStoreParameterValues(condition, rule, promotion);

		verify(modelService, times(2)).create(eq(ProductForPromotionSourceRuleModel.class));
		verify(modelService, times(2)).save(anyObject());
	}

	@Test
	public void testGetAndStoreParameterValueEmptyProductCodes()
	{
		final RuleParameterData parameter = new RuleParameterData();
		parameter.setValue(Collections.emptyList());

		final Map<String, RuleParameterData> parameters = new HashMap<String, RuleParameterData>();
		parameters.put(PROD_PARAM_KEY, parameter);

		final RuleConditionData condition = new RuleConditionData();
		condition.setDefinitionId(CONDITION_DEFINITION_ID);
		condition.setParameters(parameters);

		final PromotionSourceRuleModel rule = new PromotionSourceRuleModel();
		final RuleBasedPromotionModel promotion = new RuleBasedPromotionModel();

		strategy.getAndStoreParameterValues(condition, rule, promotion);

		verify(modelService, times(0)).create(eq(CatForPromotionSourceRuleModel.class));
		verify(modelService, times(0)).save(anyObject());
	}
	
	@Test
	public void shouldCleanStoredParameterValuesForContext()
	{
		//given
		final PromotionSourceRuleModel rule = new PromotionSourceRuleModel();
		final RuleCompilerContext context = new DefaultRuleCompilerContext(null,rule,MODULE_NAME,null);
		final ProductForPromotionSourceRuleModel productForRule1 = new ProductForPromotionSourceRuleModel();
		final ProductForPromotionSourceRuleModel productForRule2 = new ProductForPromotionSourceRuleModel();
		final List<ProductForPromotionSourceRuleModel> productsForRule = Arrays.asList(productForRule1, productForRule2);
		given(promotionSourceRuleDao.findAllProductForPromotionSourceRule(rule,MODULE_NAME)).willReturn(productsForRule);
		doNothing().when(modelService).removeAll(anyList());
		//when
		strategy.cleanStoredParameterValues(context);
		//then
		verify(modelService, times(1)).removeAll(productsForRule);
	}
}
