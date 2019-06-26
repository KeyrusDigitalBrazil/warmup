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
package de.hybris.platform.ruleengine.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ruleengine.ExecutionContext;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.RuleEvaluationContext;
import de.hybris.platform.ruleengine.RuleEvaluationResult;
import de.hybris.platform.ruleengine.exception.DroolsInitializationException;
import de.hybris.platform.ruleengine.init.InitializationFuture;
import de.hybris.platform.ruleengine.init.RuleEngineKieModuleSwapper;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static de.hybris.platform.ruleengine.impl.DefaultPlatformRuleEngineService.MODULE_MVN_VERSION_NONE;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;


@IntegrationTest
public class DefaultPlatformRuleEngineServiceIT extends AbstractPlatformRuleEngineServiceIT
{

	private static final String TEST_MODULE_NAME = "ruleengine-test-module";
	private static final String TEST_BASE_NAME = "ruleengine-test-base";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private RuleEngineService platformRuleEngineService;
	@Resource
	private RuleEngineKieModuleSwapper ruleEngineKieModuleSwapper;

	@Before
	public void setUp() throws IOException, ImpExException
	{
		importCsv("/ruleengine/test/ruleenginesetup.impex", "utf-8");

		ruleTemplateContent = new String(
					 Files.readAllBytes(Paths.get(new ClassPathResource("/ruleengine/test/impl/drools_rule_template.drl").getURI())));
		ruleTemplateWrongContent = new String(
					 Files.readAllBytes(
								  Paths.get(new ClassPathResource("/ruleengine/test/impl/drools_rule_template_wrong.drl").getURI())));
	}

	@Test
	public void testInitializeOnStartupFail()
	{
		final DroolsKIEModuleModel rulesModule = createRulesForModule(TEST_MODULE_NAME, TEST_BASE_NAME, 100);
		createNewDroolsRule(UUID.randomUUID().toString(), "ruleengine-test-module_rule_WRONG", TEST_MODULE_NAME,
					 ruleTemplateWrongContent,
					 rulesModule.getKieBases().iterator().next());

		assertResultIsKo(platformRuleEngineService.initialize(singletonList(rulesModule), false, false), MODULE_MVN_VERSION_NONE);
		assertThat(rulesModule.getDeployedMvnVersion()).isNull();
	}

	@Test
	public void testInitializeOnStartupSingleModule()
	{
		final DroolsKIEModuleModel rulesModule = createRulesForModule(TEST_MODULE_NAME, TEST_BASE_NAME, 10);
		assertResultIsOk(platformRuleEngineService.initialize(singletonList(rulesModule), false, false), rulesModule);
		assertModuleModelIsOk(rulesModule);
	}

	@Test
	public void testInitializeOnStartupMultipleModules()
	{
		final DroolsKIEModuleModel rulesModule1 = createRulesForModule("ruleengine-test-module1", "ruleengine-test-base1", 100);
		final DroolsKIEModuleModel rulesModule2 = createRulesForModule("ruleengine-test-module2", "ruleengine-test-base2", 100);
		final DroolsKIEModuleModel rulesModule3 = createRulesForModule("ruleengine-test-module3", "ruleengine-test-base3", 100);

		final RuleEngineActionResult result1 = createRuleEngineActionResult();
		platformRuleEngineService.initializeNonBlocking(rulesModule1, null, true, false, result1);
		final RuleEngineActionResult result2 = createRuleEngineActionResult();
		platformRuleEngineService.initializeNonBlocking(rulesModule2, null, true, false, result2);
		final RuleEngineActionResult result3 = createRuleEngineActionResult();
		platformRuleEngineService.initializeNonBlocking(rulesModule3, null, true, false, result3);

		ruleEngineKieModuleSwapper.waitForSwappingToFinish();

		assertModuleModelIsOk(rulesModule1);
		assertModuleModelIsOk(rulesModule2);
		assertModuleModelIsOk(rulesModule3);

		assertResultIsOk(result1, rulesModule1);
		assertResultIsOk(result2, rulesModule2);
		assertResultIsOk(result3, rulesModule3);
	}

	@Test
	public void testInitializeSingleModuleUpdateWrongRule()
	{
		final DroolsKIEModuleModel rulesModule = createRulesForModule(TEST_MODULE_NAME, TEST_BASE_NAME, 100);
		assertResultIsOk(platformRuleEngineService.initialize(singletonList(rulesModule), false, false), rulesModule);

		createNewDroolsRule(UUID.randomUUID().toString(), "ruleengine-test-module_rule_WRONG", TEST_MODULE_NAME,
					 ruleTemplateWrongContent,
					 rulesModule.getKieBases().iterator().next());
		assertResultIsKo(platformRuleEngineService.initialize(singletonList(rulesModule), false, false),
					 rulesModule.getDeployedMvnVersion());
	}

	@Test
	public void testEvaluateRule()
	{
		final DroolsKIEModuleModel rulesModule = createRulesForModule(TEST_MODULE_NAME, TEST_BASE_NAME, 10);
		assertResultIsOk(platformRuleEngineService.initialize(singletonList(rulesModule), false, false), rulesModule);

		final RuleEvaluationContext ruleEvaluationContext = createRuleEvaluationContext(rulesModule);

		final RuleEvaluationResult evaluationResult = platformRuleEngineService.evaluate(ruleEvaluationContext);
		assertThat(evaluationResult).isNotNull();
	}

