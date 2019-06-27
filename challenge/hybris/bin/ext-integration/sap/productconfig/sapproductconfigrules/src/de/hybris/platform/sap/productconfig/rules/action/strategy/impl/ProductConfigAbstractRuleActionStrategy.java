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

import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.ruleengineservices.rule.services.RuleParametersService;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleConverterException;
import de.hybris.platform.sap.productconfig.rules.action.strategy.ProductConfigRuleActionStrategy;
import de.hybris.platform.sap.productconfig.rules.action.strategy.ProductConfigRuleActionStrategyChecker;
import de.hybris.platform.sap.productconfig.rules.enums.ProductConfigRuleMessageSeverity;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRuleFormatTranslator;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRuleUtil;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigModelFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;



/**
 * Abstract base class for all CPQ Rule Engine Strategy implementations.<br>
 * Contains some boiler plate code required for every action strategy implementation, logging utilities and
 * setter/getter for common bean dependencies.
 */
public abstract class ProductConfigAbstractRuleActionStrategy implements ProductConfigRuleActionStrategy
{

	private static final String EMPTY_STRING = "";
	private static final String RULE_UUID_END = "\\}";
	private static final String RULE_UUID_START = "\\{";

	private static final Logger LOG = Logger.getLogger(ProductConfigAbstractRuleActionStrategy.class);

	private ProductConfigRuleActionStrategyChecker ruleActionChecker;
	private ProductConfigRuleFormatTranslator rulesFormator;
	private RuleEngineService ruleEngineService;
	private RuleParametersService ruleParametersService;
	private ConfigModelFactory configModelFactory;
	private I18NService i18NService;

	private ProductConfigRuleUtil ruleUtil;

	private static final Pattern paramPattern = Pattern.compile(".*\\{[\\-a-f0-9]+\\}.*");


