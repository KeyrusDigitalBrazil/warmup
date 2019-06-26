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
package de.hybris.platform.yacceleratorordermanagement.actions.order.fraudcheck;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.yacceleratorordermanagement.actions.order.AbstractOrderAction;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Process decision from customer support agent to determine if an order is fraudulent or not.
 */
public class OrderManualCheckedAction extends AbstractOrderAction<OrderProcessModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(OrderManualCheckedAction.class);

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

	@Override
	public final String execute(final OrderProcessModel process) throws RetryLaterException, Exception
	{
		return executeAction(process).toString();
	}

	protected Transition executeAction(final OrderProcessModel process)
	{
		validateParameterNotNullStandardMessage("process", process);
		LOG.info("Process: {} in step {}", process.getCode(), getClass().getSimpleName());

		final OrderModel order = process.getOrder();
		ServicesUtil.validateParameterNotNull(order, "Order in process cannot be null");

		//If order is cancelled, moving the order process to end state
		if (OrderStatus.CANCELLED.equals(order.getStatus()))
		{
			return Transition.CANCELLED;
		}

		ServicesUtil.validateParameterNotNull(order.getFraudulent(), "Fraudulent value in order cannot be null");

		final OrderHistoryEntryModel historyLog = createHistoryLog(
				"Order Manually checked by CSA - Fraud = " + order.getFraudulent(), order);
		modelService.save(historyLog);

		LOG.info("The fraud condition of the order {} is {}", order.getCode(), order.getFraudulent());
		if (order.getFraudulent())
		{
			order.setStatus(OrderStatus.SUSPENDED);
			getModelService().save(order);
			return Transition.NOK;
		}
		else
		{
			order.setStatus(OrderStatus.FRAUD_CHECKED);
			getModelService().save(order);
			return Transition.OK;
		}
	}

	protected enum Transition
	{
		OK, NOK, CANCELLED;

		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<String>();
			for (final Transition transitions : Transition.values())
			{
				res.add(transitions.toString());
			}
			return res;
		}
	}
}
