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
package de.hybris.platform.yacceleratorordermanagement.actions.order.cancel;

import com.google.common.collect.Lists;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordercancel.OrderCancelService;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.task.RetryLaterException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;
import java.util.stream.Collectors;


/**
 * Cancel the order. This order cancellation assumes that no items have been allocated and so no consignment exist for
 * this order yet.
 */
public class CancelOrderAction extends AbstractProceduralAction<OrderProcessModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(CancelOrderAction.class);

	private OrderCancelService orderCancelService;
	private UserService userService;

	@Override
	public void executeAction(final OrderProcessModel orderProcess) throws RetryLaterException, Exception
	{
		ServicesUtil.validateParameterNotNull(orderProcess, "Process can not be null");
		LOG.info("Process: {} in step {}", orderProcess.getCode(), getClass().getSimpleName());

		final OrderModel order = orderProcess.getOrder();
		ServicesUtil.validateParameterNotNull(order, "Order can not be null");

		final OrderCancelRequest orderCancelRequest = new OrderCancelRequest(order,
				Lists.newArrayList(createCancellationEntries(order)));
		getOrderCancelService().requestOrderCancel(orderCancelRequest, getUserService().getCurrentUser());
		order.setStatus(OrderStatus.CANCELLED);
		getModelService().save(order);
	}

	/**
	 * Create a collection of {@link OrderCancelEntry} from the list of order entries in the order.
	 *
	 * @param order
	 * 		- the order
	 * @return collection of cancellation entries; never <tt>null</tt>
	 */
	public Collection<OrderCancelEntry> createCancellationEntries(final OrderModel order)
	{
		return order.getEntries().stream().map(entry -> createCancellationEntry(entry)).collect(Collectors.toList());
	}

	/**
	 * Create a {@link OrderCancelEntry} from an {@link AbstractOrderEntryModel}.
	 *
	 * @param orderEntry
	 * 		- the order entry
	 * @return a new cancellation entry
	 */
	protected OrderCancelEntry createCancellationEntry(final AbstractOrderEntryModel orderEntry)
	{
		final OrderCancelEntry entry = new OrderCancelEntry(orderEntry,
				((OrderEntryModel) orderEntry).getQuantityPending().longValue());
		entry.setCancelReason(CancelReason.OTHER);
		return entry;
	}

	protected OrderCancelService getOrderCancelService()
	{
		return orderCancelService;
	}

	@Required
	public void setOrderCancelService(final OrderCancelService orderCancelService)
	{
		this.orderCancelService = orderCancelService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

}
