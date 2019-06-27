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
package de.hybris.platform.yacceleratorordermanagement.actions.order;

import de.hybris.platform.core.enums.DeliveryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Verifies whether order is cancelled completely or all the consignment processes are complete or not and updates the Order status/delivery status to
 * reflect this.
 */
public class VerifyOrderCompletionAction extends AbstractAction<OrderProcessModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(VerifyOrderCompletionAction.class);

	@Override
	public String execute(final OrderProcessModel process)
	{
		LOG.info("Process: {} in step {}", process.getCode(), getClass().getSimpleName());
		LOG.debug("Process: {} is checking for order cancellation and {} subprocess results", process.getCode(),
				process.getConsignmentProcesses().size());

		final OrderModel order = process.getOrder();

		final boolean someEntriesShipped = order.getEntries().stream()
				.anyMatch(entry -> ((OrderEntryModel) entry).getQuantityShipped().longValue() > 0);
		if (!someEntriesShipped)
		{
			order.setDeliveryStatus(DeliveryStatus.NOTSHIPPED);
		}
		else
		{
			final boolean someEntriesWaiting = order.getEntries().stream()
					.anyMatch(entry -> ((OrderEntryModel) entry).getQuantityPending().longValue() > 0);
			if (someEntriesWaiting)
			{
				order.setDeliveryStatus(DeliveryStatus.PARTSHIPPED);
			}
			else
			{
				order.setDeliveryStatus(DeliveryStatus.SHIPPED);
			}
		}
		save(order);

		final boolean isOrderCancelledCompletely = process.getOrder().getEntries().stream()
				.allMatch(entry -> entry.getQuantity().longValue() == 0);
		if (isOrderCancelledCompletely)
		{
			process.getOrder().setStatus(OrderStatus.CANCELLED);
			getModelService().save(process.getOrder());
			return Transition.CANCELLED.toString();
		}

		final boolean isOrderEntryNotAllocated = order.getEntries().stream()
				.anyMatch(entry -> ((OrderEntryModel) entry).getQuantityUnallocated().longValue() > 0);
		if (isOrderEntryNotAllocated)
		{
			return Transition.WAIT.toString();
		}

		for (final ConsignmentProcessModel subProcess : process.getConsignmentProcesses())
		{
			if (!subProcess.isDone())
			{
				LOG.debug(
						"Process: {} found subprocess {} incomplete -> wait again!", process.getCode(), subProcess.getCode());
				return Transition.WAIT.toString();
			}
			LOG.debug("Process: {} found subprocess {} complete ...", process.getCode(), subProcess.getCode());
		}

		order.setStatus(OrderStatus.READY);
		save(order);
		return Transition.OK.toString();
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

	protected enum Transition
	{
		OK, CANCELLED, WAIT;

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
