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
package de.hybris.platform.ruleengineservices.rule.services.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.model.AbstractRuleTemplateModel;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.ruleengineservices.model.SourceRuleTemplateModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@IntegrationTest
public class DefaultRuleServiceTest extends ServicelayerTransactionalTest
{
	@Rule
	public ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@Resource
	private DefaultRuleService defaultRuleService;

	@Resource
	private ModelService modelService;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/ruleengineservices/test/rule/DefaultRuleServiceTest.impex", "utf-8");
	}

	@Test
	public void testCreateRuleFromTemplate()
	{
		// given
		final SourceRuleTemplateModel ruleTemplate = modelService.create(SourceRuleTemplateModel.class);
		ruleTemplate.setCode("testRuleTemplate");
		ruleTemplate.setName("Test Rule Template");
		ruleTemplate.setConditions("[{\"parameters\":{},\"definitionId\":\"group\"}]");
		ruleTemplate.setActions("[{\"parameters\":{\"value\":{\"value\":10}},\"definitionId\":\"y_order_percentage_discount\"}]");

		// when
		final SourceRuleModel createdRule = defaultRuleService.createRuleFromTemplate((AbstractRuleTemplateModel) ruleTemplate);

		// then
		assertTrue(createdRule.getCode().startsWith(ruleTemplate.getCode() + "-"));
		assertEquals("Test Rule Template", createdRule.getName());
		assertEquals(ruleTemplate.getConditions(), createdRule.getConditions());
		assertEquals(ruleTemplate.getActions(), createdRule.getActions());
	}

	@Test
	public void testCreateRuleFromTemplateIsNull()
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		defaultRuleService.createRuleFromTemplate((AbstractRuleTemplateModel) null);
	}

	@Test
	public void testCreateRuleFromTemplateWithCodeIsNull()
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		defaultRuleService.createRuleFromTemplate((AbstractRuleTemplateModel) modelService.create(SourceRuleTemplateModel.class));
	}

	@Test
	public void testCreateRuleFromTemplateWithCustomCode()
	{
		// given
		final SourceRuleTemplateModel ruleTemplate = modelService.create(SourceRuleTemplateModel.class);
		ruleTemplate.setCode("testRuleTemplate");
		ruleTemplate.setName("Test Rule Template");
		ruleTemplate.setConditions("[{\"parameters\":{},\"definitionId\":\"group\"}]");
		ruleTemplate.setActions("[{\"parameters\":{\"value\":{\"value\":10}},\"definitionId\":\"y_order_percentage_discount\"}]");

		final String newRuleCode = "custom_code";
		// when
		final SourceRuleModel createdRule = defaultRuleService.createRuleFromTemplate(newRuleCode,
				(AbstractRuleTemplateModel) ruleTemplate);

		// then
		assertTrue(newRuleCode.equals(createdRule.getCode()));
		assertEquals("Test Rule Template", createdRule.getName());
		assertEquals(ruleTemplate.getConditions(), createdRule.getConditions());
		assertEquals(ruleTemplate.getActions(), createdRule.getActions());
	}

	@Test
	public void testCreateRuleFromTemplateWithCustomCodeIsNull()
	{
		// given
		final SourceRuleTemplateModel ruleTemplate = modelService.create(SourceRuleTemplateModel.class);
		ruleTemplate.setCode("testRuleTemplate");
		ruleTemplate.setName("Test Rule Template");
		ruleTemplate.setConditions("[{\"parameters\":{},\"definitionId\":\"group\"}]");
		ruleTemplate.setActions("[{\"parameters\":{\"value\":{\"value\":10}},\"definitionId\":\"y_order_percentage_discount\"}]");

		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		defaultRuleService.createRuleFromTemplate(null,
				(AbstractRuleTemplateModel) ruleTemplate);
	}

	@Test
	public void testCreateRuleFromTemplateIsNullWithCustomCode()
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		defaultRuleService.createRuleFromTemplate("custom_code", (AbstractRuleTemplateModel) null);
	}

	@Test
	public void testCloneRule()
	{
		// given
		final SourceRuleModel rule = modelService.create(SourceRuleModel.class);
		rule.setCode("testRule");
		rule.setName("Test Rule");
		rule.setConditions("[{\"parameters\":{},\"definitionId\":\"group\"}]");
		rule.setActions("[{\"parameters\":{\"value\":{\"value\":10}},\"definitionId\":\"y_order_percentage_discount\"}]");

		// when
		final SourceRuleModel clonedRule = (SourceRuleModel) defaultRuleService.cloneRule("clonedTestRule", rule);

		// then
		assertEquals("clonedTestRule", clonedRule.getCode());
		assertEquals("Test Rule", clonedRule.getName());
		assertEquals(rule.getConditions(), clonedRule.getConditions());
		assertEquals(rule.getActions(), clonedRule.getActions());
		assertEquals(RuleStatus.UNPUBLISHED, clonedRule.getStatus());
		assertEquals(Long.valueOf(0), clonedRule.getVersion());
		assertTrue(CollectionUtils.isEmpty(clonedRule.getRulesModules()));
	}
}
