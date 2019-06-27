/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.yacceleratorordermanagement.actions.consignment;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.warehousing.externalfulfillment.strategy.SendConsignmentToExternalFulfillmentSystemStrategy;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Extracts a {@link SendConsignmentToExternalFulfillmentSystemStrategy}, based on {@link ConsignmentModel#FULFILLMENTSYSTEMCONFIG}.</br>
 * And uses this strategy to send the {@link ConsignmentModel} to external fulfillment system.
 */
public class SendConsignmentToExternalFulfillmentSystemAction extends AbstractAction<ConsignmentProcessModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(SendConsignmentToExternalFulfillmentSystemAction.class);
	private Map<String, SendConsignmentToExternalFulfillmentSystemStrategy> sendConsignmentToExternalFulfillmentSystemStrategyMap;

	@Override
	public String execute(final ConsignmentProcessModel consignmentProcess)
	{
		LOG.info("Process: {} in step {}", consignmentProcess.getCode(), getClass().getSimpleName());
		String transition = Transition.OK.toString();

		final ConsignmentModel consignment = consignmentProcess.getConsignment();
		final String configClassName = consignment.getFulfillmentSystemConfig().getClass().getSimpleName();
		final SendConsignmentToExternalFulfillmentSystemStrategy systemStrategy = getSendConsignmentToExternalFulfillmentSystemStrategyMap()
				.get(configClassName);
		if (systemStrategy != null)
		{
			LOG.debug(
					"Executing: [" + systemStrategy.getClass().getSimpleName() + "] to send the consignment: [" + consignment.getCode()
							+ "]!");
			systemStrategy.sendConsignment(consignment);
		}
		else
		{
			LOG.info(
					"No SendConsignmentToExternalFulfillmentSystemStrategy found for the config [{}]. Moving the consignment [{}] to Cancelled.",
					configClassName, consignment.getCode());
			consignment.setStatus(ConsignmentStatus.CANCELLED);
			getModelService().save(consignment);
			transition = Transition.ERROR.toString();
		}
		LOG.debug("Process: {} transitions to {}", consignmentProcess.getCode(), transition);
		return transition;
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

	protected Map<String, SendConsignmentToExternalFulfillmentSystemStrategy> getSendConsignmentToExternalFulfillmentSystemStrategyMap()
	{
		return sendConsignmentToExternalFulfillmentSystemStrategyMap;
	}

	@Required
	public void setSendConsignmentToExternalFulfillmentSystemStrategyMap(
			final Map<String, SendConsignmentToExternalFulfillmentSystemStrategy> sendConsignmentToExternalFulfillmentSystemStrategyMap)
	{
		this.sendConsignmentToExternalFulfillmentSystemStrategyMap = sendConsignmentToExternalFulfillmentSystemStrategyMap;
	}

	protected enum Transition
	{
		OK, ERROR;

		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<>();

			for (final Transition transition : Transition.values())
			{
				res.add(transition.toString());
			}
			return res;
		}
	}
}
