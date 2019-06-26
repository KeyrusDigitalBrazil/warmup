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
package de.hybris.platform.ruleengine.test;

import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.AbstractRulesModuleModel;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;


/**
 * Rule engine test supporting interface
 */
public interface RuleEngineTestSupportService
{
	/**
	 * factory method declaration to create new instance of AbstractRuleEngineRuleModel
	 *
	 * @return instance of AbstractRuleEngineRuleModel subclass
	 */
	AbstractRuleEngineRuleModel createRuleModel();

	/**
	 * create the rule module and associate the rules to it
	 *
	 * @param moduleName
	 *           - name of the module (or the related knowledge base)
	 * @param rules
	 *           - set of rules to associate the module with
	 *
	 * @return instance of AbstractRulesModuleModel subclass
	 */
	AbstractRulesModuleModel associateRulesToNewModule(String moduleName, Set<? extends AbstractRuleEngineRuleModel> rules);

	/**
	 * associate the rules to a module
	 *
	 * @param module
	 *           - rule module
	 *
	 * @param rules
	 *           - set of rules to associate the module with
	 *
	 */
	void associateRulesModule(AbstractRulesModuleModel module, Set<? extends AbstractRuleEngineRuleModel> rules);

	/**
	 * Creates the concrete subclass of AbstractRulesModuleModel, based on rule engine implementation
	 *
	 * @param abstractContext
	 *           instance of AbstractRuleEngineContextModel
	 * @param ruleModels
	 *           - a set of AbstractRuleEngineRuleModel instances
	 *
	 * @return instance of AbstractRulesModuleModel subclass (based on rule engine implementation)
	 *
	 */
	AbstractRulesModuleModel getTestRulesModule(AbstractRuleEngineContextModel abstractContext,
			Set<AbstractRuleEngineRuleModel> ruleModels);

	/**
	 * Returns a module, associated to a rule
	 *
	 * @param ruleModel
	 *           an instance of the AbstractRuleEngineRuleModel subclass
	 * @return Optional instance of AbstractRulesModuleModel
	 */
	Optional<AbstractRulesModuleModel> resolveAssociatedRuleModule(AbstractRuleEngineRuleModel ruleModel);

	/**
	 * Returns a Customer, adding additional functionality to AbstractRuleEngineRuleModel, based on parameters map
	 *
	 * @param params
	 *           Map of strings to strings, parametrizing additional functionality (e.g. global values in case of Drools
	 *           implementation)
	 *
	 * @return an instance of Customer for AbstractRuleEngineRuleModel
	 */
	Consumer<AbstractRuleEngineRuleModel> decorateRuleForTest(Map<String, String> params);

	/**
	 * Returns the name of the module, associated to the rule
	 *
	 * @param ruleModel
	 *           an instance of AbstractRuleEngineRuleModel or it's subclass
	 *
	 * @return name of the associated module, if applicable. Null otherwise
	 */
	String getTestModuleName(AbstractRuleEngineRuleModel ruleModel);
}
