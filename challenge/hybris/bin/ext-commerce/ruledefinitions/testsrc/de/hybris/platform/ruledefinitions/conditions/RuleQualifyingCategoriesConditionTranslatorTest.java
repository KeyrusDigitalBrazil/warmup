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
package de.hybris.platform.ruledefinitions.conditions;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruledefinitions.AmountOperator;
import de.hybris.platform.ruledefinitions.CollectionOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeRelCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrExistsCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrLocalVariablesContainer;
import de.hybris.platform.ruleengineservices.compiler.RuleIrNotCondition;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.CategoryRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rao.ProductRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleQualifyingCategoriesConditionTranslatorTest
{
	private static final String ORDER_ENTRY_RAO_VAR = "orderEntryRaoVariable";
	private static final String CART_RAO_VAR = "cartRaoVariable";
	private static final String PRODUCT_RAO_VAR = "productRaoVariable";
	private static final String CATEGORY_RAO_VAR = "categoryRaoVariable";
	private static final String AVAILABLE_QUANTITY_VAR = "availableQuantity";

	public static final String CART_RAO_ENTRIES_ATTRIBUTE = "entries";
	public static final String ORDER_ENTRY_RAO_PRODUCT_ATTRIBUTE = "product";
	public static final String PRODUCT_RAO_CATEGORIES_ATTRIBUTE = "categories";
	public static final String CATEGORY_RAO_CODE_ATTRIBUTE = "code";

	@InjectMocks
	private RuleQualifyingCategoriesConditionTranslator translator;

	@Mock
	private RuleCompilerContext context;
	@Mock
	private RuleConditionData condition;
	@Mock
	private RuleConditionDefinitionData conditionDefinition;
	@Mock
	private Map<String, RuleParameterData> parameters;
	@Mock
	private RuleIrLocalVariablesContainer variablesContainer;

	@Mock
	private RuleParameterData operatorParameter, quantityParameter, categoriesOperatorParameter, categoriesParameter,
			excludedCategoriesParameter;

	@Before
	public void setUp()
	{
		when(condition.getParameters()).thenReturn(parameters);
		when(parameters.get(RuleQualifyingCategoriesConditionTranslator.OPERATOR_PARAM)).thenReturn(operatorParameter);
		when(parameters.get(RuleQualifyingCategoriesConditionTranslator.QUANTITY_PARAM)).thenReturn(quantityParameter);
		when(parameters.get(RuleQualifyingCategoriesConditionTranslator.CATEGORIES_OPERATOR_PARAM))
				.thenReturn(categoriesOperatorParameter);
		when(parameters.get(RuleQualifyingCategoriesConditionTranslator.CATEGORIES_PARAM)).thenReturn(categoriesParameter);
		when(operatorParameter.getValue()).thenReturn(AmountOperator.GREATER_THAN);
		when(quantityParameter.getValue()).thenReturn(new Integer(1));
		when(context.createLocalContainer()).thenReturn(variablesContainer);
		when(context.generateLocalVariable(variablesContainer, CategoryRAO.class)).thenReturn(CATEGORY_RAO_VAR);

		final List<String> categoryList = new ArrayList<>();
		categoryList.add("categoryCode1");
		categoryList.add("categoryCode2");
		when(categoriesParameter.getValue()).thenReturn(categoryList);

		when(context.generateVariable(OrderEntryRAO.class)).thenReturn(ORDER_ENTRY_RAO_VAR);
		when(context.generateVariable(CartRAO.class)).thenReturn(CART_RAO_VAR);
		when(context.generateVariable(ProductRAO.class)).thenReturn(PRODUCT_RAO_VAR);
		when(context.generateVariable(CategoryRAO.class)).thenReturn(CATEGORY_RAO_VAR);
	}

	@Test
	public void testTranslateOperatorParamNull()
	{
		when(parameters.get(RuleQualifyingCategoriesConditionTranslator.OPERATOR_PARAM)).thenReturn(null);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);
		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void testTranslateCategoriesOperatorParamNull()
	{
		when(parameters.get(RuleQualifyingCategoriesConditionTranslator.CATEGORIES_OPERATOR_PARAM)).thenReturn(null);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);
		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void testTranslateCategoriesParamNull()
	{
		when(parameters.get(RuleQualifyingCategoriesConditionTranslator.CATEGORIES_PARAM)).thenReturn(null);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);
		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void testTranslateNotOperatorCondition()
	{
		when(categoriesOperatorParameter.getValue()).thenReturn(CollectionOperator.NOT_CONTAINS);

		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);
		assertThat(ruleIrCondition, instanceOf(RuleIrGroupCondition.class));

		final RuleIrGroupCondition irGroupCondition = (RuleIrGroupCondition) ruleIrCondition;
		final List<RuleIrCondition> childCondition = irGroupCondition.getChildren();
		assertThat(childCondition.get(0), instanceOf(RuleIrNotCondition.class));
		final RuleIrNotCondition irNotCondition = (RuleIrNotCondition) childCondition.get(0);
		assertEquals(7, irNotCondition.getChildren().size());
		checkChildConditions(irNotCondition.getChildren());
	}

	@Test
	public void testTranslateAnyOperatorCondition()
	{
		when(categoriesOperatorParameter.getValue()).thenReturn(CollectionOperator.CONTAINS_ANY);

		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);
		assertThat(ruleIrCondition, instanceOf(RuleIrGroupCondition.class));
		final RuleIrGroupCondition irGroupCondition = (RuleIrGroupCondition) ruleIrCondition;
		assertEquals(7, irGroupCondition.getChildren().size());
		checkChildConditions(irGroupCondition.getChildren());
	}

	@Test
	public void testTranslateAnyOperatorWithExcludeCategoryCondition()
	{
		when(categoriesOperatorParameter.getValue()).thenReturn(CollectionOperator.CONTAINS_ANY);
		when(parameters.get(RuleQualifyingCategoriesConditionTranslator.EXCLUDED_CATEGORIES_PARAM))
				.thenReturn(excludedCategoriesParameter);
		final List<String> excludedCategories = new ArrayList<>();
		excludedCategories.add("excludeSubCat1");
		when(excludedCategoriesParameter.getValue()).thenReturn(excludedCategories);

		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);
		assertThat(ruleIrCondition, instanceOf(RuleIrGroupCondition.class));
		final RuleIrGroupCondition irGroupCondition = (RuleIrGroupCondition) ruleIrCondition;
		assertEquals(8, irGroupCondition.getChildren().size());
		checkChildConditions(irGroupCondition.getChildren());
		checkExcludeCategoryCondition(irGroupCondition.getChildren().get(7));
	}


	private void checkExcludeCategoryCondition(final RuleIrCondition ruleIrCondition)
	{
		assertThat(ruleIrCondition, instanceOf(RuleIrNotCondition.class));
		final RuleIrNotCondition excludeCondition = (RuleIrNotCondition) ruleIrCondition;
		assertEquals(2, excludeCondition.getChildren().size());

		final RuleIrAttributeCondition attributeCondition = (RuleIrAttributeCondition) excludeCondition.getChildren().get(0);
		assertEquals(RuleIrAttributeOperator.IN, attributeCondition.getOperator());
		assertEquals(CATEGORY_RAO_VAR, attributeCondition.getVariable());
		assertEquals(CATEGORY_RAO_CODE_ATTRIBUTE, attributeCondition.getAttribute());
		final List<String> excludedCategories = (List<String>) attributeCondition.getValue();
		assertTrue(excludedCategories.contains("excludeSubCat1"));

		final RuleIrAttributeRelCondition attributeRelCondition = (RuleIrAttributeRelCondition) excludeCondition.getChildren()
				.get(1);
		assertEquals(RuleIrAttributeOperator.CONTAINS, attributeRelCondition.getOperator());
		assertEquals(PRODUCT_RAO_VAR, attributeRelCondition.getVariable());
		assertEquals(PRODUCT_RAO_CATEGORIES_ATTRIBUTE, attributeRelCondition.getAttribute());
		assertEquals(CATEGORY_RAO_VAR, attributeRelCondition.getTargetVariable());

	}

	@Test
	public void testTranslateAllOperatorCondition()
	{
		when(categoriesOperatorParameter.getValue()).thenReturn(CollectionOperator.CONTAINS_ALL);

		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);
		assertThat(ruleIrCondition, instanceOf(RuleIrGroupCondition.class));
		final RuleIrGroupCondition irGroupCondition = (RuleIrGroupCondition) ruleIrCondition;
		assertEquals(9, irGroupCondition.getChildren().size());
		checkChildConditions(irGroupCondition.getChildren());
		assertThat(irGroupCondition.getChildren().get(7), instanceOf(RuleIrExistsCondition.class));
		assertThat(irGroupCondition.getChildren().get(8), instanceOf(RuleIrExistsCondition.class));
	}

	private void checkChildConditions(final List<RuleIrCondition> ruleIrConditions)
	{
		assertThat(ruleIrConditions.get(0), instanceOf(RuleIrAttributeCondition.class));
		final RuleIrAttributeCondition ruleIrAttributeCondition = (RuleIrAttributeCondition) ruleIrConditions.get(0);
		assertEquals(RuleIrAttributeOperator.IN, ruleIrAttributeCondition.getOperator());
		final List<String> categories = (List<String>) ruleIrAttributeCondition.getValue();
		assertTrue(categories.contains("categoryCode1"));
		assertTrue(categories.contains("categoryCode2"));

		assertThat(ruleIrConditions.get(1), instanceOf(RuleIrAttributeRelCondition.class));
		final RuleIrAttributeRelCondition ruleIrAttributeRelCondition = (RuleIrAttributeRelCondition) ruleIrConditions.get(1);
		assertEquals(RuleIrAttributeOperator.CONTAINS, ruleIrAttributeRelCondition.getOperator());
		assertEquals(PRODUCT_RAO_VAR, ruleIrAttributeRelCondition.getVariable());
		assertEquals(PRODUCT_RAO_CATEGORIES_ATTRIBUTE, ruleIrAttributeRelCondition.getAttribute());
		assertEquals(CATEGORY_RAO_VAR, ruleIrAttributeRelCondition.getTargetVariable());

		assertThat(ruleIrConditions.get(2), instanceOf(RuleIrAttributeRelCondition.class));
		final RuleIrAttributeRelCondition ruleIrAttributeRelConditionOrderEntry = (RuleIrAttributeRelCondition) ruleIrConditions
				.get(2);
		assertEquals(RuleIrAttributeOperator.EQUAL, ruleIrAttributeRelConditionOrderEntry.getOperator());
		assertEquals(ORDER_ENTRY_RAO_VAR, ruleIrAttributeRelConditionOrderEntry.getVariable());
		assertEquals(ORDER_ENTRY_RAO_PRODUCT_ATTRIBUTE, ruleIrAttributeRelConditionOrderEntry.getAttribute());
		assertEquals(PRODUCT_RAO_VAR, ruleIrAttributeRelConditionOrderEntry.getTargetVariable());

		assertThat(ruleIrConditions.get(3), instanceOf(RuleIrAttributeCondition.class));
		final RuleIrAttributeCondition ruleIrAttributeOrderEntryQuantityCondition = (RuleIrAttributeCondition) ruleIrConditions
				.get(3);
		assertEquals(RuleIrAttributeOperator.GREATER_THAN, ruleIrAttributeOrderEntryQuantityCondition.getOperator());
		assertEquals(new Integer(1), ruleIrAttributeOrderEntryQuantityCondition.getValue());

		assertThat(ruleIrConditions.get(4), instanceOf(RuleIrAttributeRelCondition.class));

		final RuleIrAttributeRelCondition RuleIrCartOrderEntryRelCondition = (RuleIrAttributeRelCondition) ruleIrConditions.get(4);
		assertEquals(RuleIrAttributeOperator.CONTAINS, RuleIrCartOrderEntryRelCondition.getOperator());
		assertEquals(CART_RAO_VAR, RuleIrCartOrderEntryRelCondition.getVariable());
		assertEquals(ORDER_ENTRY_RAO_VAR, RuleIrCartOrderEntryRelCondition.getTargetVariable());
		assertEquals(CART_RAO_ENTRIES_ATTRIBUTE, RuleIrCartOrderEntryRelCondition.getAttribute());

		assertThat(ruleIrConditions.get(5), instanceOf(RuleIrAttributeRelCondition.class));
		final RuleIrAttributeRelCondition ruleIrCartConsumedCondition = (RuleIrAttributeRelCondition) ruleIrConditions.get(5);
		assertEquals(RuleIrAttributeOperator.EQUAL, ruleIrCartConsumedCondition.getOperator());
		assertEquals(ORDER_ENTRY_RAO_VAR, ruleIrCartConsumedCondition.getTargetVariable());

		assertThat(ruleIrConditions.get(6), instanceOf(RuleIrAttributeCondition.class));
		final RuleIrAttributeCondition ruleIrAvailableQtyCondition = (RuleIrAttributeCondition) ruleIrConditions.get(6);
		assertEquals(RuleIrAttributeOperator.GREATER_THAN_OR_EQUAL, ruleIrAvailableQtyCondition.getOperator());
		assertEquals(AVAILABLE_QUANTITY_VAR, ruleIrAvailableQtyCondition.getAttribute());
	}
}
