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

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.warehousing.onhold.service.OrderOnHoldService;

import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Action node responsible for putting an {@link OrderModel} on hold, meaning all of its {@link de.hybris.platform.ordersplitting.model.ConsignmentModel}
 * and associated task assignment workflows are cancelled.
 */
public class PutOrderOnHoldAction extends AbstractProceduralAction<OrderProcessModel>
{
	private OrderOnHoldService orderOnHoldService;

	@Override
	public void executeAction(final OrderProcessModel orderProcess) throws RetryLaterException, Exception
	{
		validateParameterNotNullStandardMessage("Order process model", orderProcess);

		final OrderModel order = orderProcess.getOrder();

		getOrderOnHoldService().processOrderOnHold(order);
		order.setStatus(OrderStatus.ON_HOLD);
		getModelService().save(order);
	}

	protected OrderOnHoldService getOrderOnHoldService()
	{
		return orderOnHoldService;
	}

	@Required
	public void setOrderOnHoldService(final OrderOnHoldService orderOnHoldService)
	{
		this.orderOnHoldService = orderOnHoldService;
	}
}
