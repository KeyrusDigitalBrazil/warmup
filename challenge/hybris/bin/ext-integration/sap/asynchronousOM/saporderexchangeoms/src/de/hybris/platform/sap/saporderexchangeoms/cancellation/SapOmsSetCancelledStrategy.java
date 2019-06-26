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
package de.hybris.platform.sap.saporderexchangeoms.cancellation;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.impl.orderstatechangingstrategies.SetCancellledStrategy;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;


public class SapOmsSetCancelledStrategy extends SetCancellledStrategy

{

	@Override
	public void changeOrderStatusAfterCancelOperation(final OrderCancelRecordEntryModel orderCancelRecordEntry,
			final boolean saveOrderModel)
	{

		final OrderModel order = orderCancelRecordEntry.getModificationRecord().getOrder();

		if (isOrderFullyCancelled(order))
		{
			order.setStatus(OrderStatus.CANCELLED);
			order.setStatusInfo(OrderStatus.CANCELLED.toString());
		}

		if (!(saveOrderModel))
		{
			return;
		}
		getModelService().save(order);

	}


	protected boolean isOrderFullyCancelled(final OrderModel order)
	{
		return order.getSapOrders().stream()
				.allMatch(sapOrder -> sapOrder.getSapOrderStatus().equals(SAPOrderStatus.CANCELLED_FROM_ERP));
	}


}
