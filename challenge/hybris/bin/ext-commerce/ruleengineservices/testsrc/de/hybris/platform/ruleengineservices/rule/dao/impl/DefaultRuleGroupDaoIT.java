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
package de.hybris.platform.ruleengineservices.rule.dao.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.ruleengineservices.model.RuleGroupModel;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultRuleGroupDaoIT extends ServicelayerTest
{
	@Resource(name = "defaultRuleGroupDao")
	private DefaultRuleGroupDao ruleGroupDao;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/ruleengineservices/test/rule/maintenance/test_source_rules.impex", "UTF-8");
	}

	@Test
	public void testFindAllReferredRuleGroups()
	{
		final List<RuleGroupModel> ruleGroups = ruleGroupDao.findAllReferredRuleGroups();
		assertThat(ruleGroups).isNotEmpty().hasSize(1);
		assertThat(ruleGroups.stream().anyMatch(rg -> "productPromotionRuleGroup".equals(rg.getCode()))).isTrue();
	}

	@Test
	public void testFindAllNotReferredRuleGroups()
	{
		final List<RuleGroupModel> ruleGroups = ruleGroupDao.findAllNotReferredRuleGroups();
		assertThat(ruleGroups).isNotEmpty().hasSize(1);
		assertThat(ruleGroups.stream().anyMatch(rg -> "emptyPromotionRuleGroup".equals(rg.getCode()))).isTrue();
	}
}