	@Override
	public boolean apply(final ConfigModel model, final AbstractRuleActionRAO action)
	{

		final Map<String, CsticModel> csticMap = getRuleUtil().getCsticMap(model);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Checking if Action '" + action + "' execution is possible.");
		}
		final boolean configChanged;
		if (!isActionPossible(model, action, csticMap))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Discarding Action '" + action + "', because action execution is not possible.");
			}
			configChanged = false;
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Executing Action: " + action);
			}
			configChanged = executeAction(model, action, csticMap);
			handleMessage(model, action, csticMap);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Action Execution resulted in configChange='" + configChanged + "' for action: " + action);
			}
		}
		return configChanged;
	}

	protected void handleMessage(final ConfigModel model, final AbstractRuleActionRAO action,
			final Map<String, CsticModel> csticMap)
	{
		final AbstractRuleEngineRuleModel rule = getRule(action);
		final String code = rule.getCode();

		final String params = rule.getRuleParameters();
		final String messageText = rule.getMessageFired(i18NService.getCurrentLocale());
		final String messageTextResolved = resolveMessage(messageText, code, params);

		final ProductConfigRuleMessageSeverity ruleMessageSeverity = rule.getMessageSeverity();
		final ProductConfigMessageSeverity messageSeverity = mapSeverity(ruleMessageSeverity);
		final String csticName = rule.getMessageForCstic();
		final ProductConfigMessage productConfigMessage = createMessage(code, messageTextResolved, messageSeverity);

		logMessageData(rule, csticName, null, productConfigMessage);
		showMessage(model, csticMap, rule, csticName, null, productConfigMessage);
	}

	protected AbstractRuleEngineRuleModel getRule(final AbstractRuleActionRAO action)
	{
		final String code = action.getFiredRuleCode();
		final String moduleName = action.getModuleName();
		return getRuleEngineService().getRuleForCodeAndModule(code, moduleName);
	}

	protected String resolveMessage(final String messageFired, final String code, final String params)
	{
		String messageFiredResolved = messageFired;
		if (StringUtils.isNotEmpty(messageFired) && messageContainsParameters(messageFired))
		{
			try
			{
				final List<RuleParameterData> paramList = getRuleParametersService().convertParametersFromString(params);
				messageFiredResolved = replaceMessageParameters(messageFired, paramList);
			}
			catch (final RuleConverterException ex)
			{
				LOG.debug("Failed to parse rule message parmetrs. ", ex);
				LOG.error("Failed to parse rule message parmetrs, due to '" + ex.getMessage() + "' see debug log for details.");
			}
			if (messageContainsParameters(messageFiredResolved))
			{
				LOG.error("Could not resolve all parameters of message. Please check in backoffice for rule '" + code
						+ "' whether the given UUID's are correct for message '" + messageFiredResolved + "'.");
			}
		}
		return messageFiredResolved;
	}

	protected CsticValueModel retrieveCsticValueModel(final String ruleMessageForCsticValue, final CsticModel cstic)
	{
		CsticValueModel csticValue = null;
		if (StringUtils.isNotEmpty(ruleMessageForCsticValue))
		{
			for (final CsticValueModel assignableValue : cstic.getAssignableValues())
			{
				if (assignableValue.getName().equals(ruleMessageForCsticValue))
				{
					csticValue = assignableValue;
					break;
				}
			}
		}
		return csticValue;
	}


	protected void logMessageData(final AbstractRuleEngineRuleModel rule, final String csticName, final String csticValueName,
			final ProductConfigMessage productConfigMessage)
	{
		if (LOG.isDebugEnabled())
		{
			final String msgTempl = "Creating Rule Message with data: [ruleCode='%s', csticName='%s', csticValueName='%s', msgText='%s', extendedMsgText='%s', ruleParams='%s' severity='%s']";
			LOG.debug(String.format(msgTempl, rule.getCode(), csticName, csticValueName, productConfigMessage.getMessage(),
					productConfigMessage.getExtendedMessage(), rule.getRuleParameters(), productConfigMessage.getSeverity()));
		}
	}

	protected ProductConfigMessageSeverity mapSeverity(final ProductConfigRuleMessageSeverity ruleMessageSeverity)
	{
		final ProductConfigMessageSeverity severity;
		if (ruleMessageSeverity != null)
		{
			switch (ruleMessageSeverity)
			{
				case WARNING:
					severity = ProductConfigMessageSeverity.WARNING;
					break;
				case INFO:
					severity = ProductConfigMessageSeverity.INFO;
					break;
				default:
					severity = ProductConfigMessageSeverity.INFO;
					break;
			}
		}
		else
		{
			severity = ProductConfigMessageSeverity.INFO;
		}

		return severity;
	}

	protected boolean messageContainsParameters(final String messageFired)
	{
		return paramPattern.matcher(messageFired).matches();
	}

	protected String replaceMessageParameters(final String messageFired, final List<RuleParameterData> paramList)
	{
		String replacedMassage = messageFired;
		for (final RuleParameterData ruleParam : paramList)
		{
			final Pattern pattern = Pattern.compile(RULE_UUID_START + ruleParam.getUuid() + RULE_UUID_END);
			final String valueString = ruleParam.getValue() == null ? EMPTY_STRING : ruleParam.getValue().toString();
			replacedMassage = pattern.matcher(replacedMassage).replaceAll(valueString);
		}
		return replacedMassage;
	}

	protected ProductConfigMessage createMessage(final String code, final String messageFired,
			final ProductConfigMessageSeverity severity)
	{
		return getConfigModelFactory().createInstanceOfProductConfigMessage(messageFired, code, severity,
				ProductConfigMessageSource.RULE);
	}

	protected CsticModel getCstic(final ConfigModel model, final AbstractRuleActionRAO action,
			final Map<String, CsticModel> csticMap)
	{
		return getRuleActionChecker().getCstic(model, action, csticMap);
	}

	protected ProductConfigRuleActionStrategyChecker getRuleActionChecker()
	{
		return ruleActionChecker;
	}

	/**
	 * @param ruleActionChecker
	 */
	@Required
	public void setRuleActionChecker(final ProductConfigRuleActionStrategyChecker ruleActionChecker)
	{
		this.ruleActionChecker = ruleActionChecker;
	}

	protected ProductConfigRuleFormatTranslator getRulesFormator()
	{
		return rulesFormator;
	}

	/**
	 * @param rulesFormator
	 */
	@Required
	public void setRulesFormator(final ProductConfigRuleFormatTranslator rulesFormator)
	{
		this.rulesFormator = rulesFormator;
	}

	protected RuleEngineService getRuleEngineService()
	{
		return ruleEngineService;
	}

	/**
	 * @param ruleEngineService
	 */
	@Required
	public void setRuleEngineService(final RuleEngineService ruleEngineService)
	{
		this.ruleEngineService = ruleEngineService;
	}

	protected ConfigModelFactory getConfigModelFactory()
	{
		return this.configModelFactory;
	}

	/**
	 * @param configModelFactory
	 */
	@Required
	public void setConfigModelFactory(final ConfigModelFactory configModelFactory)
	{
		this.configModelFactory = configModelFactory;
	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	/**
	 * @param i18NService
	 */
	public void setI18NService(final I18NService i18NService)
	{
		this.i18NService = i18NService;
	}

	protected RuleParametersService getRuleParametersService()
	{
		return ruleParametersService;
	}

	/**
	 * @param ruleParametersService
	 */
	@Required
	public void setRuleParametersService(final RuleParametersService ruleParametersService)
	{
		this.ruleParametersService = ruleParametersService;
	}

	protected ProductConfigRuleUtil getRuleUtil()
	{
		return ruleUtil;
	}

	/**
	 * @param ruleUtil
	 */
	@Required
	public void setRuleUtil(final ProductConfigRuleUtil ruleUtil)
	{
		this.ruleUtil = ruleUtil;
	}

	protected abstract boolean executeAction(ConfigModel model, AbstractRuleActionRAO action, Map<String, CsticModel> csticMap);

	protected abstract boolean isActionPossible(final ConfigModel model, final AbstractRuleActionRAO action,
			Map<String, CsticModel> csticMap);


	protected void showMessage(final ConfigModel model, final Map<String, CsticModel> csticMap,
			final AbstractRuleEngineRuleModel rule, final String ruleMessageForCstic, final String ruleMessageForCsticValue,
			final ProductConfigMessage productConfigMessage)

	{
		final String code = rule.getCode();
		CsticModel cstic = null;
		CsticValueModel csticValue = null;

		if (StringUtils.isNotEmpty(ruleMessageForCstic))
		{
			final String ruleMessageForCsticUpperCase = ruleMessageForCstic.toUpperCase(Locale.ENGLISH);
			cstic = csticMap.get(ruleMessageForCsticUpperCase);

			csticValue = retrieveCsticValueModelForRuleMessage(model, ruleMessageForCsticValue, code, cstic,
					ruleMessageForCsticUpperCase);
		}
		addMessageToConfigModel(model, ruleMessageForCstic, ruleMessageForCsticValue, productConfigMessage, cstic, csticValue);
	}

	protected void addMessageToConfigModel(final ConfigModel model, final String ruleMessageForCstic,
			final String ruleMessageForCsticValue, final ProductConfigMessage productConfigMessage, final CsticModel cstic,
			final CsticValueModel csticValue)
	{
		if (StringUtils.isNotEmpty(productConfigMessage.getMessage()))
		{
			if (csticValue != null)
			{
				addMessageToCsticValue(productConfigMessage, cstic, csticValue);
			}
			else if (cstic != null && StringUtils.isEmpty(ruleMessageForCsticValue))
			{
				final Set<ProductConfigMessage> messages = cstic.getMessages();
				messages.add(productConfigMessage);
				cstic.setMessages(messages);
			}
			else if (StringUtils.isEmpty(ruleMessageForCstic) && StringUtils.isEmpty(ruleMessageForCsticValue))
			{
				final Set<ProductConfigMessage> messages = model.getMessages();
				messages.add(productConfigMessage);
				model.setMessages(messages);
			}
		}
	}

	protected void addMessageToCsticValue(final ProductConfigMessage productConfigMessage, final CsticModel cstic,
			final CsticValueModel csticValue)
	{
		final Set<ProductConfigMessage> messages = csticValue.getMessages();
		messages.add(productConfigMessage);
		csticValue.setMessages(messages);
		final Optional<CsticValueModel> assignedValue = cstic.getAssignedValues().stream()
				.filter(value -> value.getName().equals(csticValue.getName())).findFirst();
		if (assignedValue.isPresent())
		{
			assignedValue.get().setMessages(messages);
		}
	}

	protected CsticValueModel retrieveCsticValueModelForRuleMessage(final ConfigModel model, final String ruleMessageForCsticValue,
			final String code, final CsticModel cstic, final String ruleMessageForCsticUpperCase)
	{
		CsticValueModel csticValue = null;

		if (cstic != null)
		{
			csticValue = retrieveCsticValueModel(ruleMessageForCsticValue, cstic);
			if (StringUtils.isNotEmpty(ruleMessageForCsticValue) && csticValue == null)
			{
				LOG.error("Rule with code " + code + " tries to assign a message to the characteristic / value "
						+ ruleMessageForCsticUpperCase + " / " + ruleMessageForCsticValue + " of the model " + model.getName()
						+ " . However the model does not contain the characteristic with the required value.");
			}
		}
		else
		{
			LOG.error("Rule with code " + code + " tries to assign a message to the characteristic " + ruleMessageForCsticUpperCase
					+ " of the model " + model.getName() + " . However the model does not contain the required characteristic.");
		}
		return csticValue;
	}
}
