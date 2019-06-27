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
package de.hybris.platform.ruleengineservices.compiler.impl;

import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleTargetCodeGenerator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultRuleTargetCodeGeneratorFactoryTest
{
	@Rule
	public ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@Mock
	private RuleCompilerContext ruleCompilerContext;

	@Mock
	private RuleTargetCodeGenerator ruleTargetCodeGenerator;

	private DefaultRuleTargetCodeGeneratorFactory ruleTargetCodeGeneratorFactory;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		ruleTargetCodeGeneratorFactory = new DefaultRuleTargetCodeGeneratorFactory();
		ruleTargetCodeGeneratorFactory.setRuleTargetCodeGenerator(ruleTargetCodeGenerator);
	}

	@Test
	public void testGetGenerator() throws Exception
	{
		// when
		final RuleTargetCodeGenerator generator = ruleTargetCodeGeneratorFactory.getTargetCodeGenerator(ruleCompilerContext);

		// then
		assertSame(ruleTargetCodeGenerator, generator);
	}
}
