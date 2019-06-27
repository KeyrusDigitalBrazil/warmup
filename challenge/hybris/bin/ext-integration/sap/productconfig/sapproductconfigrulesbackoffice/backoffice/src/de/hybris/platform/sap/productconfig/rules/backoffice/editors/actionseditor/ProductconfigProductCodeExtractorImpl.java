/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.rules.backoffice.editors.actionseditor;

import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.ruleengineservices.rule.services.RuleConditionsRegistry;
import de.hybris.platform.ruleengineservices.rule.services.RuleConditionsService;
import de.hybris.platform.sap.productconfig.rules.backoffice.constants.SapproductconfigrulesbackofficeConstants;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Default implementation of the product codes list provider for products used in "Currently configuring product"
 * conditions in a product configuration rule
 */
public class ProductconfigProductCodeExtractorImpl implements ProductconfigProductCodeExtractor
{

	private RuleConditionsRegistry ruleConditionsRegistry;

	private RuleConditionsService ruleConditionsService;


	@Override
	public List<String> retrieveProductCodeList(final ProductConfigSourceRuleModel ruleModel)
	{
		final List<String> products = new ArrayList<String>();
		final Class<?> ruleType = ruleModel.getClass();
		final String conditions = ruleModel.getConditions();

		final Map<String, RuleConditionDefinitionData> conditionDefinitions = ruleConditionsRegistry
				.getConditionDefinitionsForRuleTypeAsMap(ruleType);
		final List<RuleConditionData> conditionDataList = ruleConditionsService.convertConditionsFromString(conditions,
				conditionDefinitions);

		for (final RuleConditionData conditionData : conditionDataList)
		{
			if (SapproductconfigrulesbackofficeConstants.CONDITION_DEFINITION_ID_CURRENT_CONFIG_PRODUCT
					.equalsIgnoreCase(conditionData.getDefinitionId()))
			{
				final Map<String, RuleParameterData> parameters = conditionData.getParameters();
				final RuleParameterData productParameter = parameters.get(SapproductconfigrulesbackofficeConstants.PARAMETER_PRODUCT);
				if (productParameter != null && !products.contains(productParameter.getValue()))
				{
					products.add(productParameter.getValue());
				}
			}
		}
		return products;
	}

	/**
	 * @return the ruleConditionsRegistry
	 */
	protected RuleConditionsRegistry getRuleConditionsRegistry()
	{
		return ruleConditionsRegistry;
	}

	/**
	 * @param ruleConditionsRegistry
	 *           the ruleConditionsRegistry to set
	 */
	public void setRuleConditionsRegistry(final RuleConditionsRegistry ruleConditionsRegistry)
	{
		this.ruleConditionsRegistry = ruleConditionsRegistry;
	}

	/**
	 * @return the ruleConditionsService
	 */
	protected RuleConditionsService getRuleConditionsService()
	{
		return ruleConditionsService;
	}

	/**
	 * @param ruleConditionsService
	 *           the ruleConditionsService to set
	 */
	public void setRuleConditionsService(final RuleConditionsService ruleConditionsService)
	{
		this.ruleConditionsService = ruleConditionsService;
	}
}
