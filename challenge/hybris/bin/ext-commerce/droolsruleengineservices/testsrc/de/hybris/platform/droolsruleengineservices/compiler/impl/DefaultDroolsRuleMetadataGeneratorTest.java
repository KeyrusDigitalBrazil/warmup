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
package de.hybris.platform.droolsruleengineservices.compiler.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.droolsruleengineservices.compiler.DroolsRuleGeneratorContext;
import de.hybris.platform.ruleengineservices.compiler.RuleIr;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDroolsRuleMetadataGeneratorTest extends AbstractGeneratorTest
{
	public static final String INDENTATION = "  ";

	@Mock
	private DroolsRuleGeneratorContext droolsRuleGeneratorContext;

	@Mock
	private RuleIrCondition ruleIrCondition;

	@Mock
	private RuleIr ruleIr;

	@InjectMocks
	private DefaultDroolsRuleMetadataGenerator defaultDroolsRuleMetadataGenerator;

	@Test
	public void testGenerateMetadata() throws Exception
	{
		// given
		final String expectedDroolsCode = getResourceAsString("/droolsruleengineservices/test/compiler/generatedMeta.bin");

		final Map<String, Object> metadata = new HashMap<String, Object>()
		{
			{
				put("META_ID", "META_VALUE");
			}
		};
		when(droolsRuleGeneratorContext.getRuleIr()).thenReturn(ruleIr);
		when(ruleIr.getConditions()).thenReturn(Arrays.asList(ruleIrCondition));
		when(ruleIrCondition.getMetadata()).thenReturn(metadata);
		// when
		final String generatedDroolsCode = defaultDroolsRuleMetadataGenerator.generateMetadata(droolsRuleGeneratorContext,
				INDENTATION);
		// then
		assertEquals(expectedDroolsCode, generatedDroolsCode);
	}

	@Test
	public void testGenerateTwoMetadata() throws Exception
	{
		// given
		final String expectedDroolsCode1 = getResourceAsString("/droolsruleengineservices/test/compiler/generatedMeta21.bin");
		final String expectedDroolsCode2 = getResourceAsString("/droolsruleengineservices/test/compiler/generatedMeta22.bin");

		final Map<String, Object> metadata1 = new HashMap<String, Object>()
		{
			{
				put("META_ID1", "META_VALUE1");
				put("META_ID2", "META_VALUE2");
			}
		};
		when(droolsRuleGeneratorContext.getRuleIr()).thenReturn(ruleIr);
		when(ruleIr.getConditions()).thenReturn(Arrays.asList(ruleIrCondition));
		when(ruleIrCondition.getMetadata()).thenReturn(metadata1);
		// when
		final String generatedDroolsCode = defaultDroolsRuleMetadataGenerator.generateMetadata(droolsRuleGeneratorContext,
				INDENTATION);
		// then
		assertTrue("Unexpected metadata generated",
				expectedDroolsCode1.equals(generatedDroolsCode) || expectedDroolsCode2.equals(generatedDroolsCode));
	}

}
