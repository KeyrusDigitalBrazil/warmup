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

import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.sap.productconfig.rules.action.strategy.impl.ProductConfigAbstractRuleActionStrategy;
import de.hybris.platform.sap.productconfig.rules.cps.rao.action.ProductConfigPromoMessageRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.strategies.mappers.impl.ProductConfigMessageRuleParameterValueMapper;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ProductConfigMessageBuilder;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Creates a promo message that can be shown on the UI.
 */
public class DisplayPromoMessageRuleActionStrategyImpl extends ProductConfigAbstractRuleActionStrategy
{
	private static final Logger LOG = Logger.getLogger(DisplayPromoMessageRuleActionStrategyImpl.class);

	private ProductConfigMessageRuleParameterValueMapper messageValueMapper;

	@Override
	protected boolean executeAction(final ConfigModel model, final AbstractRuleActionRAO action,
			final Map<String, CsticModel> csticMap)
	{
		// Show message maintained as parameter of the Display Promo Message action
		final ProductConfigPromoMessageRAO displayPromoMessageAction = (ProductConfigPromoMessageRAO) action;
		final AbstractRuleEngineRuleModel rule = getRule(action);
		final String code = rule.getCode();
		final String params = rule.getRuleParameters();

		final String messageText = displayPromoMessageAction.getMessage();
		final String localizedMessageText = extractLocalizedMessageText(messageText);
		final String messageTextResolved = resolveMessage(localizedMessageText, code, params);

		final String extendedMessageText = displayPromoMessageAction.getExtendedMessage();
		final String localizedExtendedMessageText = extractLocalizedMessageText(extendedMessageText);
		final String extendedMessageTextResolved = resolveMessage(localizedExtendedMessageText, code, params);

		if (StringUtils.isNotEmpty(messageTextResolved))
		{
			final CsticRAO appliedToObject = (CsticRAO) displayPromoMessageAction.getAppliedToObject();
			final String csticName = appliedToObject.getCsticName();

			final String csticValueName = displayPromoMessageAction.getValueName().getCsticValueName();
			final Date endDate = rule.getValidUntilDate();
			final ProductConfigMessagePromoType promoType = displayPromoMessageAction.getPromoType();

			final ProductConfigMessage productConfigMessage = createPromoMessageBuilder().appendKey(code)
					.appendMessage(messageTextResolved).appendPromotionFields(promoType, extendedMessageTextResolved, endDate).build();

			logPromoMessageData(rule, csticName, csticValueName, productConfigMessage);
			showMessageForAllOccurrences(model, rule, csticName, csticValueName, productConfigMessage);
			afterMessageCreated(model.getId(), csticName, csticValueName, productConfigMessage);
		}
		return false;
	}

	protected void afterMessageCreated(final String configId, final String csticName, final String csticValueName,
			final ProductConfigMessage productConfigMessage)
	{
		// hook for subclasses to overwrite
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

	protected ProductConfigMessageBuilder createPromoMessageBuilder()
	{
		return getConfigModelFactory().createProductConfigMessageBuilder().appendSeverity(ProductConfigMessageSeverity.INFO)
				.appendSourceAndType(ProductConfigMessageSource.RULE, ProductConfigMessageSourceSubType.DISPLAY_PROMO_MESSAGE);
	}


	protected void showMessageForAllOccurrences(final ConfigModel model, final AbstractRuleEngineRuleModel rule,
			final String ruleMessageForCstic, final String ruleMessageForCsticValue, final ProductConfigMessage productConfigMessage)

	{
		if (StringUtils.isNotEmpty(ruleMessageForCstic))
		{
			final String ruleMessageForCsticUpperCase = ruleMessageForCstic.toUpperCase(Locale.ENGLISH);
			final List<CsticModel> cstics = getRuleUtil().getCsticsForCsticName(model, ruleMessageForCsticUpperCase);

			if (!cstics.isEmpty())
			{
				final String code = rule.getCode();

				for (final CsticModel cstic : cstics)
				{
					final CsticValueModel csticValue = retrieveCsticValueModelForRuleMessage(model, ruleMessageForCsticValue, code,
							cstic, ruleMessageForCsticUpperCase);
					addMessageToConfigModel(model, ruleMessageForCstic, ruleMessageForCsticValue, productConfigMessage, cstic,
							csticValue);
				}
			}
		}
	}

	protected void logPromoMessageData(final AbstractRuleEngineRuleModel rule, final String csticName, final String csticValueName,
			final ProductConfigMessage productConfigMessage)
	{
		if (LOG.isDebugEnabled())
		{
			final String msgTempl = "Creating Rule Promo Message with data: [ruleCode='%s', csticName='%s', csticValueName='%s', msgText='%s', extendedMsgText='%s', ruleParams='%s' severity='%s', ruleEndDate='%s']";
			LOG.debug(String.format(msgTempl, rule.getCode(), csticName, csticValueName, productConfigMessage.getMessage(),
					productConfigMessage.getExtendedMessage(), rule.getRuleParameters(), productConfigMessage.getSeverity(),
					rule.getValidUntilDate()));
		}
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
