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
package de.hybris.platform.ruleengineservices.definitions.validation;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerProblem;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerProblemFactory;
import de.hybris.platform.ruleengineservices.rule.data.AbstractRuleDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterDefinitionData;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@UnitTest
public class RuleRequiredParameterValidatorTest
{
	@Mock
	private RuleCompilerContext context;
	@Mock
	private RuleCompilerProblemFactory ruleCompilerProblemFactory;

	private AbstractRuleDefinitionData ruleDefinition;
	private RuleParameterData parameter;
	private RuleParameterDefinitionData parameterDefinition;

	private RuleRequiredParameterValidator ruleRequiredParameterValidator;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		ruleDefinition = new AbstractRuleDefinitionData()
		{/* empty */};
		parameter = new RuleParameterData();
		parameterDefinition = new RuleParameterDefinitionData();
		ruleRequiredParameterValidator = new RuleRequiredParameterValidator();

		ruleRequiredParameterValidator.setRuleCompilerProblemFactory(ruleCompilerProblemFactory);
	}

	@Test
	public void testValueIsNull()
	{
		// given
		parameter.setValue(null);

		// when
		ruleRequiredParameterValidator.validate(context, ruleDefinition, parameter, parameterDefinition);

		// then
		verify(context).addProblem(any(RuleCompilerProblem.class));
	}

	@Test
	public void testValueIsEmptyString()
	{
		// given
		parameter.setValue(StringUtils.EMPTY);

		// when
		ruleRequiredParameterValidator.validate(context, ruleDefinition, parameter, parameterDefinition);

		// then
		verify(context).addProblem(any(RuleCompilerProblem.class));
	}

	@Test
	public void testValueIsEmptyCollection()
	{
		// given
		parameter.setValue(CollectionUtils.EMPTY_COLLECTION);

		// when
		ruleRequiredParameterValidator.validate(context, ruleDefinition, parameter, parameterDefinition);

		// then
		verify(context).addProblem(any(RuleCompilerProblem.class));
	}

	@Test
	public void testValueIsEmptyMap()
	{
		// given
		parameter.setValue(MapUtils.EMPTY_SORTED_MAP);

		// when
		ruleRequiredParameterValidator.validate(context, ruleDefinition, parameter, parameterDefinition);

		// then
		verify(context).addProblem(any(RuleCompilerProblem.class));
	}
}
