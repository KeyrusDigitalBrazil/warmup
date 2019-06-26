/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.chinesepaymentservices.order.service;

import de.hybris.platform.core.model.order.OrderModel;

/**
 * The service of ChineseOrder
 */
public interface ChineseOrderService {
	/**
	 * Cancel the order
	 *
	 * @param code
	 *            The code of the order
	 */
	void cancelOrder(final String code);

	/**
	 * Call back for refund service to update order status
	 *
	 * @param orderModel
	 *            The order model to be updated
	 * @param refundSucceed
	 *            True if the order has been refunded successfully and false
	 *            otherwise
	 */
	void updateOrderForRefund(final OrderModel orderModel, boolean refundSucceed);
}
