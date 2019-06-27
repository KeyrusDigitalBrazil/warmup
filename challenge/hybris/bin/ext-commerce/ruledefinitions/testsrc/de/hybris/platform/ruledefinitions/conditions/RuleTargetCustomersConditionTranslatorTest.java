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
import de.hybris.platform.ruledefinitions.CollectionOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeRelCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrExistsCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrNotCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrTypeCondition;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.UserGroupRAO;
import de.hybris.platform.ruleengineservices.rao.UserRAO;
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
public class RuleTargetCustomersConditionTranslatorTest
{
	private static final String USER_RAO_VAR = "userRaoVariable";
	private static final String USER_GROUP_RAO_VAR = "userGroupRaoVariable";
	private static final String CART_RAO_VAR = "cartRaoVariable";
	public static final String USER_RAO_GROUPS_ATTRIBUTE = "groups";
	public static final String USER_RAO_ID_ATTRIBUTE = "id";
	public static final String USER_RAO_PK_ATTRIBUTE = "pk";
	public static final String CART_RAO_USER_ATTRIBUTE = "user";


	@InjectMocks
	private RuleTargetCustomersConditionTranslator translator;

	@Mock
	private RuleCompilerContext context;
	@Mock
	private RuleConditionData condition;
	@Mock
	private RuleConditionDefinitionData conditionDefinition;
	@Mock
	private Map<String, RuleParameterData> parameters;
	@Mock
	private RuleParameterData customerGroupsOperatorParameter, customerGroupsParameter, customersParameter,
			excludedCustomerGroupsParameter, excludedCustomersParameter;

	@Before
	public void setUp()
	{
		when(condition.getParameters()).thenReturn(parameters);
		when(parameters.get(RuleTargetCustomersConditionTranslator.CUSTOMER_GROUPS_OPERATOR_PARAM)).thenReturn(
				customerGroupsOperatorParameter);
		when(parameters.get(RuleTargetCustomersConditionTranslator.CUSTOMER_GROUPS_PARAM)).thenReturn(customerGroupsParameter);
		when(parameters.get(RuleTargetCustomersConditionTranslator.CUSTOMERS_PARAM)).thenReturn(customersParameter);
		when(parameters.get(RuleTargetCustomersConditionTranslator.EXCLUDED_USERS_PARAM)).thenReturn(excludedCustomersParameter);

		final List<String> customerGroups = new ArrayList<>();
		customerGroups.add("customerGroup1");
		customerGroups.add("customerGroup2");
		when(customerGroupsParameter.getValue()).thenReturn(customerGroups);
		final List<String> customers = new ArrayList<>();
		customers.add("customer1");
		customers.add("customer2");
		when(customersParameter.getValue()).thenReturn(customers);

		when(context.generateVariable(UserRAO.class)).thenReturn(USER_RAO_VAR);
		when(context.generateVariable(CartRAO.class)).thenReturn(CART_RAO_VAR);
		when(context.generateVariable(UserGroupRAO.class)).thenReturn(USER_GROUP_RAO_VAR);
	}

	@Test
	public void testTranslateCustomersParamNull()
	{
		when(parameters.get(RuleTargetCustomersConditionTranslator.CUSTOMER_GROUPS_PARAM)).thenReturn(null);
		when(parameters.get(RuleTargetCustomersConditionTranslator.CUSTOMERS_PARAM)).thenReturn(null);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);
		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void testTranslateNotOperatorCustomerCondition()
	{
		when(customerGroupsOperatorParameter.getValue()).thenReturn(CollectionOperator.NOT_CONTAINS);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);
		assertThat(ruleIrCondition, instanceOf(RuleIrGroupCondition.class));

		final RuleIrGroupCondition irGroupCondition = (RuleIrGroupCondition) ruleIrCondition;
		final List<RuleIrCondition> childCondition = irGroupCondition.getChildren();
		assertThat(childCondition.get(0), instanceOf(RuleIrNotCondition.class));
		final RuleIrNotCondition irNotCondition = (RuleIrNotCondition) childCondition.get(0);
		assertEquals(3, irNotCondition.getChildren().size());
		checkFirstLevelChildConditions(irNotCondition.getChildren());

