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
package de.hybris.platform.sap.productconfig.rules.rule.evaluation.impl;

import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleActionContext;
import de.hybris.platform.sap.productconfig.rules.definitions.ProductConfigRuleDisplayMessageSeverity;
import de.hybris.platform.sap.productconfig.rules.enums.ProductConfigRuleMessageSeverity;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.action.ProductConfigDisplayMessageRAO;

import java.util.Map;


/**
 * Encapsulates display message logic as rule action.
 */
public class DisplayMessageRAOAction extends ProductConfigAbstractRAOAction
{
	/**
	 * Message Severity used in the DisplayMessage action definition.
	 */
	protected static final String MESSAGE_SEVERITY = "message_severity";

	@Override
	public void performAction(final RuleActionContext context)
	{
		final Map<String, Object> parameters = context.getParameters();

		validateRuleAndLog(context, parameters, CSTIC_NAME, CSTIC_VALUE, MESSAGE, MESSAGE_SEVERITY);

		if (validateProcessStep(context, parameters, ProcessStep.CREATE_DEFAULT_CONFIGURATION, ProcessStep.RETRIEVE_CONFIGURATION)
				&& validateAllowedByRuntime(context))
		{
			final ProductConfigDisplayMessageRAO displayMessageRao = new ProductConfigDisplayMessageRAO();
			final CsticRAO csticRao = createCsticRAO(parameters);
			final CsticValueRAO valueRao = createCsticValueRAO(parameters);

			final String messageText = (String) parameters.get(MESSAGE);
			final ProductConfigRuleDisplayMessageSeverity displayMessageSeverity = (ProductConfigRuleDisplayMessageSeverity) parameters
					.get(MESSAGE_SEVERITY);
			final ProductConfigRuleMessageSeverity messageSeverity = convertMessageSeverity(displayMessageSeverity);
			displayMessageRao.setAppliedToObject(csticRao);
			displayMessageRao.setMessage(messageText);
			displayMessageRao.setMessageSeverity(messageSeverity);
			displayMessageRao.setValueNameForMessage(valueRao);
			updateContext(context, displayMessageRao);
		}
	}

	protected ProductConfigRuleMessageSeverity convertMessageSeverity(
			final ProductConfigRuleDisplayMessageSeverity displayMessageSeverity)
	{
		ProductConfigRuleMessageSeverity messageSeverity;

		if (displayMessageSeverity != null)
		{
			switch (displayMessageSeverity)
			{
				case WARNING:
					messageSeverity = ProductConfigRuleMessageSeverity.WARNING;
					break;
				case INFO:
					messageSeverity = ProductConfigRuleMessageSeverity.INFO;
					break;
				default:
					messageSeverity = ProductConfigRuleMessageSeverity.INFO;
					break;
			}
		}
		else
		{
			messageSeverity = ProductConfigRuleMessageSeverity.INFO;
		}
		return messageSeverity;

	}

	@Override
	protected String prepareActionLogText(final RuleActionContext context, final Map<String, Object> parameters)
	{
		final String csticName = (String) parameters.get(CSTIC_NAME);
		final String csticValue = (String) parameters.get(CSTIC_VALUE);
		final String messageText = (String) parameters.get(MESSAGE);
		final ProductConfigRuleDisplayMessageSeverity messageSeverity = (ProductConfigRuleDisplayMessageSeverity) parameters
				.get(MESSAGE_SEVERITY);
		return "Hence skipping display message \"" + messageText + "\" with severity " + messageSeverity + " for characteristic "
				+ csticName + " and value " + csticValue;
	}

}
