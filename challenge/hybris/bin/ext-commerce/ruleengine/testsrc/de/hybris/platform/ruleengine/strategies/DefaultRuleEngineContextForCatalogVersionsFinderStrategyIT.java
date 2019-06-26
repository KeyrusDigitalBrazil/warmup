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
package de.hybris.platform.ruleengine.strategies;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.ruleengine.dao.RuleEngineContextDao;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.DroolsRuleEngineContextModel;
import de.hybris.platform.ruleengine.strategies.impl.DefaultRuleEngineContextForCatalogVersionsFinderStrategy;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultRuleEngineContextForCatalogVersionsFinderStrategyIT extends ServicelayerTest
{

	@Resource(name = "defaultRuleEngineContextForCatalogVersionsFinderStrategy")
	private DefaultRuleEngineContextForCatalogVersionsFinderStrategy strategy;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private RuleEngineContextDao ruleEngineContextDao;

	@Resource
	private ModelService modelService;

	private DroolsRuleEngineContextModel catmap01LiveContext;
	private DroolsRuleEngineContextModel catmap02LiveContext;
	private DroolsRuleEngineContextModel catmap01PreviewContext;
	private DroolsRuleEngineContextModel catmap02PreviewContext;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/ruleengine/test/mappings/mappings-test-data.impex", "UTF-8");
		catmap01LiveContext = (DroolsRuleEngineContextModel) ruleEngineContextDao
				.findRuleEngineContextByName("catmap01-live-context");
		catmap02LiveContext = (DroolsRuleEngineContextModel) ruleEngineContextDao
				.findRuleEngineContextByName("catmap02-live-context");
		catmap01PreviewContext = (DroolsRuleEngineContextModel) ruleEngineContextDao
				.findRuleEngineContextByName("catmap01-preview-context");
		catmap02PreviewContext = (DroolsRuleEngineContextModel) ruleEngineContextDao
				.findRuleEngineContextByName("catmap01-preview-context");
		assertThat(catmap01LiveContext).isNotNull();
		assertThat(catmap02LiveContext).isNotNull();
		assertThat(catmap01PreviewContext).isNotNull();
		assertThat(catmap02PreviewContext).isNotNull();
		// service layer seems to require a refresh for relations to be populated on both ends
		modelService.detachAll();
	}

	@Test
	public void testNullValues() throws Exception
	{
		final List<AbstractRuleEngineContextModel> contexts = strategy.findRuleEngineContexts(null, null);
		assertThat(contexts).isEmpty();
	}

	@Test
	public void testFindTwoValues() throws Exception
	{
		final CatalogVersionModel staged01 = catalogVersionService.getCatalogVersion("catmap01", "Staged");
		final CatalogVersionModel staged02 = catalogVersionService.getCatalogVersion("catmap02", "Staged");

		final List<AbstractRuleEngineContextModel> contexts = strategy.findRuleEngineContexts(Arrays.asList(staged01, staged02),
				null);
		assertThat(contexts).isNotEmpty();
		assertThat(contexts).hasSize(2);
		contexts.forEach(c -> assertThat(c).isInstanceOf(DroolsRuleEngineContextModel.class));
		assertThat(contexts).contains(catmap01PreviewContext);
		assertThat(contexts).contains(catmap02PreviewContext);
	}

	@Test
	public void testFindOneValue() throws Exception
	{
		final CatalogVersionModel staged01 = catalogVersionService.getCatalogVersion("catmap01", "Staged");
		final List<AbstractRuleEngineContextModel> contexts = strategy.findRuleEngineContexts(Collections.singletonList(staged01), null);
		assertThat(contexts).isNotEmpty();
		assertThat(contexts).hasSize(1);
		assertThat(contexts).contains(catmap01PreviewContext);
	}

	@Test
	public void testFindOneValueWithMatchingRuleType() throws Exception
	{
		final CatalogVersionModel staged01 = catalogVersionService.getCatalogVersion("catmap01", "Staged");
		final List<AbstractRuleEngineContextModel> contexts = strategy.findRuleEngineContexts(Collections.singletonList(staged01),
				RuleType.DEFAULT);
		assertThat(contexts).isNotEmpty();
		assertThat(contexts).hasSize(1);
		assertThat(contexts).contains(catmap01PreviewContext);
	}
}