	@Test
	public void testEvaluateRuleRuleEngineNotInitialized()
	{
		final DroolsKIEModuleModel rulesModule = createRulesForModule(TEST_MODULE_NAME, TEST_BASE_NAME, 10);

		final RuleEvaluationContext ruleEvaluationContext = createRuleEvaluationContext(rulesModule);

		expectedException.expect(DroolsInitializationException.class);
		expectedException.expectMessage("Cannot complete the evaluation: rule engine was not initialized for releaseId [ruleengine-test:ruleengine-test-module:DUMMY_VERSION]");
		platformRuleEngineService.evaluate(ruleEvaluationContext);
	}

	@Test
	public void testEvaluateRuleWhileSwitchingKieModule()
	{
		final DroolsKIEModuleModel rulesModule = createRulesForModule(TEST_MODULE_NAME, TEST_BASE_NAME, 100);
		assertResultIsOk(platformRuleEngineService.initialize(singletonList(rulesModule), false, false), rulesModule);

		final String deployedMvnversion = rulesModule.getDeployedMvnVersion();

		createNewDroolsRule(UUID.randomUUID().toString(), "ruleengine-test-module_rule_NEW", TEST_MODULE_NAME, ruleTemplateContent,
					 rulesModule.getKieBases().iterator().next());
		final RuleEngineActionResult result = createRuleEngineActionResult();
		platformRuleEngineService.initializeNonBlocking(rulesModule, null, false, false, result);

		final RuleEvaluationContext ruleEvaluationContext = createRuleEvaluationContext(rulesModule);
		final RuleEvaluationResult evaluationResult = platformRuleEngineService.evaluate(ruleEvaluationContext);
		ruleEngineKieModuleSwapper.waitForSwappingToFinish();
		assertResultIsOk(result, rulesModule, deployedMvnversion);
		assertThat(evaluationResult).isNotNull();
	}

	@Test
	public void testInitializeRuleWhileSwitchingKieModule()
	{
		final DroolsKIEModuleModel rulesModule = createRulesForModule(TEST_MODULE_NAME, TEST_BASE_NAME, 100);
		final RuleEngineActionResult result = createRuleEngineActionResult();
		platformRuleEngineService.initializeNonBlocking(rulesModule, null, false, false, result);

		createNewDroolsRule(UUID.randomUUID().toString(), "ruleengine-test-module_rule_NEW", TEST_MODULE_NAME, ruleTemplateContent,
					 rulesModule.getKieBases().iterator().next());
		final RuleEngineActionResult result1 = createRuleEngineActionResult();
		expectedException.expect(DroolsInitializationException.class);
		expectedException.expectMessage("Kie container swapping is in progress, no rules updates are possible at this time");
		platformRuleEngineService.initializeNonBlocking(rulesModule, null, false, false, result1);
	}

	private void assertResultIsOk(final InitializationFuture initializationFuture, final DroolsKIEModuleModel module)
	{
		final List<RuleEngineActionResult> results = initializationFuture.waitForInitializationToFinish().getResults();
		assertThat(results).isNotEmpty();
		for (RuleEngineActionResult result : results)
		{
			assertResultIsOk(result, module, MODULE_MVN_VERSION_NONE);
		}
	}

	private void assertResultIsOk(final RuleEngineActionResult result, final DroolsKIEModuleModel module)
	{
		assertResultIsOk(result, module, MODULE_MVN_VERSION_NONE);
	}

	private void assertResultIsOk(final RuleEngineActionResult result, final DroolsKIEModuleModel module,
				 final String oldDeployedMvnVersion)
	{
		assertThat(result).isNotNull();
		assertThat(result.isActionFailed()).isFalse();
		assertThat(result.getDeployedVersion()).matches(module.getDeployedMvnVersion());
		assertThat(result.getOldVersion()).isEqualTo(oldDeployedMvnVersion);
		assertThat(result.getResults()).isNullOrEmpty();
	}

	private void assertResultIsKo(final InitializationFuture initializationFuture, final String deployedMvnVersion)
	{
		final List<RuleEngineActionResult> results = initializationFuture.waitForInitializationToFinish().getResults();
		assertThat(results).isNotEmpty();
		for (RuleEngineActionResult result : results)
		{
			assertThat(result).isNotNull();
			assertThat(result.isActionFailed()).isTrue();
			assertThat(result.getDeployedVersion()).isEqualTo(deployedMvnVersion);
			assertThat(result.getOldVersion()).isEqualTo(deployedMvnVersion);
			assertThat(result.getResults()).isNotEmpty();
		}
	}

	private void assertModuleModelIsOk(final DroolsKIEModuleModel module)
	{
		assertThat(module.getDeployedMvnVersion()).isEqualTo(INITIAL_MODULE_MVN_VERSION + "." + module.getVersion());
	}

	private RuleEngineActionResult createRuleEngineActionResult()
	{
		final RuleEngineActionResult result = new RuleEngineActionResult();
		result.setExecutionContext(new ExecutionContext());
		return result;
	}

	@After
	public void tearDown()
	{
		ruleEngineKieModuleSwapper.waitForSwappingToFinish();
	}

}
