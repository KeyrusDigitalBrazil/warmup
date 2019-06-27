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

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.PerformanceTest;
import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.RuleEvaluationContext;
import de.hybris.platform.ruleengine.RuleEvaluationResult;
import de.hybris.platform.ruleengine.dao.RuleEngineContextDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerResult;
import de.hybris.platform.ruleengineservices.init.AbstractSourceRulesAwareIT;
import de.hybris.platform.ruleengineservices.maintenance.RuleCompilerPublisherResult;
import de.hybris.platform.ruleengineservices.maintenance.RuleMaintenanceService;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;


@PerformanceTest
public class DefaultRuleMaintenanceServicePerformanceTest extends AbstractSourceRulesAwareIT
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRuleMaintenanceServicePerformanceTest.class);
	private static final String TEST_CURRENCY_CODE = "USD";

	@Resource
	private RuleMaintenanceService ruleMaintenanceService;
	@Resource
	private RuleEngineContextDao ruleEngineContextDao;
	@Resource
	private RuleEngineService commerceRuleEngineService;

	@Test
	public void testCompileRulesAsync()
	{
		final List<SourceRuleModel> rules = getRuleDao().findAllActiveRules().stream().map(r -> (SourceRuleModel) r)
				.collect(toList());
		stopwatch.start();
		final List<RuleCompilerResult> ruleCompilerResults = ((DefaultRuleMaintenanceService) ruleMaintenanceService)
				.compileRules(rules, testKieModuleName).getRuleCompilerResults();
		LOGGER.info("Elapsed time for testCompileRulesAsync: {}", stopwatch.stop().toString());
		final List<AbstractRuleEngineRuleModel> activeRules = getEngineRuleDao().getActiveRules(testKieModuleName);
		assertThat(activeRules).hasSize(rules.size());
		assertThat(ruleCompilerResults.stream().filter(r -> !r.getResult().equals(RuleCompilerResult.Result.SUCCESS))).isEmpty();
	}

	@Test
	public void testCompileAndDeployRulesAsync()
	{
		final List<SourceRuleModel> rules = getRuleDao().findAllActiveRules().stream().map(r -> (SourceRuleModel) r)
				.collect(toList());
		stopwatch.start();
		final RuleCompilerPublisherResult ruleCompilerPublisherResult = ruleMaintenanceService
				.compileAndPublishRules(rules, testKieModuleName, false);
		LOGGER.info("Elapsed time for testCompileAndDeployRulesAsync: {}", stopwatch.stop().toString());
		assertThat(ruleCompilerPublisherResult).isNotNull();
		assertThat(ruleCompilerPublisherResult.getResult()).isEqualByComparingTo(RuleCompilerPublisherResult.Result.SUCCESS);
	}

	@Test
	public void testIncrementalUpdateRulesAsync()
	{
		final List<SourceRuleModel> rules = getRuleDao().findAllActiveRules().stream().map(r -> (SourceRuleModel) r)
				.collect(toList());
		stopwatch.start();
		ruleMaintenanceService.compileAndPublishRules(rules, testKieModuleName, true);
		LOGGER.info("Elapsed time for testIncrementalUpdateRulesAsync, step 1 (full init): {}", stopwatch.stop().toString());
		stopwatch.reset();
		updateSourceRules(testSourceRuleCode, 1, 2, 3, 4, 5);

		final List<SourceRuleModel> rulesToUpdate = getRuleDao().findAllActiveRules().stream().map(r -> (SourceRuleModel) r)
				.collect(toList());
		stopwatch.start();
		final RuleCompilerPublisherResult ruleCompilerPublisherResultAfterUpdate = ruleMaintenanceService
				.compileAndPublishRules(rulesToUpdate, testKieModuleName, true);

		LOGGER.info("Elapsed time for testIncrementalUpdateRulesAsync, step 2 (incremental update of 5 rules): {}",
				stopwatch.stop().toString());
		assertThat(ruleCompilerPublisherResultAfterUpdate).isNotNull();
		assertThat(ruleCompilerPublisherResultAfterUpdate.getResult())
				.isEqualByComparingTo(RuleCompilerPublisherResult.Result.SUCCESS);
	}

	@Test
	public void testIncrementalDeleteRulesAsync()
	{
		final List<SourceRuleModel> rules = getRuleDao().findAllActiveRules().stream().map(r -> (SourceRuleModel) r)
				.collect(toList());
		stopwatch.start();
		ruleMaintenanceService.compileAndPublishRules(rules, testKieModuleName, true);
		LOGGER.info("Elapsed time for testIncrementalDeleteRulesAsync, step 1 (full init): {}", stopwatch.stop().toString());
		stopwatch.reset();
		deleteRules(testSourceRuleCode, 1, 2, 3, 4, 5);

		final List<SourceRuleModel> rulesToUpdate = getRuleDao().findAllActiveRules().stream().map(r -> (SourceRuleModel) r)
				.collect(toList());
		stopwatch.start();
		final RuleCompilerPublisherResult ruleCompilerPublisherResultAfterUpdate = ruleMaintenanceService
				.compileAndPublishRules(rulesToUpdate, testKieModuleName, true);

		LOGGER.info("Elapsed time for testIncrementalDeleteRulesAsync, step 2 (incremental delete of 5 rules): {}",
				stopwatch.stop().toString());
		assertThat(ruleCompilerPublisherResultAfterUpdate).isNotNull();
		assertThat(ruleCompilerPublisherResultAfterUpdate.getResult())
				.isEqualByComparingTo(RuleCompilerPublisherResult.Result.SUCCESS);
	}

	@Test
	public void testUndeployRulesAsync()
	{
		final List<SourceRuleModel> rules = getRuleDao().findAllActiveRules().stream().map(r -> (SourceRuleModel) r)
				.collect(toList());
		stopwatch.start();
		ruleMaintenanceService.compileAndPublishRules(rules, testKieModuleName, true);
		LOGGER.info("Elapsed time for testUndeployRulesAsync, step 1 (full init): {}", stopwatch.stop().toString());
		stopwatch.reset();

		final List<SourceRuleModel> rulesToUndeploy = getSourceRulesSubset(testSourceRuleCode, 1, 2, 3, 4, 5);
		stopwatch.start();
		final Optional<RuleCompilerPublisherResult> ruleCompilerPublisherResultAfterUpdate = ruleMaintenanceService
				.undeployRules(rulesToUndeploy, testKieModuleName);

		LOGGER.info("Elapsed time for testUndeployRulesAsync, step 2 (incremental undeployment of 5 rules): {}",
				stopwatch.stop().toString());
		assertThat(ruleCompilerPublisherResultAfterUpdate).isPresent();
		assertThat(ruleCompilerPublisherResultAfterUpdate.get().getResult())
				.isEqualByComparingTo(RuleCompilerPublisherResult.Result.SUCCESS);
		final List<AbstractRuleEngineRuleModel> activeRules = getEngineRuleDao().getActiveRules(testKieModuleName);
		assertThat(activeRules).hasSize(rules.size() - rulesToUndeploy.size());
	}

	@Test
	public void testRuleEvaluationAfterUpdate()
	{
		final List<SourceRuleModel> rules = getRuleDao().findAllActiveRules().stream().map(r -> (SourceRuleModel) r)
				.collect(toList());
		stopwatch.start();
		ruleMaintenanceService.compileAndPublishRules(rules, testKieModuleName, false);
		LOGGER.info("Elapsed time for testRuleEvaluationAfterUpdate, step 1 (full init): {}", stopwatch.stop().toString());
		stopwatch.reset();

		stopwatch.start();
		RuleEvaluationResult ruleEvaluationResult = evaluate(Collections.singleton(createCartRAO("123", TEST_CURRENCY_CODE)));
		LOGGER.info("Elapsed time for testRuleEvaluationAfterUpdate, step 2 (evaluation of 1 rule): {}",
				stopwatch.stop().toString());
		stopwatch.reset();
		assertThat(ruleEvaluationResult).isNotNull();
		assertThat(ruleEvaluationResult.isEvaluationFailed()).isFalse();
		RuleEngineResultRAO result = ruleEvaluationResult.getResult();
		assertThat(result).isNotNull();

		stopwatch.start();
		ruleEvaluationResult = evaluate(Collections.singleton(createCartRAO("456", TEST_CURRENCY_CODE)));
		LOGGER.info("Elapsed time for testRuleEvaluationAfterUpdate, step 3 (2-nd evaluation of 1 rule): {}",
				stopwatch.stop().toString());
		stopwatch.reset();
		assertThat(ruleEvaluationResult).isNotNull();
		assertThat(ruleEvaluationResult.isEvaluationFailed()).isFalse();
		result = ruleEvaluationResult.getResult();
		assertThat(result).isNotNull();

		stopwatch.start();
		for (int i = 0; i < 10; i++)
		{
			evaluate(Collections.singleton(createCartRAO("123" + i, TEST_CURRENCY_CODE)));
		}
		LOGGER.info("Elapsed time for testRuleEvaluationAfterUpdate, step 4 (repetitive 10 evaluations): {}",
				stopwatch.stop().toString());
		stopwatch.reset();
	}

	@Test
	public void testSynchronizeRulesModulesNoOverlap()
	{
		testSynchronizeRulesModulesOverlapXPercent(0.0);
	}

	@Test
	public void testSynchronizeRulesModulesOverlap30Percent()
	{
		testSynchronizeRulesModulesOverlapXPercent(0.3);
	}

	@Test
	public void testSynchronizeRulesModulesOverlap90Percent()
	{
		testSynchronizeRulesModulesOverlapXPercent(0.9);
	}

	@Test
	public void testSynchronizeRulesModulesFullOverlap()
	{
		testSynchronizeRulesModulesOverlapXPercent(1.0);
	}
	
	private void testSynchronizeRulesModulesOverlapXPercent(final double overlapPercentage)
	{
		final List<SourceRuleModel> rules = getRuleDao().findAllActiveRules().stream().map(r -> (SourceRuleModel) r)
				.collect(toList());
		final int firstPartSize = rules.size() / 2;
		final List<SourceRuleModel> rulesForPreview = rules.stream().limit(firstPartSize).collect(toList());
		final List<SourceRuleModel> rulesForLive = rules.stream().skip((long) ((1.0 - overlapPercentage) * firstPartSize)).limit(firstPartSize)
				.collect(toList());

		assertThat(rulesForPreview.size()).isEqualTo(rulesForLive.size());

		stopwatch.start();
		ruleMaintenanceService.compileAndPublishRules(rulesForLive, "promotions-module-junit", false);
		LOGGER.info("Elapsed time for testSynchronizeRulesModulesOverlapXPercent with overlap {}, step 1 (rules compilation for live module): {}",
				overlapPercentage, stopwatch.stop().toString());
		stopwatch.reset();

		stopwatch.start();
		ruleMaintenanceService.compileAndPublishRules(rulesForPreview, "preview-promotions-module-junit", false);
		LOGGER.info(
				"Elapsed time for testSynchronizeRulesModulesOverlapXPercent with overlap {}, step 2 (rules compilation for preview module): {}",
				overlapPercentage, stopwatch.stop().toString());
		stopwatch.reset();

		stopwatch.start();
		final Optional<RuleCompilerPublisherResult> ruleCompilerPublisherResult = ruleMaintenanceService
				.synchronizeModules("preview-promotions-module-junit", "promotions-module-junit");
		LOGGER.info(
				"Elapsed time for testSynchronizeRulesModulesOverlapXPercent with overlap {}, step 3 (rules sync from preview module to live module): {}",
				overlapPercentage, stopwatch.stop().toString());
		stopwatch.reset();

		if(overlapPercentage < 1.0)
		{
			assertThat(ruleCompilerPublisherResult).isPresent();
			assertThat(ruleCompilerPublisherResult.get().getResult())
					.isEqualByComparingTo(RuleCompilerPublisherResult.Result.SUCCESS);
		}
		final Set<AbstractRuleEngineRuleModel> activeRulesForLive = ImmutableSet
				.copyOf(getEngineRuleDao().getActiveRules("promotions-module-junit"));
		final Set<AbstractRuleEngineRuleModel> activeRulesForPreview = ImmutableSet
				.copyOf(getEngineRuleDao().getActiveRules("preview-promotions-module-junit"));
		assertThat(activeRulesForLive.size()).isEqualTo(activeRulesForPreview.size());
		assertThat(toRuleCodeList(activeRulesForLive)).containsAll(toRuleCodeList(activeRulesForPreview));
	}

	private List<String> toRuleCodeList(final Set<AbstractRuleEngineRuleModel> rules)
	{
		return rules.stream().map(AbstractRuleEngineRuleModel::getCode).sorted().collect(toList());
	}

	protected RuleEvaluationResult evaluate(final Set<Object> facts)
	{
		final RuleEvaluationContext context = prepareContext(facts);
		return commerceRuleEngineService.evaluate(context);
	}

	protected RuleEvaluationContext prepareContext(final Set<Object> facts)
	{
		final RuleEvaluationContext context = new RuleEvaluationContext();
		context.setFacts(facts);
		context.setRuleEngineContext(ruleEngineContextDao.findRuleEngineContextByName(testRuleEngineContextName));
		return context;
	}
}
