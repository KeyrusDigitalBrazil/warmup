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
package de.hybris.platform.sap.ysapomsfulfillment.actions.order;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.task.RetryLaterException;

/**
 * Cancel multiple SAP OMS orders
 */
public class SapCancelOrderAction extends SapOmsAbstractAction<OrderProcessModel> {

	@Override
	public String execute(OrderProcessModel process) throws Exception {

		final OrderModel order = process.getOrder();

		if (isCancellationComplete(process)) {

			setOrderStatus(order, OrderStatus.CANCELLED);
			
			order.getConsignments().stream().forEach(consignment -> {
				consignment.setStatus(ConsignmentStatus.CANCELLED);
				getModelService().save(consignment);
			});

			return Transition.OK.toString();

		} else {

			return Transition.WAIT.toString();

		}

	}

	protected boolean isCancellationComplete(OrderProcessModel process) {

		boolean sapOrdersCancelled = process.getOrder().getSapOrders()//
				.stream()//
				.allMatch(sapOrder -> sapOrder.getSapOrderStatus().equals(SAPOrderStatus.CANCELLED_FROM_ERP));

		boolean allSapConsignmentsCancelled = process
				.getSapConsignmentProcesses()
				.stream()
				.allMatch(
						sapConsignmentProcess -> sapConsignmentProcess.getConsignment().getStatus()
								.equals(ConsignmentStatus.CANCELLED));

		return sapOrdersCancelled && allSapConsignmentsCancelled;

	}

}
