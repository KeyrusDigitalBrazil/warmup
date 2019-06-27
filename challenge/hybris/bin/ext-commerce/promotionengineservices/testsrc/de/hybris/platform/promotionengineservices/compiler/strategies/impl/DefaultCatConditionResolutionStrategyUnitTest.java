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
import de.hybris.platform.promotionengineservices.model.CombinedCatsForRuleModel;
import de.hybris.platform.promotionengineservices.model.ExcludedProductForRuleModel;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.RuleBasedPromotionModel;
import de.hybris.platform.ruledefinitions.CollectionOperator;
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
public class DefaultCatConditionResolutionStrategyUnitTest
{
	private static final String CAT_PARAM_KEY = "categories";
	private static final String CONDITION_DEFINITION_ID = "y_qualifying_categories";
	private static final String CATEGORIES_OPERATOR_PARAM = "categories_operator";
	private static final String MODULE_NAME = "moduleName";

	@Spy
	@InjectMocks
	private DefaultCatConditionResolutionStrategy strategy;

	@Mock
	private ModelService modelService;

	@Mock
	private PromotionSourceRuleDao promotionSourceRuleDao;

	@Test
	public void testGetAndStoreParameterValue()
	{
		final RuleParameterData parameter = new RuleParameterData();
		parameter.setValue(Arrays.asList("111", "222"));

		final RuleParameterData catOperatorParam = new RuleParameterData();
		catOperatorParam.setValue(CollectionOperator.CONTAINS_ANY);

		final Map<String, RuleParameterData> parameters = new HashMap<String, RuleParameterData>();
		parameters.put(CAT_PARAM_KEY, parameter);
		parameters.put(CATEGORIES_OPERATOR_PARAM, catOperatorParam);

		final RuleConditionData condition = new RuleConditionData();
		condition.setDefinitionId(CONDITION_DEFINITION_ID);
		condition.setParameters(parameters);

		final PromotionSourceRuleModel rule = new PromotionSourceRuleModel();
		final RuleBasedPromotionModel promotion = new RuleBasedPromotionModel();

		when(modelService.create(CatForPromotionSourceRuleModel.class)).thenReturn(new CatForPromotionSourceRuleModel());
		doNothing().when(modelService).save(anyObject());

		strategy.getAndStoreParameterValues(condition, rule, promotion);

		verify(modelService, times(2)).create(eq(CatForPromotionSourceRuleModel.class));
		verify(modelService, times(2)).save(anyObject());
	}

	@Test
	public void testGetAndStoreParameterValueEmptyCategoryCodes()
	{
		final RuleParameterData parameter = new RuleParameterData();
		parameter.setValue(Collections.emptyList());

		final Map<String, RuleParameterData> parameters = new HashMap<String, RuleParameterData>();
		parameters.put(CAT_PARAM_KEY, parameter);

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
	public void shouldClearStoredParameterValuesForRuleAndModule()
	{
		//given
		final PromotionSourceRuleModel rule = new PromotionSourceRuleModel();
		final RuleCompilerContext context = new DefaultRuleCompilerContext(null,rule,MODULE_NAME,null);

		final CatForPromotionSourceRuleModel catForRule1 = new CatForPromotionSourceRuleModel();
		final CatForPromotionSourceRuleModel catForRule2 = new CatForPromotionSourceRuleModel();
		final List<CatForPromotionSourceRuleModel> catsForRule = Arrays.asList(catForRule1, catForRule2);
		when(promotionSourceRuleDao.findAllCatForPromotionSourceRule(rule,MODULE_NAME)).thenReturn(catsForRule);

		final CombinedCatsForRuleModel combinedCatForRule1 = new CombinedCatsForRuleModel();
		final CombinedCatsForRuleModel combinedCatForRule2 = new CombinedCatsForRuleModel();
		final List<CombinedCatsForRuleModel> combinedCatsForRule = Arrays.asList(combinedCatForRule1, combinedCatForRule2);
		when(promotionSourceRuleDao.findAllCombinedCatsForRule(rule,MODULE_NAME)).thenReturn(combinedCatsForRule);

		final ExcludedProductForRuleModel excludedProductForRule = new ExcludedProductForRuleModel();
		final List<ExcludedProductForRuleModel> excludedProductsForRule = Arrays.asList(excludedProductForRule);
		when(promotionSourceRuleDao.findAllExcludedProductForPromotionSourceRule(rule,MODULE_NAME)).thenReturn(excludedProductsForRule);

		doNothing().when(modelService).removeAll(anyList());
		//when
		strategy.cleanStoredParameterValues(context);
		//then
		verify(promotionSourceRuleDao, times(1)).findAllCatForPromotionSourceRule(rule,MODULE_NAME);
		verify(modelService, times(1)).removeAll(catsForRule);

		verify(promotionSourceRuleDao, times(1)).findAllCombinedCatsForRule(rule,MODULE_NAME);
		verify(modelService, times(1)).removeAll(combinedCatsForRule);

		verify(promotionSourceRuleDao, times(1)).findAllExcludedProductForPromotionSourceRule(rule,MODULE_NAME);
		verify(modelService, times(1)).removeAll(excludedProductsForRule);

		verify(promotionSourceRuleDao, times(1)).findAllExcludedCatForPromotionSourceRule(rule,MODULE_NAME);
	}
}
