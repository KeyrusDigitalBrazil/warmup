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
import de.hybris.platform.ruleengine.RuleEvaluationContext;
import de.hybris.platform.ruleengine.enums.DroolsSessionType;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIESessionModel;
import de.hybris.platform.ruleengine.model.DroolsRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.UUID;

import javax.annotation.Resource;

import org.kie.api.runtime.rule.AgendaFilter;

import com.google.common.base.Preconditions;

import jdk.nashorn.internal.ir.annotations.Ignore;


@IntegrationTest
public class AbstractPlatformRuleEngineServiceIT extends ServicelayerTest
{

	protected static final String INITIAL_MODULE_MVN_VERSION = "1.0.0";
	protected String ruleTemplateContent;
	protected String ruleTemplateWrongContent;

	@Resource
	protected ModelService modelService;

	private DroolsKIEModuleModel createTestModule(final String moduleName)
	{
		final DroolsKIEModuleModel rulesModule = modelService.create(DroolsKIEModuleModel.class);
		rulesModule.setName(moduleName);
		rulesModule.setActive(true);
		rulesModule.setMvnGroupId("ruleengine-test");
		rulesModule.setMvnArtifactId(moduleName);
		rulesModule.setMvnVersion(INITIAL_MODULE_MVN_VERSION);
		rulesModule.setRuleType(RuleType.DEFAULT);
		rulesModule.setVersion(0L);
		modelService.save(rulesModule);
		return rulesModule;
	}

	private DroolsKIEBaseModel createTestKieBase(final String baseName, final DroolsKIEModuleModel module)
	{
		final DroolsKIEBaseModel kieBase = modelService.create(DroolsKIEBaseModel.class);
		kieBase.setName(baseName);
		kieBase.setKieModule(module);
		modelService.save(kieBase);

		final DroolsKIESessionModel kieSessionModel = modelService.create(DroolsKIESessionModel.class);
		kieSessionModel.setName("ruleengine-test-session");
		kieSessionModel.setKieBase(kieBase);
		kieSessionModel.setSessionType(DroolsSessionType.STATELESS);
		modelService.save(kieSessionModel);

		modelService.refresh(kieBase);

		return kieBase;
	}

	protected DroolsRuleModel createNewDroolsRule(final String ruleUuid, final String ruleCode, final String moduleName, final String ruleTemplateContent,
				 final DroolsKIEBaseModel kieBase)
	{
		final String ruleContent = ruleTemplateContent.replaceAll("\\$\\{rule_uuid\\}", ruleUuid)
					 .replaceAll("\\$\\{rule_code\\}", ruleCode).replaceAll("\\$\\{module_name\\}", moduleName);
		final DroolsRuleModel droolsRule = modelService.create(DroolsRuleModel.class);
		droolsRule.setUuid(ruleUuid);
		droolsRule.setCode(ruleCode);
		droolsRule.setRuleContent(ruleContent);
		droolsRule.setActive(true);
		droolsRule.setCurrentVersion(true);
		droolsRule.setRuleType(RuleType.DEFAULT);
		droolsRule.setKieBase(kieBase);
		modelService.save(droolsRule);
		return droolsRule;
	}

	protected DroolsKIEModuleModel createRulesForModule(final String moduleName, final String baseName, final int numOfRules)
	{
		Preconditions.checkArgument(numOfRules > 0, "The number of rules to generate should exceed 0");

		final DroolsKIEModuleModel rulesModule = createTestModule(moduleName);
		final DroolsKIEBaseModel kieBase = createTestKieBase(baseName, rulesModule);
		for (int i = 0; i < numOfRules; i++)
		{
			createNewDroolsRule(UUID.randomUUID().toString(), moduleName + "_rule" + i, moduleName, ruleTemplateContent, kieBase);
		}
		return rulesModule;
	}

	protected final RuleEvaluationContext createRuleEvaluationContext(final DroolsKIEModuleModel module)
	{
		final DroolsRuleEngineContextModel ruleEngineContextModel = modelService.create(DroolsRuleEngineContextModel.class);
		ruleEngineContextModel.setName("ruleengine-test-context");
		ruleEngineContextModel.setRuleFiringLimit(1L);
		ruleEngineContextModel.setKieSession(module.getKieBases().iterator().next().getKieSessions().iterator().next());

		final RuleEvaluationContext ruleEvaluationContext = new RuleEvaluationContext();
		ruleEvaluationContext.setRuleEngineContext(ruleEngineContextModel);
		ruleEvaluationContext.setFilter((AgendaFilter) match -> true);
		return ruleEvaluationContext;
	}

}
