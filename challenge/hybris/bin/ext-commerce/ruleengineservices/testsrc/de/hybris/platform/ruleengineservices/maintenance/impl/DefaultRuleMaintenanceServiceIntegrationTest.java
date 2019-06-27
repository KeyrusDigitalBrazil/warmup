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
package de.hybris.platform.ruleengineservices.maintenance.impl;

import com.google.common.collect.ImmutableSet;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerResult;
import de.hybris.platform.ruleengineservices.init.AbstractSourceRulesAwareIT;
import de.hybris.platform.ruleengineservices.maintenance.RuleCompilerPublisherResult;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@IntegrationTest
public class DefaultRuleMaintenanceServiceIntegrationTest extends AbstractSourceRulesAwareIT
{
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final String TEST_MODULE_NAME_LIVE = "promotions-module-junit";
	private static final String TEST_MODULE_NAME_PREVIEW = "preview-promotions-module-junit";
	private static final String SOURCE_RULES_BASIC_IMPEX_PATH = "ruleengineservices.test.sourcerules.basic.impex.path";
	private static final String SOURCE_RULES_LOCALIZED_IMPEX_PATH = "ruleengineservices.test.sourcerules.localized.impex.path";
	private static final String RULES_DEFINITIONS_IMPEX_PATH = "ruleengineservices.test.sourcerules.ruledefinitions.impex.path";


	@Resource
	private DefaultRuleMaintenanceService ruleMaintenanceService;
	@Resource
	private ConfigurationService configurationService;

	@Before
	@Override
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		createHardwareCatalog();

		importCsv("/ruleengine/test/ruleenginesetup.impex", "utf-8");
		importCsv(configurationService.getConfiguration().getString(RULES_DEFINITIONS_IMPEX_PATH), DEFAULT_ENCODING);
		importCsv(configurationService.getConfiguration().getString(SOURCE_RULES_BASIC_IMPEX_PATH), DEFAULT_ENCODING);
		importCsv(configurationService.getConfiguration().getString(SOURCE_RULES_LOCALIZED_IMPEX_PATH), DEFAULT_ENCODING);
	}

	@Test
	public void testCompileRules()
	{
		final List<SourceRuleModel> rules = getRuleDao().findAllActiveRules().stream().map(r -> (SourceRuleModel) r)
				.collect(toList());
		final List<RuleCompilerResult> ruleCompilerResults = ruleMaintenanceService.compileRules(rules, TEST_MODULE_NAME_LIVE)
				.getRuleCompilerResults();
		final List<AbstractRuleEngineRuleModel> activeRules = getEngineRuleDao().getActiveRules(TEST_MODULE_NAME_LIVE);
		assertThat(activeRules).hasSize(rules.size());
		assertThat(ruleCompilerResults.stream().filter(r -> !r.getResult().equals(RuleCompilerResult.Result.SUCCESS))).isEmpty();
	}

	@Test
	public void testCompileRulesRepeatedly()
	{
		List<SourceRuleModel> rules = getRuleDao().findAllActiveRules().stream().map(r -> (SourceRuleModel) r).collect(toList());
		ruleMaintenanceService.compileRules(rules, TEST_MODULE_NAME_LIVE);
		final List<RuleCompilerResult> ruleCompilerResults = ruleMaintenanceService.compileRules(rules, TEST_MODULE_NAME_LIVE)
				.getRuleCompilerResults();
		assertThat(ruleCompilerResults.stream().filter(r -> !r.getResult().equals(RuleCompilerResult.Result.SUCCESS))).isEmpty();
	}
	
	@Test
	public void testSourceRuleCodeReadonly()
	{
		List<SourceRuleModel> rules = getRuleDao().findAllActiveRules().stream().map(r -> (SourceRuleModel) r).collect(toList());
		final SourceRuleModel rule = rules.get(0);
		rule.setCode("TEST_RULE");
		assertThatThrownBy(() -> getModelService().save(rule)).hasCauseInstanceOf(JaloInvalidParameterException.class);
	}

	@Test
	public void testSynchronizeModulesNoOverlap()
	{
		cloneSourceRules(4);
		testSynchronizeModulesXLive(2, 0);
	}

	@Test
	public void testSynchronizeModulesFullOverlap()
	{
		sampleRules = getRuleDao().findAllActiveRules().stream().map(r -> (SourceRuleModel) r).collect(toList());
		cloneSourceRules(4);
		testSynchronizeModulesXLive(4, 4);
	}

	@Test
	public void testSynchronizeModulesPartialOverlap()
	{
		cloneSourceRules(4);
		testSynchronizeModulesXLive(3, 2);
	}

	@Test
	public void testSynchronizeModulesEmptySource()
	{
		cloneSourceRules(4);
		testSynchronizeModulesXLive(4, 0);
	}

	@Test
	public void testSynchronizeModulesEmptySourceEmptyTarget()
	{
		cloneSourceRules(4);
		testSynchronizeModulesXLive(0, -4);
	}

	@Test
	public void testSynchronizeModulesEmptyTarget()
	{
		cloneSourceRules(4);
		testSynchronizeModulesXLive(0, 0);
	}

	private void testSynchronizeModulesXLive(final long liveRulesNumber, final long previewOverlapShift)
	{
		List<SourceRuleModel> rules = getRuleDao().findAllActiveRules().stream().map(r -> (SourceRuleModel) r).collect(toList());
		final List<SourceRuleModel> rulesForLive = rules.stream().limit(liveRulesNumber).collect(toList());
		final List<SourceRuleModel> rulesForPreview = rules.stream().skip(liveRulesNumber - previewOverlapShift).collect(toList());

		ruleMaintenanceService.compileAndPublishRules(rulesForLive, TEST_MODULE_NAME_LIVE, false);
		ruleMaintenanceService.compileAndPublishRules(rulesForPreview, TEST_MODULE_NAME_PREVIEW, false);

		final Optional<RuleCompilerPublisherResult> ruleCompilerPublisherResult = ruleMaintenanceService
				.synchronizeModules(TEST_MODULE_NAME_PREVIEW, TEST_MODULE_NAME_LIVE);

		ruleCompilerPublisherResult
				.ifPresent(result -> assertThat(result.getResult()).isEqualByComparingTo(RuleCompilerPublisherResult.Result.SUCCESS));
		final Set<AbstractRuleEngineRuleModel> activeRulesForLive = ImmutableSet
				.copyOf(getEngineRuleDao().getActiveRules(TEST_MODULE_NAME_LIVE));
		final Set<AbstractRuleEngineRuleModel> activeRulesForPreview = ImmutableSet
				.copyOf(getEngineRuleDao().getActiveRules(TEST_MODULE_NAME_PREVIEW));
		assertThat(activeRulesForLive.size()).isEqualTo(activeRulesForPreview.size());
		assertThat(toRuleCodeList(activeRulesForLive)).containsAll(toRuleCodeList(activeRulesForPreview));
	}

	private List<String> toRuleCodeList(final Set<AbstractRuleEngineRuleModel> rules) // NOSONAR
	{
		return rules.stream().map(AbstractRuleEngineRuleModel::getCode).sorted().collect(toList());
	}

}
