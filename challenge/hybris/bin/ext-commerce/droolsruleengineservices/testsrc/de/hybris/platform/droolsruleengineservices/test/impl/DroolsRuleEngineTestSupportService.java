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
package de.hybris.platform.droolsruleengineservices.test.impl;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.fest.assertions.Assertions.assertThat;

import de.hybris.platform.ruleengine.dao.RulesModuleDao;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.AbstractRulesModuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengine.test.RuleEngineTestSupportService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Required;


public class DroolsRuleEngineTestSupportService implements RuleEngineTestSupportService
{
	private ModelService modelService;

	@Resource
	private RulesModuleDao rulesModuleDao;

	@Override
	public AbstractRuleEngineRuleModel createRuleModel()
	{
		final AbstractRuleEngineRuleModel ruleModel = getModelService().create(DroolsRuleModel.class);
		ruleModel.setRuleType(RuleType.DEFAULT);
		return ruleModel;
	}

	@Override
	public AbstractRulesModuleModel associateRulesToNewModule(final String moduleName,
			final Set<? extends AbstractRuleEngineRuleModel> rules)
	{
		DroolsKIEModuleModel rulesModule;
		try
		{
			rulesModule = (DroolsKIEModuleModel) rulesModuleDao.findByName(moduleName);
		}
		catch (final ModelNotFoundException e)
		{
			rulesModule = getModelService().create(DroolsKIEModuleModel.class);
			rulesModule.setActive(true);
			rulesModule.setName(moduleName);
			rulesModule.setVersion(-1L);
			rulesModule.setRuleType(RuleType.DEFAULT);
			rulesModule.setMvnArtifactId(moduleName);
			rulesModule.setMvnGroupId("yunit-mvn-group");
			rulesModule.setMvnVersion("1.0");

			getModelService().save(rulesModule);
		}

		final DroolsKIEBaseModel rulesBase = getModelService().create(DroolsKIEBaseModel.class);
		rulesBase.setName(moduleName);
		rulesBase.setKieModule(rulesModule);
		getModelService().save(rulesBase);

		rulesModule.setDefaultKIEBase(rulesBase);
		rulesBase.setKieModule(rulesModule);
		getModelService().save(rulesModule);

		rules.stream().map(r -> (DroolsRuleModel) r).forEach(r -> r.setKieBase(rulesBase));

		return rulesModule;
	}

	@Override
	public void associateRulesModule(final AbstractRulesModuleModel module, final Set<? extends AbstractRuleEngineRuleModel> rules)
	{
		checkState(module instanceof DroolsKIEModuleModel, "module must be of type DroolsKIEModuleModel");

		final DroolsKIEModuleModel droolsModule = (DroolsKIEModuleModel) module;
		final DroolsKIEBaseModel baseModel = droolsModule.getDefaultKIEBase();
		if (nonNull(baseModel))
		{
			baseModel.setRules((Set<DroolsRuleModel>) rules);
			if (isNotEmpty(rules))
			{
				rules.stream().forEach(r -> ((DroolsRuleModel) r).setKieBase(baseModel));
			}
		}
	}

	@Override
	public AbstractRulesModuleModel getTestRulesModule(final AbstractRuleEngineContextModel abstractContext,
			final Set<AbstractRuleEngineRuleModel> rules)
	{
		checkState(abstractContext instanceof DroolsRuleEngineContextModel,
				"ruleengine context must be of type DroolsRuleEngineContextModel");

		final Set<DroolsRuleModel> droolsSet = rules.stream().filter(r -> r instanceof DroolsRuleModel)
				.map(r -> (DroolsRuleModel) r).collect(Collectors.toSet());

		final DroolsRuleEngineContextModel context = (DroolsRuleEngineContextModel) abstractContext;
		final DroolsKIEBaseModel kieBase = context.getKieSession().getKieBase();
		kieBase.setRules(droolsSet);
		getModelService().saveAll();
		return context.getKieSession().getKieBase().getKieModule();
	}

	@Override
	public Optional<AbstractRulesModuleModel> resolveAssociatedRuleModule(final AbstractRuleEngineRuleModel ruleModel)
	{
		AbstractRulesModuleModel ruleModule = null;
		if (ruleModel instanceof DroolsRuleModel)
		{
			final DroolsRuleModel droolsRule = (DroolsRuleModel) ruleModel;
			assertThat(droolsRule.getKieBase()).isNotNull();
			assertThat(droolsRule.getKieBase().getKieModule()).isNotNull();
			ruleModule = droolsRule.getKieBase().getKieModule();
		}
		return Optional.ofNullable(ruleModule);
	}

	@Override
	public Consumer<AbstractRuleEngineRuleModel> decorateRuleForTest(final Map<String, String> params)
	{
		return r -> this.setGlobals(r, params);
	}

	@Override
	public String getTestModuleName(final AbstractRuleEngineRuleModel ruleModel)
	{
		if (ruleModel instanceof DroolsRuleModel)
		{
			return ((DroolsRuleModel) ruleModel).getKieBase().getKieModule().getName();
		}
		return null;
	}

	protected void setGlobals(final AbstractRuleEngineRuleModel ruleModel, final Map<String, String> globals)
	{
		if (ruleModel instanceof DroolsRuleModel)
		{
			((DroolsRuleModel) ruleModel).setGlobals(globals);
		}
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}


}
