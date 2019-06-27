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
package de.hybris.platform.sap.productconfig.rules.action.strategy.impl;

import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.sap.productconfig.rules.enums.ProductConfigRuleMessageSeverity;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.action.ProductConfigDisplayMessageRAO;
import de.hybris.platform.sap.productconfig.rules.strategies.mappers.impl.ProductConfigMessageRuleParameterValueMapper;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * Creates a message that can be shown on the UI.
 */
public class DisplayMessageRuleActionStrategyImpl extends ProductConfigAbstractRuleActionStrategy
{
	private ProductConfigMessageRuleParameterValueMapper messageValueMapper;

	@Override
	protected boolean executeAction(final ConfigModel model, final AbstractRuleActionRAO action,
			final Map<String, CsticModel> csticMap)
	{
		// Show message maintained as parameter of the Display Message action
		final ProductConfigDisplayMessageRAO displayMessageAction = (ProductConfigDisplayMessageRAO) action;
		final AbstractRuleEngineRuleModel rule = getRule(action);
		final String code = rule.getCode();
		final String params = rule.getRuleParameters();

		final String messageText = displayMessageAction.getMessage();
		final String localizedMessageText = extractLocalizedMessageText(messageText);
		final String messageTextResolved = resolveMessage(localizedMessageText, code, params);

		if (StringUtils.isNotEmpty(messageTextResolved))
		{
			final CsticRAO appliedToObject = (CsticRAO) action.getAppliedToObject();
			final String csticName = appliedToObject.getCsticName();

			final ProductConfigRuleMessageSeverity ruleMessageSeverity = displayMessageAction.getMessageSeverity();
			final ProductConfigMessageSeverity messageSeverity = mapSeverity(ruleMessageSeverity);

			String csticValueName = "";
			final CsticValueRAO valueNameForMessage = displayMessageAction.getValueNameForMessage();
			if (valueNameForMessage != null)
			{
				csticValueName = valueNameForMessage.getCsticValueName();
			}

			final ProductConfigMessage productConfigMessage = createMessage(code, messageTextResolved, messageSeverity);

			logMessageData(rule, csticName, csticValueName, productConfigMessage);
			showMessage(model, csticMap, rule, csticName, csticValueName, productConfigMessage);
		}
		return false;
	}

	protected String extractLocalizedMessageText(final String messageText)
	{
		final Map<Locale, String> messageMap = getMessageValueMapper().fromString(messageText);
		String localizedMessageText = messageMap.get(getI18NService().getCurrentLocale());
		if (StringUtils.isEmpty(localizedMessageText))
		{
			localizedMessageText = messageMap.get(Locale.ENGLISH);
		}
		return localizedMessageText;
	}

	@Override
	protected boolean isActionPossible(final ConfigModel model, final AbstractRuleActionRAO action,
			final Map<String, CsticModel> csticMap)
	{
		return true;
	}

	@Override
	protected ProductConfigMessage createMessage(final String code, final String messageFired,
			final ProductConfigMessageSeverity severity)
	{
		return getConfigModelFactory().createInstanceOfProductConfigMessage(messageFired, code, severity,
				ProductConfigMessageSource.RULE, ProductConfigMessageSourceSubType.DISPLAY_MESSAGE);
	}

	/**
	 * @return the messageValueMapper
	 */
	protected ProductConfigMessageRuleParameterValueMapper getMessageValueMapper()
	{
		return messageValueMapper;
	}

	/**
	 * @param messageValueMapper
	 *           the messageValueMapper to set
	 */
	public void setMessageValueMapper(final ProductConfigMessageRuleParameterValueMapper messageValueMapper)
	{
		this.messageValueMapper = messageValueMapper;
	}
}
