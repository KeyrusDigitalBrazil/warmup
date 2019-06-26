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
package de.hybris.platform.ruleengine.dao.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.AbstractRulesModuleModel;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultRulesModuleDaoIT extends ServicelayerTest
{
	@Resource(name = "defaultRulesModuleDao")
	private DefaultRulesModuleDao rulesModuleDao;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/ruleengine/test/dao/rulesmoduledao-test-data.impex", "UTF-8");
	}

	@Test
	public void testFindActiveRulesModulesByRuleType()
	{
		final List<AbstractRulesModuleModel> rulesModules = rulesModuleDao.findActiveRulesModulesByRuleType(RuleType.DEFAULT);
		assertThat(rulesModules).isNotEmpty().hasSize(2);
		assertThat(rulesModules.stream().allMatch(rm -> RuleType.DEFAULT.equals(rm.getRuleType()))).isTrue();
		assertThat(rulesModules.stream().allMatch(rm -> rm.getActive().booleanValue())).isTrue();
		assertThat(rulesModules.stream().anyMatch(rm -> "preview-module".equals(rm.getName()))).isTrue();
		assertThat(rulesModules.stream().anyMatch(rm -> "live-module".equals(rm.getName()))).isTrue();
	}
}
