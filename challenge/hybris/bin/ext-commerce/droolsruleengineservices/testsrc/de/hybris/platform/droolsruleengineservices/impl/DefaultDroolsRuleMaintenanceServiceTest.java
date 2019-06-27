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
package de.hybris.platform.droolsruleengineservices.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class DefaultDroolsRuleMaintenanceServiceTest extends AbstractRuleEngineServicesTest
{

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/droolsruleengineservices/test/ruleenginesetup.impex", "utf-8");
	}

	@Test
	public void testUpdateEngineRule()
	{

		final DroolsRuleModel ruleEngineRule = createRule("testUpdateEngineRule", "Test Update Engine Rule");

		final DroolsKIEBaseModel kbase = getKIEBase();

		assertFalse(getPlatformRuleEngineService().updateEngineRule(ruleEngineRule, kbase.getKieModule()).isActionFailed());

		assertTrue("KIEBase should contain rule.",
					 kbase.getRules().stream().anyMatch(r -> r.getCode().equals(ruleEngineRule.getCode())));
	}

	protected DroolsKIEBaseModel getKIEBase()
	{
		final DroolsRuleEngineContextModel context = (DroolsRuleEngineContextModel) getRuleEngineContextDao()
				.findRuleEngineContextByName(RULE_ENGINGE_CONTEXT_NAME);

		return context.getKieSession().getKieBase();
	}

	@Test
	public void testUpdateEngineRuleNullRule()
	{
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("ruleEngineRule");
		getPlatformRuleEngineService().updateEngineRule(null,  null);
	}

	private DroolsRuleModel createRule(final String code, final String name)
	{
		final DroolsKIEBaseModel baseModel = getKieBaseOrCreateNew();

		final DroolsRuleModel ruleEngineRule = getModelService().create(DroolsRuleModel.class);
		ruleEngineRule.setCode(code);
		ruleEngineRule.setUuid(name);
		ruleEngineRule.setRulePackage("de.hybris.platform.promotionengineservices.test");
		ruleEngineRule.setRuleContent(createRuleContent(code, name));
		ruleEngineRule.setRuleType(RuleType.DEFAULT);
		ruleEngineRule.setKieBase(baseModel);
		ruleEngineRule.setCurrentVersion(true);
		ruleEngineRule.setActive(true);
		return ruleEngineRule;
	}

	protected String createRuleContent(final String code, final String name)
	{
		return "package de.hybris.platform.promotionengineservices.test\n"
				+ "import de.hybris.platform.ruleengineservices.rao.CartRAO;\n"
				+ "import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;\n"
				+ "import de.hybris.platform.ruleengineservices.rao.DiscountRAO;\n"
				+ "import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;\n"
				+ "import java.math.BigDecimal;\n"
				+ "import de.hybris.platform.ruleengineservices.rule.evaluation.AddFixedPriceEntryDiscountRAOAction;\n"
				+ "global AddFixedPriceEntryDiscountRAOAction addFixedPriceEntryDiscountRAOAction;\n"
				+ "rule \""
				+ name
				+ "\"\n"
				+ "@ruleCode(\""
				+ code
				+ "\")\n"
				+ "@moduleName(\""
				+ RULE_ENGINGE_KMODULE_JUNIT
				+ "\")\n"
				+ "salience 10\n"
				+ "when\n"
				+ "$orderEntry:OrderEntryRAO(product.getCode()==\"1382080\", availableQuantity > 0 )\n"
				+ "$cart : CartRAO(entries contains $orderEntry)\n"
				+ "$result : RuleEngineResultRAO()\n"
				+ "then\n"
				+ "addFixedPriceEntryDiscountRAOAction.addFixedPriceEntryDiscount($orderEntry, new BigDecimal(\"200.00\"), $result, kcontext);\n"
				+ "end\n";
	}

}
