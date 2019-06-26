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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.ruledefinitions.MembershipOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeRelCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrNotCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrTypeCondition;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryGroupRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import org.apache.commons.collections4.CollectionUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleEntryGroupTypeConditionTranslatorTest
{
	private static final String ORDER_ENTRY_RAO_VAR = "orderEntryRaoVariable";
	private static final String ORDER_ENTRY_GROUP_RAO_VAR = "orderEntryGroupRaoVariable";
	private static final String CART_RAO_VAR = "cartRaoVariable";
	private static final String ENTRY_GROUP_NUMBER_VAR = "entryGroupNumberVariable";

	@InjectMocks
	private RuleEntryGroupTypeConditionTranslator translator;

	@Mock
	private RuleCompilerContext context;

	@Mock
	private RuleConditionData condition;

	@Mock
	private RuleConditionDefinitionData conditionDefinition;

	@Mock
	private RuleParameterData operatorParameter;

	@Mock
	private RuleParameterData valueParameter;

	@Mock
	private GroupType groupType1;

	@Mock
	private GroupType groupType2;

	@Mock
	private Map<String, RuleParameterData> parameters;

	@Before
	public void setUp()
	{
		when(parameters.get("operator")).thenReturn(operatorParameter);
		when(parameters.get("groupTypes")).thenReturn(valueParameter);

		when(condition.getParameters()).thenReturn(parameters);

		when(context.generateVariable(OrderEntryRAO.class)).thenReturn(ORDER_ENTRY_RAO_VAR);
		when(context.generateVariable(OrderEntryGroupRAO.class)).thenReturn(ORDER_ENTRY_GROUP_RAO_VAR);
		when(context.generateVariable(CartRAO.class)).thenReturn(CART_RAO_VAR);
		when(context.generateVariable(Integer.class)).thenReturn(ENTRY_GROUP_NUMBER_VAR);
	}

	@Test
	public void shouldTranslateOperatorParamNull()
	{
		when(parameters.get("operator")).thenReturn(null);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void shouldTranslateValueParamNull()
	{
		when(parameters.get("groupTypes")).thenReturn(null);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void shouldTranslateOperatorNull()
	{
		when(operatorParameter.getValue()).thenReturn(null);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void shouldTranslateValueNull()
	{
		when(valueParameter.getValue()).thenReturn(null);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void shouldTranslateEmptyValue()
	{
		when(valueParameter.getValue()).thenReturn(emptyList());
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void shouldTranslateInCondition()
	{
		when(operatorParameter.getValue()).thenReturn(MembershipOperator.IN);

		final List<GroupType> value = asList(groupType1, groupType2);

		when(valueParameter.getValue()).thenReturn(value);

		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);
		assertThat(ruleIrCondition, instanceOf(RuleIrGroupCondition.class));

		final RuleIrGroupCondition irGroupCondition = (RuleIrGroupCondition) ruleIrCondition;
		assertEquals(4, irGroupCondition.getChildren().size());
		assertEquals(RuleIrGroupOperator.AND, irGroupCondition.getOperator());

		assertThat(irGroupCondition.getChildren(), containsInAnyOrder(orderEntryGroupRaoConditionMatcher(),
				groupTypeRaoConditionMatcher(value), orderEntryRaoConditionMatcher(), instanceOf(RuleIrTypeCondition.class)));
	}

	@Test
	public void shouldTranslateNotInCondition()
	{
		when(operatorParameter.getValue()).thenReturn(MembershipOperator.NOT_IN);

		final List<GroupType> value = asList(groupType1, groupType2);

		when(valueParameter.getValue()).thenReturn(value);

		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);
		assertThat(ruleIrCondition, instanceOf(RuleIrGroupCondition.class));

		final RuleIrGroupCondition irGroupCondition = (RuleIrGroupCondition) ruleIrCondition;
		assertEquals(1, irGroupCondition.getChildren().size());
		assertEquals(RuleIrGroupOperator.AND, irGroupCondition.getOperator());

		assertThat(irGroupCondition.getChildren().get(0), instanceOf(RuleIrNotCondition.class));

		final RuleIrNotCondition irNotCondition = (RuleIrNotCondition) irGroupCondition.getChildren().get(0);
		assertEquals(4, irNotCondition.getChildren().size());

		assertThat(irNotCondition.getChildren(), containsInAnyOrder(orderEntryGroupRaoConditionMatcher(),
				groupTypeRaoConditionMatcher(value), orderEntryRaoConditionMatcher(), instanceOf(RuleIrTypeCondition.class)));
	}

	protected Matcher<RuleIrCondition> orderEntryGroupRaoConditionMatcher()
	{
		return new BaseMatcher<RuleIrCondition>()
		{
			@Override
			public boolean matches(final Object o)
			{
				if (o instanceof RuleIrAttributeRelCondition)
				{
					final RuleIrAttributeRelCondition condition = (RuleIrAttributeRelCondition) o;
					return ORDER_ENTRY_RAO_VAR.equals(condition.getTargetVariable())
							&& "entryGroupId".equals(condition.getAttribute())
							&& RuleIrAttributeOperator.MEMBER_OF == condition.getOperator();
				}
				return false;
			}

			@Override
			public void describeTo(final Description description)
			{
				// empty
			}
		};
	}

	protected Matcher<RuleIrCondition> groupTypeRaoConditionMatcher(final List<GroupType> value)
	{
		return new BaseMatcher<RuleIrCondition>()
		{
			@Override
			public boolean matches(final Object o)
			{
				if (o instanceof RuleIrAttributeCondition)
				{
					final RuleIrAttributeCondition condition = (RuleIrAttributeCondition) o;
					return CollectionUtils.isEqualCollection(value, (List<GroupType>) condition.getValue())
							&& "groupType".equals(condition.getAttribute())
							&& RuleIrAttributeOperator.IN == condition.getOperator();
				}
				return false;
			}

			@Override
			public void describeTo(final Description description)
			{
				// empty
			}
		};
	}

	protected Matcher<RuleIrCondition> orderEntryRaoConditionMatcher()
	{
		return new BaseMatcher<RuleIrCondition>()
		{
			@Override
			public boolean matches(final Object o)
			{
				if (o instanceof RuleIrAttributeRelCondition)
				{
					final RuleIrAttributeRelCondition condition = (RuleIrAttributeRelCondition) o;
					return "entries".equals(condition.getAttribute())
							&& RuleIrAttributeOperator.CONTAINS == condition.getOperator();
				}
				return false;
			}

			@Override
			public void describeTo(final Description description)
			{
				// empty
			}
		};
	}
}
