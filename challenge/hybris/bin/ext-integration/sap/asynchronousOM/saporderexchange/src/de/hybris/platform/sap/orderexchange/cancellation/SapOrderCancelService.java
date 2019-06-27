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
package de.hybris.platform.sap.orderexchange.cancellation;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelException;


/**
 * This interface provides methods to cancel a hybris order and to restore an order status if cancellation failed
 *
 */
public interface SapOrderCancelService
{

	/**
	 * Cancel a hybris order,
	 *
	 * @param order
	 *           the order to be cancelled
	 * @param erpRejectionReason
	 *           rejection reason coming from ERP
	 * @throws OrderCancelException
	 * 			Exception thrown when cancelling an order fails
	 */
	void cancelOrder(OrderModel order, String erpRejectionReason) throws OrderCancelException;

	/**
	 * restore the order status after a failed cancel request
	 *
	 * @param order
	 * 			the order to be cancelled
	 * @throws OrderCancelException
	 * 			Exception thrown when cancelling an order fails
	 */
	void restoreAfterCancelFailed(OrderModel order) throws OrderCancelException;

}