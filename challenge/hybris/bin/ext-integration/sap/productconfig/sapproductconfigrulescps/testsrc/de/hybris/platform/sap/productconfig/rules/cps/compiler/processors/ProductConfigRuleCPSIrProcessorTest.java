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
package de.hybris.platform.sap.productconfig.rules.cps.compiler.processors;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerException;
import de.hybris.platform.ruleengineservices.compiler.RuleIr;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigRuleCPSIrProcessorTest
{
	private ProductConfigRuleCPSIrProcessor classUnderTest;
	private RuleIr ruleIr;
	private ProductConfigSourceRuleModel productConfigSourceRule;

	@Mock
	private RuleCompilerContext context;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		classUnderTest = new ProductConfigRuleCPSIrProcessor();

		ruleIr = new RuleIr();
		productConfigSourceRule = new ProductConfigSourceRuleModel();

		given(context.getRule()).willReturn(productConfigSourceRule);
	}

	@Test(expected = RuleCompilerException.class)
	public void testProcessProductInCartAndDiscountAction()
	{
		productConfigSourceRule.setConditions("... y_configurable_product_in_cart ...");
		productConfigSourceRule.setActions("... y_configurable_product_percentage_discount_for_option ...");
		// RuleCompilerException expected
		classUnderTest.process(context, ruleIr);
	}

	@Test(expected = RuleCompilerException.class)
	public void testProcessProductInCartAndPromoMessageAction()
	{
		productConfigSourceRule.setConditions("... y_configurable_product_in_cart ...");
		productConfigSourceRule.setActions("... y_configurable_product_display_promo_message ...");
		// RuleCompilerException expected
		classUnderTest.process(context, ruleIr);
	}

	@Test(expected = RuleCompilerException.class)
	public void testProcessProductInCartAndPromoOpportunityMessageAction()
	{
		productConfigSourceRule.setConditions("... y_configurable_product_in_cart ...");
		productConfigSourceRule.setActions("... y_configurable_product_display_promo_opportunity_message ...");
		// RuleCompilerException expected
		classUnderTest.process(context, ruleIr);
	}

	@Test
	public void testProcessNoProductInCartNoPromoActions()
	{
		productConfigSourceRule.setConditions("No ProductInCart Condition");
		productConfigSourceRule.setActions("No Promo Actions");
		// No exception expected
		classUnderTest.process(context, ruleIr);
	}

	@Test
	public void testProcessPromoActionAndNoProductInCart()
	{
		productConfigSourceRule.setConditions("No ProductInCart Condition");
		productConfigSourceRule.setActions("... y_configurable_product_percentage_discount_for_option ...");
		// No exception expected
		classUnderTest.process(context, ruleIr);
	}

	@Test
	public void testProcessProductInCartAndNoPromoAction()
	{
		productConfigSourceRule.setConditions("... y_configurable_product_in_cart ...");
		productConfigSourceRule.setActions("No Promo Actions");
		// No exception expected
		classUnderTest.process(context, ruleIr);
	}

	@Test
	public void testProcessProductInCartAndDiscountActionNotProductConfigRule()
	{
		final SourceRuleModel sourceRule = new SourceRuleModel();
		sourceRule.setConditions("... y_configurable_product_in_cart ...");
		sourceRule.setActions("... y_configurable_product_percentage_discount_for_option ...");
		given(context.getRule()).willReturn(sourceRule);
		// No exception expected
		classUnderTest.process(context, ruleIr);
	}
}
