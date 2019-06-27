/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.rules.compiler.listeners;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;

import org.junit.Test;
import org.mockito.Mockito;

@UnitTest
public class ProductConfigRuleCompilerListenerTest
{
	private ProductConfigRuleCompilerListener classUnderTest = new ProductConfigRuleCompilerListener();

	@Test
	public void testBeforeCompileWithProductConfigSourceRule()
	{
		RuleCompilerContext context = Mockito.mock(RuleCompilerContext.class);
		when(context.getRule()).thenReturn(new ProductConfigSourceRuleModel());

		classUnderTest.beforeCompile(context);

		verify(context, times(3)).generateVariable(any());
	}

	@Test
	public void testBeforeCompileWithNonProductConfigSourceRule()
	{
		RuleCompilerContext context = Mockito.mock(RuleCompilerContext.class);
		when(context.getRule()).thenReturn(new SourceRuleModel());

		classUnderTest.beforeCompile(context);

		verify(context, times(0)).generateVariable(any());
	}
}