		final RuleIrGroupCondition ruleIrGroupCondition = (RuleIrGroupCondition) irNotCondition.getChildren().get(2);
		final List<RuleIrCondition> groupConditions = ruleIrGroupCondition.getChildren();
		assertThat(groupConditions.get(0), instanceOf(RuleIrGroupCondition.class));
		final RuleIrGroupCondition irCustomerGroupsCondition = (RuleIrGroupCondition) groupConditions.get(0);
		assertEquals(2, irCustomerGroupsCondition.getChildren().size());

		checkCustomerGroupConditions(irCustomerGroupsCondition);

		assertThat(groupConditions.get(1), instanceOf(RuleIrGroupCondition.class));
		final RuleIrGroupCondition ruleIrGroupCustomerCondition = (RuleIrGroupCondition) groupConditions.get(1);
		final List<RuleIrCondition> children = ruleIrGroupCustomerCondition.getChildren();
		assertThat(children.get(0), instanceOf(RuleIrAttributeCondition.class));
		checkCustomerCondition((RuleIrAttributeCondition) children.get(0));

	}

	@Test
	public void testTranslateAnyOperatorCondition()
	{
		when(customerGroupsOperatorParameter.getValue()).thenReturn(CollectionOperator.CONTAINS_ANY);

		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);
		assertThat(ruleIrCondition, instanceOf(RuleIrGroupCondition.class));
		final RuleIrGroupCondition irGroupCondition = (RuleIrGroupCondition) ruleIrCondition;
		assertEquals(3, irGroupCondition.getChildren().size());
		checkFirstLevelChildConditions(irGroupCondition.getChildren());

		final RuleIrGroupCondition ruleIrGroupCondition = (RuleIrGroupCondition) irGroupCondition.getChildren().get(2);
		final List<RuleIrCondition> groupConditions = ruleIrGroupCondition.getChildren();
		assertThat(groupConditions.get(0), instanceOf(RuleIrGroupCondition.class));
		final RuleIrGroupCondition irCustomerGroupsCondition = (RuleIrGroupCondition) groupConditions.get(0);
		assertEquals(2, irCustomerGroupsCondition.getChildren().size());

		checkCustomerGroupConditions(irCustomerGroupsCondition);

		assertThat(groupConditions.get(1), instanceOf(RuleIrGroupCondition.class));
		final RuleIrGroupCondition ruleIrGroupCustomerCondition = (RuleIrGroupCondition) groupConditions.get(1);
		final List<RuleIrCondition> children = ruleIrGroupCustomerCondition.getChildren();
		assertThat(children.get(0), instanceOf(RuleIrAttributeCondition.class));
		checkCustomerCondition((RuleIrAttributeCondition) children.get(0));
	}

	@Test
	public void testTranslateAnyOperatorWithExcludedCondition()
	{
		when(customerGroupsOperatorParameter.getValue()).thenReturn(CollectionOperator.CONTAINS_ANY);
		final List<String> exludeCustomers = new ArrayList<>();
		exludeCustomers.add("excludeCustomer1");
		exludeCustomers.add("excludeCustomer2");
		when(excludedCustomersParameter.getValue()).thenReturn(exludeCustomers);

		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);
		assertThat(ruleIrCondition, instanceOf(RuleIrGroupCondition.class));
		final RuleIrGroupCondition irGroupCondition = (RuleIrGroupCondition) ruleIrCondition;
		assertEquals(4, irGroupCondition.getChildren().size());
		checkFirstLevelChildConditions(irGroupCondition.getChildren());

		checkExcludeCustomerCondition(irGroupCondition.getChildren().get(3), USER_RAO_ID_ATTRIBUTE);
		final RuleIrGroupCondition ruleIrGroupCondition = (RuleIrGroupCondition) irGroupCondition.getChildren().get(2);
		final List<RuleIrCondition> groupConditions = ruleIrGroupCondition.getChildren();
		assertThat(groupConditions.get(0), instanceOf(RuleIrGroupCondition.class));
		final RuleIrGroupCondition irCustomerGroupsCondition = (RuleIrGroupCondition) groupConditions.get(0);
		assertEquals(2, irCustomerGroupsCondition.getChildren().size());

		checkCustomerGroupConditions(irCustomerGroupsCondition);

		assertThat(groupConditions.get(1), instanceOf(RuleIrGroupCondition.class));
		final RuleIrGroupCondition ruleIrGroupCustomerCondition = (RuleIrGroupCondition) groupConditions.get(1);
		final List<RuleIrCondition> children = ruleIrGroupCustomerCondition.getChildren();
		assertThat(children.get(0), instanceOf(RuleIrAttributeCondition.class));
		checkCustomerCondition((RuleIrAttributeCondition) children.get(0));
	}

	@Test
	public void testTranslateExcludedConditionWithPKAttribute()
	{
		when(customerGroupsOperatorParameter.getValue()).thenReturn(CollectionOperator.CONTAINS_ANY);
		final List<String> exludeCustomers = new ArrayList<>();
		exludeCustomers.add("excludeCustomer1");
		exludeCustomers.add("excludeCustomer2");
		when(excludedCustomersParameter.getValue()).thenReturn(exludeCustomers);
		translator.setUsePk(true);

		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrGroupCondition.class));
		final RuleIrGroupCondition irGroupCondition = (RuleIrGroupCondition) ruleIrCondition;
		assertEquals(4, irGroupCondition.getChildren().size());
		checkFirstLevelChildConditions(irGroupCondition.getChildren());

		checkExcludeCustomerCondition(irGroupCondition.getChildren().get(3), USER_RAO_PK_ATTRIBUTE);
	}

	private void checkExcludeCustomerCondition(final RuleIrCondition ruleIrCondition, final String userRAOAttribute)
	{
		assertThat(ruleIrCondition, instanceOf(RuleIrAttributeCondition.class));
		final RuleIrAttributeCondition irExcludedCustomersCondition = (RuleIrAttributeCondition) ruleIrCondition;
		assertEquals(userRAOAttribute, irExcludedCustomersCondition.getAttribute());
		assertEquals(USER_RAO_VAR, irExcludedCustomersCondition.getVariable());
		assertEquals(RuleIrAttributeOperator.NOT_IN, irExcludedCustomersCondition.getOperator());
		final List<String> excludedCustomers = (List<String>) irExcludedCustomersCondition.getValue();
		assertTrue(excludedCustomers.contains("excludeCustomer1"));
		assertTrue(excludedCustomers.contains("excludeCustomer2"));
	}

	@Test
	public void testTranslateAllOperatorCondition()
	{
		when(customerGroupsOperatorParameter.getValue()).thenReturn(CollectionOperator.CONTAINS_ALL);

		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);
		assertThat(ruleIrCondition, instanceOf(RuleIrGroupCondition.class));
		final RuleIrGroupCondition irGroupCondition = (RuleIrGroupCondition) ruleIrCondition;
		assertEquals(3, irGroupCondition.getChildren().size());

		checkFirstLevelChildConditions(irGroupCondition.getChildren());

		final RuleIrGroupCondition ruleIrGroupCondition = (RuleIrGroupCondition) irGroupCondition.getChildren().get(2);
		final List<RuleIrCondition> groupConditions = ruleIrGroupCondition.getChildren();
		assertThat(groupConditions.get(0), instanceOf(RuleIrGroupCondition.class));
		final RuleIrGroupCondition irCustomerGroupsCondition = (RuleIrGroupCondition) groupConditions.get(0);
		assertEquals(4, irCustomerGroupsCondition.getChildren().size());

		checkCustomerGroupConditions(irCustomerGroupsCondition);
		assertThat(irCustomerGroupsCondition.getChildren().get(2), instanceOf(RuleIrExistsCondition.class));
		assertThat(irCustomerGroupsCondition.getChildren().get(3), instanceOf(RuleIrExistsCondition.class));

		assertThat(groupConditions.get(1), instanceOf(RuleIrGroupCondition.class));
		final RuleIrGroupCondition ruleIrGroupCustomerCondition = (RuleIrGroupCondition) groupConditions.get(1);
		final List<RuleIrCondition> children = ruleIrGroupCustomerCondition.getChildren();
		assertThat(children.get(0), instanceOf(RuleIrAttributeCondition.class));
		checkCustomerCondition((RuleIrAttributeCondition) children.get(0));
	}

	protected void checkCustomerCondition(final RuleIrAttributeCondition ruleIrAttributeCustomerCondition)
	{
		assertEquals(USER_RAO_VAR, ruleIrAttributeCustomerCondition.getVariable());
		assertEquals(USER_RAO_ID_ATTRIBUTE, ruleIrAttributeCustomerCondition.getAttribute());
		assertEquals(RuleIrAttributeOperator.IN, ruleIrAttributeCustomerCondition.getOperator());
		final List<String> customers = (List<String>) ruleIrAttributeCustomerCondition.getValue();
		assertTrue(customers.contains("customer1"));
		assertTrue(customers.contains("customer2"));
	}


	protected RuleIrGroupCondition checkFirstLevelChildConditions(final List<RuleIrCondition> ruleIrConditions)
	{
		assertThat(ruleIrConditions.get(0), instanceOf(RuleIrTypeCondition.class));
		final RuleIrTypeCondition ruleIrTypeCondition = (RuleIrTypeCondition) ruleIrConditions.get(0);
		assertEquals(USER_RAO_VAR, ruleIrTypeCondition.getVariable());

		assertThat(ruleIrConditions.get(1), instanceOf(RuleIrAttributeRelCondition.class));
		final RuleIrAttributeRelCondition cartUserRelCondition = (RuleIrAttributeRelCondition) ruleIrConditions.get(1);
		assertEquals(CART_RAO_VAR, cartUserRelCondition.getVariable());
		assertEquals(CART_RAO_USER_ATTRIBUTE, cartUserRelCondition.getAttribute());
		assertEquals(RuleIrAttributeOperator.EQUAL, cartUserRelCondition.getOperator());
		assertEquals(USER_RAO_VAR, cartUserRelCondition.getTargetVariable());

		assertThat(ruleIrConditions.get(2), instanceOf(RuleIrGroupCondition.class));
		final RuleIrGroupCondition ruleIrGroupCondition = (RuleIrGroupCondition) ruleIrConditions.get(2);
		assertEquals(RuleIrGroupOperator.OR, ruleIrGroupCondition.getOperator());
		return ruleIrGroupCondition;
	}


	protected void checkCustomerGroupConditions(final RuleIrGroupCondition irCustomerGroupsCondition)
	{
		final List<RuleIrCondition> ruleIrCustomerGroupConditions = irCustomerGroupsCondition.getChildren();
		assertThat(ruleIrCustomerGroupConditions.get(0), instanceOf(RuleIrAttributeCondition.class));
		final RuleIrAttributeCondition ruleIrAttributeCondition = (RuleIrAttributeCondition) ruleIrCustomerGroupConditions.get(0);
		assertEquals(RuleIrAttributeOperator.IN, ruleIrAttributeCondition.getOperator());
		final List<String> customerGroups = (List<String>) ruleIrAttributeCondition.getValue();
		assertTrue(customerGroups.contains("customerGroup1"));
		assertTrue(customerGroups.contains("customerGroup2"));
		assertEquals(USER_GROUP_RAO_VAR, ruleIrAttributeCondition.getVariable());

		assertThat(ruleIrCustomerGroupConditions.get(1), instanceOf(RuleIrAttributeRelCondition.class));
		final RuleIrAttributeRelCondition ruleIrAttributeRelCondition = (RuleIrAttributeRelCondition) ruleIrCustomerGroupConditions
				.get(1);
		assertEquals(RuleIrAttributeOperator.CONTAINS, ruleIrAttributeRelCondition.getOperator());
		assertEquals(USER_RAO_VAR, ruleIrAttributeRelCondition.getVariable());
		assertEquals(USER_RAO_GROUPS_ATTRIBUTE, ruleIrAttributeRelCondition.getAttribute());
		assertEquals(USER_GROUP_RAO_VAR, ruleIrAttributeRelCondition.getTargetVariable());
	}
}
