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
package de.hybris.platform.ruleengineservices.init;


import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.init.BulkyTestDataLoader;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.util.DefaultRaoService;
import de.hybris.platform.ruleengineservices.rule.dao.RuleDao;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;


public abstract class AbstractSourceRulesAwareIT extends BulkyTestDataLoader<SourceRuleModel>
{

	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final String NUMBER_OF_SOURCE_RULES_TO_TEST = "ruleengineservices.test.sourcerules.amount";
	private static final String SOURCE_RULES_BASIC_IMPEX_PATH = "ruleengineservices.test.sourcerules.basic.impex.path";
	private static final String SOURCE_RULES_LOCALIZED_IMPEX_PATH = "ruleengineservices.test.sourcerules.localized.impex.path";
	private static final String RULES_DEFINITIONS_IMPEX_PATH = "ruleengineservices.test.sourcerules.ruledefinitions.impex.path";
	protected static final String TEST_SOURCE_RULE_CODE_PARAM = "ruleengineservices.test.sourcerule.code";
	protected static final String TEST_MODULE_NAME_PARAM = "ruleengineservices.test.module.name";
	protected static final String TEST_RULE_ENGINE_CONTEXT_PARAM = "ruleengineservices.test.ruleengine.context.name";

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSourceRulesAwareIT.class);

	@Resource
	private RuleDao ruleDao;
	@Resource
	private ModelService modelService;
	@Resource
	private EngineRuleDao engineRuleDao;

	protected String testSourceRuleCode;
	protected String testKieModuleName;
	protected String testRuleEngineContextName;

	private final DefaultRaoService raoService = new DefaultRaoService();

	protected List<SourceRuleModel> sampleRules;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		createHardwareCatalog();

		testKieModuleName = getConfigurationService().getConfiguration().getString(TEST_MODULE_NAME_PARAM);
		testSourceRuleCode = getConfigurationService().getConfiguration().getString(TEST_SOURCE_RULE_CODE_PARAM);
		testRuleEngineContextName = getConfigurationService().getConfiguration().getString(TEST_RULE_ENGINE_CONTEXT_PARAM);

		importCsv(getConfigurationService().getConfiguration().getString(RULES_DEFINITIONS_IMPEX_PATH), DEFAULT_ENCODING);
		importCsv(getConfigurationService().getConfiguration().getString(SOURCE_RULES_BASIC_IMPEX_PATH), DEFAULT_ENCODING);
		importCsv(getConfigurationService().getConfiguration().getString(SOURCE_RULES_LOCALIZED_IMPEX_PATH), DEFAULT_ENCODING);

		sampleRules = ruleDao.findAllActiveRules().stream().map(r -> (SourceRuleModel) r).collect(toList());
		final int numberOfSourceRules = getConfigurationService().getConfiguration().getInt(NUMBER_OF_SOURCE_RULES_TO_TEST);
		cloneSourceRules(numberOfSourceRules);
	}

	/**
	 * Clone the sample source rule to specified number of instances
	 *
	 * @param cloneFactor
	 * 		the clone factor (number of source rules to get as a result of clone)
	 */
	protected void cloneSourceRules(final int cloneFactor)
	{
		final List<SourceRuleModel> sourceRules = getRuleDao().findAllActiveRules().stream().map(r -> (SourceRuleModel) r)
				.collect(toList());
		if(CollectionUtils.isNotEmpty(sourceRules))
		{
			final SourceRuleModel seedSourceRule = sourceRules.get(0);
			final List<SourceRuleModel> rulesToSave = Lists.newArrayList();
			stopwatch.start();
			for (int i = 0; i < cloneFactor - sourceRules.size(); i++)
			{
				final SourceRuleModel clonedRule = modelService.clone(seedSourceRule);
				clonedRule.setName(getCodeForClonedRule(seedSourceRule.getName(), i));
				clonedRule.setCode(getCodeForClonedRule(seedSourceRule.getCode(), i));
				clonedRule.setUuid(UUID.randomUUID().toString());
				rulesToSave.add(clonedRule);
			}
			LOGGER.info("Cloning source rules finished in [{}]", stopwatch.stop().toString());
			loadData(rulesToSave);
		}
	}

	/**
	 * Update the cloned source rules for specified ids
	 *
	 * @param sourceRuleCode
	 * 		the sample source rule code
	 * @param cloneIds
	 * 		ids of the source rules to update
	 */
	protected void updateSourceRules(final String sourceRuleCode, final int... cloneIds)
	{
		for (final int cloneId : cloneIds)
		{
			final SourceRuleModel clonedRule = getRuleDao().findRuleByCode(getCodeForClonedRule(sourceRuleCode, cloneId));
			final String sourceRuleActions = clonedRule.getActions();
			final String newSourceRuleActions = modifyRuleActions(sourceRuleActions);
			clonedRule.setActions(newSourceRuleActions);
			getModelService().save(clonedRule);
		}
	}

	protected String modifyRuleActions(final String ruleActions)
	{
		return ruleActions.replaceAll("\\{\"USD\":60\\}", "{\"USD\":70}");
	}

	protected String getCodeForClonedRule(final String basicRuleCode, final int cloneOrder)
	{
		return basicRuleCode + "_" + cloneOrder;
	}

	protected void deleteRules(final String ruleCode, final int... cloneIds)
	{
		for (final int cloneId : cloneIds)
		{
			final String code = getCodeForClonedRule(ruleCode, cloneId);
			final SourceRuleModel clonedRule = getRuleDao().findRuleByCode(code);
			getModelService().remove(clonedRule);
			final AbstractRuleEngineRuleModel droolsRule = engineRuleDao.getRuleByCode(code, testKieModuleName);
			droolsRule.setActive(false);
			getModelService().save(droolsRule);
		}
	}

	protected List<SourceRuleModel> getSourceRulesSubset(final String ruleCode, final int... cloneIds)
	{
		final List<SourceRuleModel> sourceRulesSubset = Lists.newArrayList();
		for (final int cloneId : cloneIds)
		{
			final String code = getCodeForClonedRule(ruleCode, cloneId);
			final SourceRuleModel clonedRule = getRuleDao().findRuleByCode(code);
			if (nonNull(clonedRule))
			{
				sourceRulesSubset.add(clonedRule);
			}
		}
		return sourceRulesSubset;
	}

	protected CartRAO createCartRAO(final String code, final String currencyIso)
	{
		final CartRAO cart = raoService.createCart();
		cart.setCode(code);
		cart.setCurrencyIsoCode(currencyIso);
		return cart;
	}

	protected RuleDao getRuleDao()
	{
		return ruleDao;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	protected EngineRuleDao getEngineRuleDao()
	{
		return engineRuleDao;
	}
}
