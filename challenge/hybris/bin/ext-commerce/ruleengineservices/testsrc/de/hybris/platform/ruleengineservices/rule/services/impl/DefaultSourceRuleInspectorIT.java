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

import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.ruleengineservices.rule.dao.RuleDao;
import de.hybris.platform.ruleengineservices.rule.services.SourceRuleInspector;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;


@IntegrationTest
public class DefaultSourceRuleInspectorIT extends ServicelayerTransactionalTest
{
	public static final String PRODUCT_FIXED_PRICE_SOURCE_RULE = "product_fixed_price";
	@Resource
	private SourceRuleInspector sourceRuleInspector;
	@Resource
	private RuleDao ruleDao;


	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/ruleengineservices/test/rule/defaultRuleConditionAndActionTest.impex", "utf-8");
	}

	@Test
	public void shouldConfirmThatConditionIsDefined() throws Exception
	{
		//given
		final SourceRuleModel sourceRule = ruleDao.findRuleByCode(PRODUCT_FIXED_PRICE_SOURCE_RULE);
		//when
		final boolean result = sourceRuleInspector.hasRuleCondition(sourceRule, "y_group");
		//then
		assertThat(result).isTrue();
	}

	@Test
	public void shouldConfirmThatConditionIsDefinedInConditionsTree() throws Exception
	{
		//given
		final SourceRuleModel sourceRule = ruleDao.findRuleByCode(PRODUCT_FIXED_PRICE_SOURCE_RULE);
		//when
		final boolean result = sourceRuleInspector.hasRuleCondition(sourceRule, "y_qualifying_products");
		//then
		assertThat(result).isTrue();
	}

	@Test
	public void shouldDenyThatConditionIsDefinedInConditionsTree() throws Exception
	{
		//given
		final SourceRuleModel sourceRule = ruleDao.findRuleByCode(PRODUCT_FIXED_PRICE_SOURCE_RULE);
		//when
		final boolean result = sourceRuleInspector.hasRuleCondition(sourceRule, "y_undefined_condition");
		//then
		assertThat(result).isFalse();
	}


	@Test
	public void shouldConfirmThatActionIsDefined() throws Exception
	{
		//given
		final SourceRuleModel sourceRule = ruleDao.findRuleByCode(PRODUCT_FIXED_PRICE_SOURCE_RULE);
		//when
		final boolean result = sourceRuleInspector.hasRuleAction(sourceRule, "y_order_entry_fixed_price");
		//then
		assertThat(result).isTrue();
	}

	@Test
	public void shouldDenyThatActionIsDefined() throws Exception
	{
		//given
		final SourceRuleModel sourceRule = ruleDao.findRuleByCode(PRODUCT_FIXED_PRICE_SOURCE_RULE);
		//when
		final boolean result = sourceRuleInspector.hasRuleAction(sourceRule, "y_undefined_action");
		//then
		assertThat(result).isFalse();
	}
}
