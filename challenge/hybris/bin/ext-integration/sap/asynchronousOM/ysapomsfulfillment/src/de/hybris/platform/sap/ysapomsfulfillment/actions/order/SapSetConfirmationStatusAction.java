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

import de.hybris.platform.core.enums.DeliveryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;


/**
 * Class for setting the Hybris order status after receiving an order
 * confirmation from the SAP ERP backend .
 * 
 */
public class SapSetConfirmationStatusAction extends SapOmsAbstractAction<OrderProcessModel> {

	@Override
	public final String execute(final OrderProcessModel process) throws Exception {

		final OrderModel order = process.getOrder();


		boolean sapOrdersConfirmed = order.getSapOrders()//
				.stream()//
				.allMatch(sapOrder -> sapOrder.getSapOrderStatus().equals(SAPOrderStatus.CONFIRMED_FROM_ERP));

		if (sapOrdersConfirmed) {

			order.setDeliveryStatus(DeliveryStatus.NOTSHIPPED);
			setOrderStatus(order, OrderStatus.CREATED);
			getModelService().save(order);

			return Transition.OK.toString();

		} else {

			return Transition.WAIT.toString();

		}

	}
	
}
