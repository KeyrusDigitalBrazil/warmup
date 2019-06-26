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
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultRuleEngineContextDaoIT extends ServicelayerTest
{
	@Resource(name = "defaultRulesModuleDao")
	private DefaultRulesModuleDao rulesModuleDao;

	@Resource(name = "defaultRuleEngineContextDao")
	private DefaultRuleEngineContextDao ruleEngineContextDao;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/ruleengine/test/dao/rulesmoduledao-test-data.impex", "UTF-8");
	}

	@Test
	public void testFindRuleEngineContextByRulesModule()
	{
		final List<? extends AbstractRuleEngineContextModel> ctxs = ruleEngineContextDao
				.findRuleEngineContextByRulesModule(rulesModuleDao.findByName("live-module"));
		assertThat(ctxs).isNotEmpty().hasSize(1);
		assertThat(ctxs.stream().anyMatch(ctx -> "live-context".equals(ctx.getName()))).isTrue();
	}

	@Test
	public void testFindRuleEngineContextByRulesModuleNotFound()
	{
		final List<? extends AbstractRuleEngineContextModel> ctxs = ruleEngineContextDao
				.findRuleEngineContextByRulesModule(rulesModuleDao.findByName("preview-module"));
		assertThat(ctxs).isEmpty();
	}
}
