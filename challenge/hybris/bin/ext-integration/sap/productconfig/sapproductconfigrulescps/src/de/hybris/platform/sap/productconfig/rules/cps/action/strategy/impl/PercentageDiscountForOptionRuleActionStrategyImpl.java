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
package de.hybris.platform.sap.productconfig.rules.cps.action.strategy.impl;

import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.sap.productconfig.rules.action.strategy.ProductConfigRuleActionStrategyChecker;
import de.hybris.platform.sap.productconfig.rules.cps.handler.impl.CharacteristicValueRulesResultHandlerImpl;
import de.hybris.platform.sap.productconfig.rules.cps.model.CharacteristicValueRulesResultModel;
import de.hybris.platform.sap.productconfig.rules.cps.model.DiscountMessageRulesResultModel;
import de.hybris.platform.sap.productconfig.rules.cps.rao.action.PercentageDiscountForOptionWithMessageRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * Prepare and persist Percentage Discount for Option relevant data
 */
public class PercentageDiscountForOptionRuleActionStrategyImpl extends DisplayPromoMessageRuleActionStrategyImpl
{
	private static final String ACTION_DESCRIPTION = "Percentage Discount for Option";

	private CharacteristicValueRulesResultHandlerImpl rulesResultHandler;

	@Override
	protected boolean executeAction(final ConfigModel model, final AbstractRuleActionRAO action,
			final Map<String, CsticModel> csticMap)
	{
		final PercentageDiscountForOptionWithMessageRAO percentageDiscountForOptionAction = (PercentageDiscountForOptionWithMessageRAO) action;
		final List<CharacteristicValueRulesResultModel> resultList = mapResult(percentageDiscountForOptionAction, csticMap);
		for (final CharacteristicValueRulesResultModel result : resultList)
		{
			getRulesResultHandler().mergeDiscountAndPersistResults(result, model.getId());
		}
		super.executeAction(model, action, csticMap);

		return true;
	}

	protected List<CharacteristicValueRulesResultModel> mapResult(
			final PercentageDiscountForOptionWithMessageRAO percentageDiscountForOptionAction,
			final Map<String, CsticModel> csticMap)
	{
		final CsticRAO appliedToObject = (CsticRAO) percentageDiscountForOptionAction.getAppliedToObject();
		final CsticValueRAO valueName = percentageDiscountForOptionAction.getValueName();

		if (StringUtils.isEmpty(valueName.getCsticValueName()))
		{
			final List<CsticValueModel> allValues = csticMap.get(appliedToObject.getCsticName()).getAssignableValues();
			final List<CharacteristicValueRulesResultModel> resultList = new ArrayList<>();
			for (final CsticValueModel valueModel : allValues)
			{
				final CharacteristicValueRulesResultModel result = getRulesResultHandler().createInstance();
				result.setCharacteristic(appliedToObject.getCsticName());
				result.setValue(valueModel.getName());
				result.setDiscountValue(percentageDiscountForOptionAction.getDiscountValue());
				resultList.add(result);
			}
			return resultList;
		}
		final CharacteristicValueRulesResultModel result = getRulesResultHandler().createInstance();
		result.setCharacteristic(appliedToObject.getCsticName());
		result.setValue(valueName.getCsticValueName());
		result.setDiscountValue(percentageDiscountForOptionAction.getDiscountValue());
		return Collections.singletonList(result);
	}

	@Override
	protected void afterMessageCreated(final String configId, final String csticName, final String csticValueName,
			final ProductConfigMessage productConfigMessage)
	{
		final DiscountMessageRulesResultModel message = mapMessage(productConfigMessage);
		getRulesResultHandler().addMessageToRulesResult(message, configId, csticName, csticValueName);
	}


	protected DiscountMessageRulesResultModel mapMessage(final ProductConfigMessage productConfigMessage)
	{
		final DiscountMessageRulesResultModel resultMessage = getRulesResultHandler().createMessageInstance();
		resultMessage.setMessage(productConfigMessage.getMessage());
		resultMessage.setEndDate(productConfigMessage.getEndDate());
		return resultMessage;
	}

	@Override
	protected boolean isActionPossible(final ConfigModel model, final AbstractRuleActionRAO action,
			final Map<String, CsticModel> csticMap)
	{
		final ProductConfigRuleActionStrategyChecker checker = getRuleActionChecker();
		boolean actionApplicable = checker.checkCsticPartOfModel(model, action, ACTION_DESCRIPTION, csticMap);
		if (actionApplicable)
		{
			final CsticModel csticModel = getCstic(model, action, csticMap);
			final String value = getValueToSet(action, csticModel);
			if (!StringUtils.isEmpty(value))
			{
				final CsticValueModel valueModel = getConfigModelFactory().createInstanceOfCsticValueModel(csticModel.getValueType());
				valueModel.setName(value);
				actionApplicable = csticModel.getAssignableValues().contains(valueModel);
			}
		}
		return actionApplicable;

	}

	protected String getValueToSet(final AbstractRuleActionRAO action, final CsticModel cstic)
	{
		String csticValueName = ((PercentageDiscountForOptionWithMessageRAO) action).getValueName().getCsticValueName();
		csticValueName = getRulesFormator().formatForService(cstic, csticValueName);
		return csticValueName;
	}

	protected CharacteristicValueRulesResultHandlerImpl getRulesResultHandler()
	{
		return rulesResultHandler;
	}

	public void setRulesResultHandler(final CharacteristicValueRulesResultHandlerImpl rulesResultHandler)
	{
		this.rulesResultHandler = rulesResultHandler;
	}
}
