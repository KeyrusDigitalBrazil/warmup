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
package de.hybris.platform.ruleengineservices.rule.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.ruleengineservices.model.AbstractRuleTemplateModel;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleTypeMappingException;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultRuleTypeMappingStrategyTest
{
	@Rule
	@SuppressWarnings("PMD")
	public final ExpectedException expectedException = ExpectedException.none();

	@Mock
	private TypeService typeService;
	@Mock
	private ComposedTypeModel composedType;

	@InjectMocks
	private final DefaultRuleTypeMappingStrategy strategy = new DefaultRuleTypeMappingStrategy();


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testNullParameters() throws RuleTypeMappingException
	{
		//expect
		expectedException.expect(RuleTypeMappingException.class);

		//when
		strategy.findRuleType(null);
	}

	@Test
	public void templateNoConvention() throws RuleTypeMappingException
	{
		//expect
		expectedException.expect(RuleTypeMappingException.class);

		//given
		when(typeService.getComposedTypeForClass(WithoutConventionTestClass.class)).thenReturn(composedType);
		when(composedType.getCode()).thenReturn("WithoutConventionTest");

		//when
		strategy.findRuleType(WithoutConventionTestClass.class);
	}

	@Test
	public void sourceRuleTemplateTest() throws RuleTypeMappingException
	{
		//given
		when(typeService.getComposedTypeForClass(ProperRuleTemplate.class)).thenReturn(composedType);
		when(composedType.getCode()).thenReturn(ProperRuleTemplate.class.getName());
		when(typeService.getModelClass(ProperRule.class.getName())).thenReturn((Class) ProperRule.class);

		//when
		final Class<? extends AbstractRuleModel> result = strategy.findRuleType(ProperRuleTemplate.class);

		//then
		assertEquals(ProperRule.class.getName(), result.getName());
	}

	class WithoutConventionTestClass extends AbstractRuleTemplateModel
	{
		//NOPMD
	}

	class ProperRuleTemplate extends AbstractRuleTemplateModel
	{
		//NOPMD
	}

	class ProperRule extends AbstractRuleTemplateModel
	{
		//NOPMD
	}
}
